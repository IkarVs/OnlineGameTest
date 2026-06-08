import { NavLink, useNavigate } from 'react-router-dom'
import { useGameStore } from '../stores/gameStore'

const links = [
  { to: '/village', label: '🏰 Village' },
  { to: '/character', label: '⚔️ Héros' },
  { to: '/missions', label: '🗺️ Missions' },
  { to: '/inventory', label: '🎒 Inventaire' },
]

export default function Navbar() {
  const { player, hero, logout } = useGameStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className="bg-slate-900 border-b border-slate-700 sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 flex items-center justify-between h-14">
        <span className="font-game text-amber-500 font-bold text-lg tracking-wider">
          Chroniques de l'Abîme
        </span>

        {player && (
          <div className="flex items-center gap-1">
            {links.map((l) => (
              <NavLink
                key={l.to}
                to={l.to}
                className={({ isActive }) =>
                  `px-3 py-1.5 rounded text-sm transition-colors ${
                    isActive
                      ? 'bg-amber-500 text-slate-900 font-semibold'
                      : 'text-slate-300 hover:text-amber-400'
                  }`
                }
              >
                {l.label}
              </NavLink>
            ))}
          </div>
        )}

        <div className="flex items-center gap-3">
          {player && (
            <span className="text-sm text-slate-400">
              <span className="text-amber-400">{player.name}</span>
              {hero && <span className="ml-1 text-slate-500">· {hero.name} niv.{hero.level}</span>}
            </span>
          )}
          {player && (
            <button onClick={handleLogout} className="btn-secondary text-xs py-1 px-3">
              Quitter
            </button>
          )}
        </div>
      </div>
    </nav>
  )
}
