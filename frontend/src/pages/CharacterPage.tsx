import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { createHero, unlockTechnique } from '../api/heroApi'
import { getTechniques } from '../api/techniqueApi'
import { useGameStore } from '../stores/gameStore'
import XpBar from '../components/XpBar'
import type { HeroClass, Technique } from '../types'

const CLASSES: { value: HeroClass; label: string; icon: string; desc: string }[] = [
  { value: 'GUERRIER', label: 'Guerrier', icon: '⚔️', desc: 'Résistant, fort en combat rapproché' },
  { value: 'MAGE', label: 'Mage', icon: '🔮', desc: 'Magie puissante, fragile physiquement' },
  { value: 'ARCHER', label: 'Archer', icon: '🏹', desc: 'Précis, équilibré entre dégâts et mobilité' },
]

const ELEMENT_COLORS: Record<string, string> = {
  PHYSIQUE: 'bg-slate-600 text-slate-200',
  FEU:      'bg-red-700 text-red-100',
  GLACE:    'bg-sky-700 text-sky-100',
  FOUDRE:   'bg-yellow-600 text-yellow-100',
  LUMIERE:  'bg-yellow-200 text-yellow-900',
  OMBRE:    'bg-purple-700 text-purple-100',
}

export default function CharacterPage() {
  const { player, hero, setHero } = useGameStore()
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [heroClass, setHeroClass] = useState<HeroClass>('GUERRIER')
  const [creating, setCreating] = useState(false)
  const [error, setError] = useState('')

  const [techniques, setTechniques] = useState<Technique[]>([])
  const [unlocking, setUnlocking] = useState<number | null>(null)
  const [feedback, setFeedback] = useState('')

  useEffect(() => {
    if (!player) { navigate('/'); return }
    getTechniques().then(setTechniques)
  }, [player, navigate])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!player || !name.trim()) return
    setCreating(true)
    setError('')
    try {
      const newHero = await createHero(name.trim(), heroClass, player.id)
      setHero(newHero)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur lors de la création')
    } finally {
      setCreating(false)
    }
  }

  const handleUnlock = async (techniqueId: number) => {
    if (!hero) return
    setUnlocking(techniqueId)
    setFeedback('')
    try {
      const updated = await unlockTechnique(hero.id, techniqueId)
      setHero(updated)
      setFeedback('Technique apprise !')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setUnlocking(null)
    }
  }

  if (!player) return null

  if (!hero) {
    return (
      <div className="max-w-lg mx-auto">
        <h1 className="font-game text-2xl text-amber-500 mb-6">Créer votre héros</h1>
        <form onSubmit={handleCreate} className="card flex flex-col gap-5">
          <div>
            <label className="block text-sm text-slate-400 mb-1">Nom du héros</label>
            <input className="input" value={name} onChange={(e) => setName(e.target.value)}
              placeholder="Nom de votre héros" maxLength={30} autoFocus />
          </div>
          <div>
            <label className="block text-sm text-slate-400 mb-2">Classe</label>
            <div className="grid grid-cols-3 gap-3">
              {CLASSES.map((c) => (
                <button
                  key={c.value}
                  type="button"
                  onClick={() => setHeroClass(c.value)}
                  className={`card text-center cursor-pointer transition-all ${
                    heroClass === c.value ? 'border-amber-500 bg-slate-700' : 'hover:border-slate-500'
                  }`}
                >
                  <div className="text-2xl mb-1">{c.icon}</div>
                  <div className="text-sm font-semibold text-slate-100">{c.label}</div>
                  <div className="text-xs text-slate-400 mt-1">{c.desc}</div>
                </button>
              ))}
            </div>
          </div>
          {error && <p className="text-red-400 text-sm">{error}</p>}
          <button type="submit" className="btn-primary" disabled={creating || !name.trim()}>
            {creating ? 'Création...' : 'Créer le héros'}
          </button>
        </form>
      </div>
    )
  }

  const classInfo = CLASSES.find((c) => c.value === hero.heroClass)
  const knownIds = new Set(hero.unlockedTechniques.map((t) => t.id))
  const available = techniques.filter((t) => !knownIds.has(t.id))

  return (
    <div className="space-y-6">
      <h1 className="font-game text-2xl text-amber-500">Fiche de héros</h1>

      {/* Stats */}
      <div className="card">
        <div className="flex items-center gap-3 mb-4">
          <span className="text-3xl">{classInfo?.icon}</span>
          <div>
            <h2 className="text-xl font-bold text-slate-100">{hero.name}</h2>
            <span className="text-sm text-slate-400">{classInfo?.label} · Niveau {hero.level}</span>
          </div>
        </div>
        <div className="mb-4">
          <XpBar experience={hero.experience} xpToNextLevel={hero.xpToNextLevel} />
        </div>
        <div className="grid grid-cols-3 gap-4">
          {[
            { label: '❤️ PV', value: hero.totalHp },
            { label: '⚔️ Attaque', value: hero.totalAttack },
            { label: '🛡️ Défense', value: hero.totalDefense },
          ].map((s) => (
            <div key={s.label} className="bg-slate-700 rounded p-3 text-center">
              <div className="text-lg font-bold text-amber-400">{s.value}</div>
              <div className="text-xs text-slate-400">{s.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Équipement actuel */}
      <div className="card">
        <h3 className="font-semibold text-slate-100 mb-3">Équipement actuel</h3>
        {Object.keys(hero.equipment).length === 0 ? (
          <p className="text-slate-500 text-sm">Aucun équipement. Rendez-vous dans l'inventaire.</p>
        ) : (
          <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
            {Object.entries(hero.equipment).map(([slot, item]) => (
              <div key={slot} className="bg-slate-700 rounded p-2 text-sm">
                <div className="text-xs text-slate-500 mb-0.5">{slot}</div>
                <div className="font-semibold text-amber-400">{item.itemName}</div>
                <div className="text-xs text-slate-400">
                  {item.attackBonus > 0 && `+${item.attackBonus} ATK `}
                  {item.defenseBonus > 0 && `+${item.defenseBonus} DEF `}
                  {item.hpBonus > 0 && `+${item.hpBonus} PV`}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Techniques apprises */}
      <div className="card">
        <h3 className="font-semibold text-slate-100 mb-3">Techniques maîtrisées</h3>
        {hero.unlockedTechniques.length === 0 ? (
          <p className="text-slate-500 text-sm">Aucune technique. Apprenez-en ci-dessous.</p>
        ) : (
          <div className="grid gap-2 sm:grid-cols-2">
            {hero.unlockedTechniques.map((t) => (
              <div key={t.id} className="bg-slate-700 rounded p-3">
                <div className="flex items-center gap-2 mb-1">
                  <span className="font-semibold text-slate-100">{t.name}</span>
                  <span className={`badge ${ELEMENT_COLORS[t.element] ?? 'bg-slate-600'}`}>{t.element}</span>
                </div>
                <div className="text-xs text-slate-400">{t.description}</div>
                <div className="text-xs text-slate-500 mt-1">{t.damage > 0 ? `${t.damage} dmg · ` : ''}{t.manaCost} mana</div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Techniques disponibles */}
      <div className="card">
        <h3 className="font-semibold text-slate-100 mb-1">Techniques à apprendre</h3>
        {feedback && <p className="text-sm text-emerald-400 mb-3">{feedback}</p>}
        <div className="grid gap-2 sm:grid-cols-2">
          {available.map((t) => {
            const canLearn = hero.level >= t.requiredLevel
            return (
              <div key={t.id} className={`bg-slate-700 rounded p-3 ${!canLearn ? 'opacity-60' : ''}`}>
                <div className="flex items-center justify-between mb-1">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-slate-100">{t.name}</span>
                    <span className={`badge ${ELEMENT_COLORS[t.element] ?? 'bg-slate-600'}`}>{t.element}</span>
                  </div>
                  <span className="text-xs text-slate-500">niv.{t.requiredLevel}</span>
                </div>
                <div className="text-xs text-slate-400 mb-2">{t.description}</div>
                <button
                  className="btn-primary text-xs py-1 px-3"
                  disabled={!canLearn || unlocking === t.id}
                  onClick={() => handleUnlock(t.id)}
                >
                  {unlocking === t.id ? '...' : canLearn ? 'Apprendre' : `Niveau ${t.requiredLevel} requis`}
                </button>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
