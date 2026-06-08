import client from './client'
import type { Hero, HeroClass } from '../types'

export const getHeroesByPlayer = (playerId: number): Promise<Hero[]> =>
  client.get<Hero[]>(`/heroes/player/${playerId}`).then((r) => r.data)

export const getHero = (heroId: number): Promise<Hero> =>
  client.get<Hero>(`/heroes/${heroId}`).then((r) => r.data)

export const createHero = (name: string, heroClass: HeroClass, playerId: number): Promise<Hero> =>
  client.post<Hero>('/heroes', { name, heroClass, playerId }).then((r) => r.data)

export const equipItem = (heroId: number, itemId: number): Promise<Hero> =>
  client.post<Hero>(`/heroes/${heroId}/equip`, { itemId }).then((r) => r.data)

export const unequipSlot = (heroId: number, slot: string): Promise<Hero> =>
  client.delete<Hero>(`/heroes/${heroId}/equip/${slot}`).then((r) => r.data)

export const unlockTechnique = (heroId: number, techniqueId: number): Promise<Hero> =>
  client.post<Hero>(`/heroes/${heroId}/techniques/${techniqueId}`).then((r) => r.data)
