import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/playerApi'
import { getHeroesByPlayer } from '../api/heroApi'
import { useGameStore } from '../stores/gameStore'

export default function HomePage() {
  const [name, setName] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const { setPlayer, setHero } = useGameStore()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    setLoading(true)
    setError('')
    try {
      const player = await login(name.trim())
      setPlayer(player)
      const heroes = await getHeroesByPlayer(player.id)
      if (heroes.length > 0) {
        setHero(heroes[0])
        navigate('/village')
      } else {
        navigate('/character')
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur de connexion')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-900 flex flex-col items-center justify-center px-4">
      <div className="text-center mb-10">
        <h1 className="font-game text-5xl font-bold text-amber-500 mb-3 tracking-widest">
          Chroniques de l'Abîme
        </h1>
        <p className="text-slate-400 text-lg">
          Partez en mission, gérez votre village, forgez votre légende.
        </p>
      </div>

      <div className="card w-full max-w-sm">
        <h2 className="text-xl font-semibold text-slate-100 mb-4 text-center">
          Entrer dans le monde
        </h2>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label className="block text-sm text-slate-400 mb-1">Votre nom d'aventurier</label>
            <input
              className="input"
              type="text"
              placeholder="Ex: Arthas, Gandalf..."
              value={name}
              onChange={(e) => setName(e.target.value)}
              maxLength={30}
              autoFocus
            />
          </div>
          {error && <p className="text-red-400 text-sm">{error}</p>}
          <button type="submit" className="btn-primary" disabled={loading || !name.trim()}>
            {loading ? 'Chargement...' : 'Commencer l\'aventure'}
          </button>
        </form>
        <p className="text-xs text-slate-500 text-center mt-4">
          Si ce nom existe déjà, votre partie sera chargée automatiquement.
        </p>
      </div>

      <div className="mt-10 grid grid-cols-3 gap-6 text-center max-w-lg">
        {[
          { icon: '⚔️', title: 'Combats', desc: 'Partez en mission et gagnez de l\'expérience' },
          { icon: '🏰', title: 'Village', desc: 'Gérez vos ressources et améliorez vos bâtiments' },
          { icon: '🎒', title: 'Équipement', desc: 'Collectez des objets et apprenez des techniques' },
        ].map((f) => (
          <div key={f.title} className="card text-center">
            <div className="text-3xl mb-2">{f.icon}</div>
            <div className="font-semibold text-amber-400 text-sm mb-1">{f.title}</div>
            <div className="text-xs text-slate-400">{f.desc}</div>
          </div>
        ))}
      </div>
    </div>
  )
}
