import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { startCombat, attack, useTechnique, tryCapture, flee } from '../api/combatApi'
import { getHero } from '../api/heroApi'
import { useGameStore } from '../stores/gameStore'
import type { Combat } from '../types'

const ELEMENT_COLORS: Record<string, string> = {
  ABIME:  'bg-indigo-800 text-indigo-200',
  OMBRE:  'bg-slate-600 text-slate-200',
  REVE:   'bg-pink-800 text-pink-200',
  OCEAN:  'bg-cyan-800 text-cyan-200',
  CHAOS:  'bg-red-800 text-red-200',
  COSMOS: 'bg-violet-800 text-violet-200',
}

function HpBar({ current, max, color }: { current: number; max: number; color: string }) {
  const pct = Math.max(0, Math.round((current / max) * 100))
  const barColor = pct > 50 ? color : pct > 20 ? 'bg-amber-500' : 'bg-red-600'
  return (
    <div>
      <div className="flex justify-between text-xs text-slate-400 mb-1">
        <span>PV</span>
        <span>{current} / {max}</span>
      </div>
      <div className="h-3 bg-slate-700 rounded-full overflow-hidden">
        <div className={`h-full ${barColor} rounded-full transition-all duration-500`} style={{ width: `${pct}%` }} />
      </div>
    </div>
  )
}

export default function CombatPage() {
  const { player, hero, setHero } = useGameStore()
  const navigate = useNavigate()

  const [combat, setCombat] = useState<Combat | null>(null)
  const [fullLog, setFullLog] = useState<string[]>([])
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [showTechniques, setShowTechniques] = useState(false)
  const logRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!player || !hero) navigate('/')
  }, [player, hero, navigate])

  useEffect(() => {
    logRef.current?.scrollTo({ top: logRef.current.scrollHeight, behavior: 'smooth' })
  }, [fullLog])

  const applyResult = async (result: Combat) => {
    setCombat(result)
    setFullLog((prev) => [...prev, ...result.log])
    setShowTechniques(false)
    // Combat terminé par victoire → rafraîchit l'XP du héros
    if (result.status === 'WON' && hero) {
      const updated = await getHero(hero.id)
      setHero(updated)
    }
  }

  const doAction = async (fn: () => Promise<Combat>) => {
    setBusy(true)
    setError('')
    try {
      await applyResult(await fn())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setBusy(false)
    }
  }

  const handleStart = () => {
    if (!hero) return
    setFullLog([])
    doAction(() => startCombat(hero.id))
  }

  if (!hero) return null

  const isOver = combat !== null && combat.status !== 'ACTIVE'
  const knownTechniques = hero.unlockedTechniques ?? []

  // ─── Écran d'accueil (pas de combat) ───────────────────────────────
  if (!combat) {
    return (
      <div className="max-w-lg mx-auto text-center space-y-6 pt-10">
        <h1 className="font-game text-3xl text-amber-500">Terres Sauvages</h1>
        <p className="text-slate-400">
          Des créatures venues d'au-delà des étoiles — les <span className="text-violet-400 font-semibold">God Critters</span> —
          rôdent dans les ténèbres. Affrontez-les, capturez-les, complétez votre Codex.
        </p>
        <div className="text-6xl">🌫️</div>
        <button className="btn-primary text-lg px-8 py-3" onClick={handleStart} disabled={busy}>
          {busy ? 'Recherche...' : '⚔️ Explorer les ténèbres'}
        </button>
        {error && <p className="text-red-400 text-sm">{error}</p>}
        <p className="text-xs text-slate-500">
          Héros : {hero.name} · Niveau {hero.level} · {hero.totalHp} PV
        </p>
      </div>
    )
  }

  // ─── Écran de combat ───────────────────────────────────────────────
  return (
    <div className="max-w-2xl mx-auto space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="font-game text-xl text-amber-500">Combat — Tour {combat.turn}</h1>
        <span className={`badge ${ELEMENT_COLORS[combat.species.element] ?? 'bg-slate-600'}`}>
          {combat.species.element}
        </span>
      </div>

      {/* Zone monstre */}
      <div className="card">
        <div className="flex items-center gap-4">
          <div className={`w-28 h-28 flex-shrink-0 rounded-lg bg-slate-900 border border-slate-700 p-1 ${combat.status === 'WON' || combat.status === 'CAPTURED' ? 'opacity-40 grayscale' : ''}`}>
            <img src={combat.species.spriteUrl} alt={combat.species.name} className="w-full h-full" />
          </div>
          <div className="flex-1">
            <div className="flex items-baseline justify-between mb-2">
              <span className="font-semibold text-slate-100 text-lg">{combat.species.name}</span>
              <span className="text-xs text-slate-500">Niv. {combat.monsterLevel} · #{combat.species.codexNumber}</span>
            </div>
            <HpBar current={combat.monsterHp} max={combat.monsterMaxHp} color="bg-violet-500" />
          </div>
        </div>
      </div>

      {/* Zone héros */}
      <div className="card">
        <div className="flex items-baseline justify-between mb-2">
          <span className="font-semibold text-amber-400">{hero.name}</span>
          <span className="text-xs text-slate-500">Niv. {hero.level} · ATK {hero.totalAttack} · DEF {hero.totalDefense}</span>
        </div>
        <HpBar current={combat.heroHp} max={combat.heroMaxHp} color="bg-emerald-500" />
      </div>

      {/* Journal de combat */}
      <div ref={logRef} className="card h-36 overflow-y-auto text-sm space-y-1">
        {fullLog.map((line, i) => (
          <p key={i} className={
            line.includes('capturé') || line.includes('Codex') ? 'text-violet-300 font-semibold' :
            line.includes('XP') ? 'text-amber-300' :
            line.includes('riposte') || line.includes('inconscience') ? 'text-red-300' :
            'text-slate-300'
          }>
            {line}
          </p>
        ))}
      </div>

      {error && (
        <div className="bg-red-900/50 border border-red-700 rounded px-4 py-2 text-red-300 text-sm">{error}</div>
      )}

      {/* Actions */}
      {!isOver ? (
        <div className="space-y-2">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            <button className="btn-primary" disabled={busy} onClick={() => doAction(() => attack(combat.id))}>
              ⚔️ Attaquer
            </button>
            <button
              className="btn-secondary"
              disabled={busy || knownTechniques.length === 0}
              onClick={() => setShowTechniques((s) => !s)}
            >
              ✨ Technique
            </button>
            <button className="btn-secondary" disabled={busy} onClick={() => doAction(() => tryCapture(combat.id))}>
              🔮 Capturer
            </button>
            <button className="btn-danger" disabled={busy} onClick={() => doAction(() => flee(combat.id))}>
              🏃 Fuir
            </button>
          </div>

          {showTechniques && (
            <div className="card grid gap-2 sm:grid-cols-2">
              {knownTechniques.map((t) => (
                <button
                  key={t.id}
                  className="btn-secondary text-left text-sm"
                  disabled={busy}
                  onClick={() => doAction(() => useTechnique(combat.id, t.id))}
                >
                  <span className="font-semibold">{t.name}</span>
                  <span className="text-xs text-slate-400 block">{t.damage} dégâts bonus · {t.element}</span>
                </button>
              ))}
            </div>
          )}

          <p className="text-xs text-slate-500 text-center">
            💡 Plus la créature est affaiblie, plus la capture a de chances de réussir.
          </p>
        </div>
      ) : (
        <div className="card text-center space-y-3">
          <div className="text-3xl">
            {combat.status === 'WON' && '🏆'}
            {combat.status === 'CAPTURED' && '🔮'}
            {combat.status === 'LOST' && '💀'}
            {combat.status === 'FLED' && '🏃'}
          </div>
          <p className="font-semibold text-slate-100">
            {combat.status === 'WON' && 'Victoire ! La créature retourne au néant.'}
            {combat.status === 'CAPTURED' && `${combat.species.name} rejoint votre Codex !`}
            {combat.status === 'LOST' && 'Défaite... Vous vous réveillez au village.'}
            {combat.status === 'FLED' && 'Vous avez fui le combat.'}
          </p>
          <div className="flex gap-3 justify-center">
            <button className="btn-primary" onClick={handleStart} disabled={busy}>
              ⚔️ Nouveau combat
            </button>
            {combat.status === 'CAPTURED' && (
              <button className="btn-secondary" onClick={() => navigate('/codex')}>
                📖 Voir le Codex
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
