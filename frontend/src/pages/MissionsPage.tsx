import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMissions, runMission, getMissionLogs } from '../api/missionApi'
import { getHero } from '../api/heroApi'
import { useGameStore } from '../stores/gameStore'
import type { Mission, MissionLog } from '../types'

const DIFFICULTY_LABEL = ['', '★', '★★', '★★★', '★★★★', '★★★★★']
const DIFFICULTY_COLOR = ['', 'text-emerald-400', 'text-sky-400', 'text-yellow-400', 'text-orange-400', 'text-red-400']

export default function MissionsPage() {
  const { player, hero, setHero } = useGameStore()
  const navigate = useNavigate()

  const [missions, setMissions] = useState<Mission[]>([])
  const [logs, setLogs] = useState<MissionLog[]>([])
  const [running, setRunning] = useState<number | null>(null)
  const [result, setResult] = useState<MissionLog | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!player || !hero) { navigate('/'); return }
    getMissions().then(setMissions)
    getMissionLogs(hero.id).then(setLogs)
  }, [player, hero, navigate])

  const handleRun = async (mission: Mission) => {
    if (!hero) return
    setRunning(mission.id)
    setResult(null)
    setError('')
    try {
      const log = await runMission(hero.id, mission.id)
      setResult(log)
      setLogs((prev) => [log, ...prev])
      // Rafraîchit le héros pour l'XP et le niveau
      const updated = await getHero(hero.id)
      setHero(updated)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur lors de la mission')
    } finally {
      setRunning(null)
    }
  }

  if (!hero) return null

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="font-game text-2xl text-amber-500">Missions</h1>
        <div className="text-sm text-slate-400">
          Héros : <span className="text-amber-400">{hero.name}</span> · Niveau <span className="text-amber-400">{hero.level}</span>
        </div>
      </div>

      {/* Résultat de la dernière mission */}
      {result && (
        <div className="bg-emerald-900/50 border border-emerald-700 rounded-lg p-4">
          <div className="font-semibold text-emerald-300 mb-1">✅ Mission accomplie : {result.mission.name}</div>
          <div className="flex gap-4 text-sm text-slate-300">
            <span>+{result.xpGained} XP</span>
            {result.woodGained > 0 && <span>🪵 +{result.woodGained}</span>}
            {result.metalGained > 0 && <span>⛏️ +{result.metalGained}</span>}
            {result.foodGained > 0 && <span>🌾 +{result.foodGained}</span>}
          </div>
        </div>
      )}

      {error && (
        <div className="bg-red-900/50 border border-red-700 rounded px-4 py-2 text-red-300 text-sm">
          ❌ {error}
        </div>
      )}

      {/* Liste des missions */}
      <div className="grid gap-3 sm:grid-cols-2">
        {missions.map((m) => {
          const canDo = hero.level >= m.requiredLevel
          return (
            <div key={m.id} className={`card transition-all ${!canDo ? 'opacity-60' : ''}`}>
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-semibold text-slate-100">{m.name}</h3>
                <span className={`font-bold ${DIFFICULTY_COLOR[m.difficulty]}`}>
                  {DIFFICULTY_LABEL[m.difficulty]}
                </span>
              </div>
              <p className="text-sm text-slate-400 mb-3">{m.description}</p>

              <div className="flex flex-wrap gap-2 text-xs text-slate-400 mb-3">
                <span className="bg-slate-700 px-2 py-0.5 rounded">Niv. {m.requiredLevel}+</span>
                <span className="bg-slate-700 px-2 py-0.5 rounded text-amber-300">+{m.xpReward} XP</span>
                {m.woodReward > 0 && <span className="bg-slate-700 px-2 py-0.5 rounded">🪵 +{m.woodReward}</span>}
                {m.metalReward > 0 && <span className="bg-slate-700 px-2 py-0.5 rounded">⛏️ +{m.metalReward}</span>}
                {m.foodReward > 0 && <span className="bg-slate-700 px-2 py-0.5 rounded">🌾 +{m.foodReward}</span>}
              </div>

              <button
                className="btn-primary w-full text-sm"
                disabled={!canDo || running === m.id}
                onClick={() => handleRun(m)}
              >
                {running === m.id ? 'En mission...' : canDo ? '⚔️ Partir en mission' : `Niveau ${m.requiredLevel} requis`}
              </button>
            </div>
          )
        })}
      </div>

      {/* Historique */}
      {logs.length > 0 && (
        <div>
          <h2 className="font-game text-lg text-slate-300 mb-3">Historique récent</h2>
          <div className="space-y-2">
            {logs.slice(0, 10).map((log) => (
              <div key={log.id} className="card flex items-center justify-between py-2">
                <div>
                  <span className="text-sm font-semibold text-slate-200">{log.mission.name}</span>
                  <span className="text-xs text-slate-500 ml-2">
                    {new Date(log.completedAt).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })}
                  </span>
                </div>
                <div className="flex gap-3 text-xs text-slate-400">
                  <span className="text-amber-400">+{log.xpGained} XP</span>
                  {log.woodGained > 0 && <span>🪵 +{log.woodGained}</span>}
                  {log.metalGained > 0 && <span>⛏️ +{log.metalGained}</span>}
                  {log.foodGained > 0 && <span>🌾 +{log.foodGained}</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
