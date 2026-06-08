import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { getVillage, collectResources, upgradeBuilding } from '../api/villageApi'
import { useGameStore } from '../stores/gameStore'
import type { Village, BuildingType } from '../types'

const BUILDING_INFO: Record<BuildingType, { icon: string; label: string; resource: string }> = {
  SCIERIE: { icon: '🪵', label: 'Scierie', resource: 'Bois' },
  MINE:    { icon: '⛏️',  label: 'Mine',    resource: 'Métal' },
  FERME:   { icon: '🌾', label: 'Ferme',   resource: 'Nourriture' },
}

export default function VillagePage() {
  const { player } = useGameStore()
  const navigate = useNavigate()
  const [village, setVillage] = useState<Village | null>(null)
  const [loading, setLoading] = useState(true)
  const [collecting, setCollecting] = useState(false)
  const [upgrading, setUpgrading] = useState<BuildingType | null>(null)
  const [feedback, setFeedback] = useState('')

  const load = useCallback(async () => {
    if (!player) return
    setLoading(true)
    try {
      setVillage(await getVillage(player.id))
    } finally {
      setLoading(false)
    }
  }, [player])

  useEffect(() => {
    if (!player) { navigate('/'); return }
    load()
  }, [player, navigate, load])

  const handleCollect = async () => {
    if (!player) return
    setCollecting(true)
    setFeedback('')
    try {
      setVillage(await collectResources(player.id))
      setFeedback('Ressources collectées !')
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setCollecting(false)
    }
  }

  const handleUpgrade = async (type: BuildingType) => {
    if (!player) return
    setUpgrading(type)
    setFeedback('')
    try {
      setVillage(await upgradeBuilding(player.id, type))
      setFeedback(`${BUILDING_INFO[type].label} améliorée !`)
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setUpgrading(null)
    }
  }

  if (loading) return <p className="text-slate-400">Chargement du village...</p>
  if (!village) return null

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="font-game text-2xl text-amber-500">Votre Village</h1>
        <button className="btn-primary" onClick={handleCollect} disabled={collecting}>
          {collecting ? 'Collecte...' : '📦 Collecter les ressources'}
        </button>
      </div>

      {feedback && (
        <div className="bg-emerald-900/50 border border-emerald-700 rounded px-4 py-2 text-emerald-300 text-sm">
          {feedback}
        </div>
      )}

      {/* Ressources */}
      <div className="grid grid-cols-3 gap-4">
        {[
          { icon: '🪵', label: 'Bois', value: village.wood, color: 'text-amber-300' },
          { icon: '⛏️', label: 'Métal', value: village.metal, color: 'text-slate-300' },
          { icon: '🌾', label: 'Nourriture', value: village.food, color: 'text-emerald-400' },
        ].map((r) => (
          <div key={r.label} className="card text-center">
            <div className="text-3xl mb-1">{r.icon}</div>
            <div className={`text-2xl font-bold ${r.color}`}>{r.value}</div>
            <div className="text-xs text-slate-400">{r.label}</div>
          </div>
        ))}
      </div>

      {/* Bâtiments */}
      <h2 className="font-game text-lg text-slate-300">Bâtiments</h2>
      <div className="grid gap-4 sm:grid-cols-3">
        {village.buildings.map((b) => {
          const info = BUILDING_INFO[b.type]
          return (
            <div key={b.type} className="card">
              <div className="flex items-center gap-2 mb-3">
                <span className="text-2xl">{info.icon}</span>
                <div>
                  <div className="font-semibold text-slate-100">{info.label}</div>
                  <div className="text-xs text-slate-400">Niveau {b.level}{b.maxLevel ? ' (MAX)' : ''}</div>
                </div>
              </div>

              <div className="space-y-1 text-sm mb-4">
                <div className="flex justify-between">
                  <span className="text-slate-400">Production</span>
                  <span className="text-emerald-400">{b.productionPerHour} {info.resource}/h</span>
                </div>
                {!b.maxLevel && (
                  <>
                    <div className="flex justify-between">
                      <span className="text-slate-400">Coût amélioration</span>
                      <span className="text-slate-300">
                        🪵 {b.upgradeCostWood} · ⛏️ {b.upgradeCostMetal}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-400">Production après</span>
                      <span className="text-emerald-300">{b.productionPerHour + (b.type === 'MINE' ? 5 : b.type === 'SCIERIE' ? 10 : 15)} /h</span>
                    </div>
                  </>
                )}
              </div>

              {/* Barre de niveau */}
              <div className="flex gap-1 mb-4">
                {Array.from({ length: 5 }).map((_, i) => (
                  <div key={i} className={`h-1.5 flex-1 rounded-full ${i < b.level ? 'bg-amber-500' : 'bg-slate-700'}`} />
                ))}
              </div>

              <button
                className="btn-primary w-full text-sm"
                disabled={b.maxLevel || upgrading === b.type}
                onClick={() => handleUpgrade(b.type)}
              >
                {b.maxLevel ? 'Niveau maximum' : upgrading === b.type ? 'Amélioration...' : 'Améliorer'}
              </button>
            </div>
          )
        })}
      </div>

      <p className="text-xs text-slate-500 text-center">
        Les ressources s'accumulent avec le temps. Collectez-les régulièrement pour en profiter.
      </p>
    </div>
  )
}
