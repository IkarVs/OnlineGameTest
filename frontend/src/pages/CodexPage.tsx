import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCodex } from '../api/combatApi'
import { useGameStore } from '../stores/gameStore'
import type { CodexEntry } from '../types'

const ELEMENT_COLORS: Record<string, string> = {
  ABIME:   'bg-indigo-800 text-indigo-200',
  OMBRE:   'bg-slate-600 text-slate-200',
  REVE:    'bg-pink-800 text-pink-200',
  OCEAN:   'bg-cyan-800 text-cyan-200',
  CHAOS:   'bg-red-800 text-red-200',
  COSMOS:  'bg-violet-800 text-violet-200',
  INCONNU: 'bg-slate-800 text-slate-500',
}

export default function CodexPage() {
  const { player } = useGameStore()
  const navigate = useNavigate()
  const [codex, setCodex] = useState<CodexEntry[]>([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState<CodexEntry | null>(null)

  useEffect(() => {
    if (!player) { navigate('/'); return }
    getCodex(player.id)
      .then(setCodex)
      .finally(() => setLoading(false))
  }, [player, navigate])

  if (loading) return <p className="text-slate-400">Ouverture du Codex...</p>

  const capturedCount = codex.filter((e) => e.captured).length

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="font-game text-2xl text-amber-500">Codex des God Critters</h1>
        <span className="text-sm text-slate-400">
          <span className="text-violet-400 font-bold">{capturedCount}</span> / {codex.length} capturés
        </span>
      </div>

      {/* Barre de progression du codex */}
      <div className="h-2 bg-slate-700 rounded-full overflow-hidden">
        <div
          className="h-full bg-violet-500 rounded-full transition-all"
          style={{ width: `${(capturedCount / Math.max(1, codex.length)) * 100}%` }}
        />
      </div>

      {/* Grille des créatures */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
        {codex.map((entry) => (
          <button
            key={entry.id}
            onClick={() => setSelected(entry)}
            className={`card text-center transition-all hover:border-violet-500 ${
              selected?.id === entry.id ? 'border-violet-500 bg-slate-700' : ''
            }`}
          >
            <div className="text-xs text-slate-500 mb-1">#{String(entry.codexNumber).padStart(2, '0')}</div>
            <div className="w-20 h-20 mx-auto mb-2">
              <img
                src={entry.spriteUrl}
                alt={entry.name}
                className={`w-full h-full ${entry.captured ? '' : 'brightness-0 opacity-30'}`}
              />
            </div>
            <div className={`text-sm font-semibold ${entry.captured ? 'text-slate-100' : 'text-slate-600'}`}>
              {entry.name}
            </div>
            {entry.captured && (
              <span className={`badge mt-1 ${ELEMENT_COLORS[entry.element] ?? 'bg-slate-600'}`}>
                {entry.element}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Détail de la créature sélectionnée */}
      {selected && (
        <div className="card">
          <div className="flex items-start gap-5">
            <div className="w-32 h-32 flex-shrink-0 bg-slate-900 rounded-lg border border-slate-700 p-2">
              <img
                src={selected.spriteUrl}
                alt={selected.name}
                className={`w-full h-full ${selected.captured ? '' : 'brightness-0 opacity-30'}`}
              />
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h2 className="text-xl font-bold text-slate-100">
                  #{String(selected.codexNumber).padStart(2, '0')} — {selected.name}
                </h2>
                <span className={`badge ${ELEMENT_COLORS[selected.element] ?? 'bg-slate-600'}`}>
                  {selected.element}
                </span>
              </div>
              <p className="text-sm text-slate-400 italic mb-3">{selected.description}</p>

              {selected.captured ? (
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
                  <div className="bg-slate-700 rounded p-2 text-center">
                    <div className="font-bold text-emerald-400">{selected.baseHp}</div>
                    <div className="text-xs text-slate-400">PV de base</div>
                  </div>
                  <div className="bg-slate-700 rounded p-2 text-center">
                    <div className="font-bold text-red-400">{selected.baseAttack}</div>
                    <div className="text-xs text-slate-400">Attaque</div>
                  </div>
                  <div className="bg-slate-700 rounded p-2 text-center">
                    <div className="font-bold text-sky-400">{selected.baseDefense}</div>
                    <div className="text-xs text-slate-400">Défense</div>
                  </div>
                  <div className="bg-slate-700 rounded p-2 text-center">
                    <div className="font-bold text-violet-400">{Math.round(selected.captureRate * 100)}%</div>
                    <div className="text-xs text-slate-400">Taux de capture</div>
                  </div>
                </div>
              ) : (
                <p className="text-xs text-slate-500">
                  Rencontre possible à partir du niveau {selected.minHeroLevel}. Capturez cette créature pour révéler ses secrets.
                </p>
              )}
            </div>
          </div>
        </div>
      )}

      <div className="text-center">
        <button className="btn-primary" onClick={() => navigate('/combat')}>
          ⚔️ Partir à la chasse
        </button>
      </div>
    </div>
  )
}
