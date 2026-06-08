import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Player, Hero, Village } from '../types'

interface GameState {
  player: Player | null
  hero: Hero | null
  village: Village | null
  setPlayer: (player: Player) => void
  setHero: (hero: Hero) => void
  setVillage: (village: Village) => void
  logout: () => void
}

export const useGameStore = create<GameState>()(
  persist(
    (set) => ({
      player: null,
      hero: null,
      village: null,
      setPlayer: (player) => set({ player }),
      setHero: (hero) => set({ hero }),
      setVillage: (village) => set({ village }),
      logout: () => set({ player: null, hero: null, village: null }),
    }),
    {
      name: 'game-storage',
      partialize: (state) => ({ player: state.player, hero: state.hero }),
    },
  ),
)
