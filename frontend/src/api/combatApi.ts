import client from './client'
import type { Combat, CodexEntry } from '../types'

export const startCombat = (heroId: number): Promise<Combat> =>
  client.post<Combat>('/combats/start', { heroId }).then((r) => r.data)

export const attack = (combatId: number): Promise<Combat> =>
  client.post<Combat>(`/combats/${combatId}/attack`).then((r) => r.data)

export const useTechnique = (combatId: number, techniqueId: number): Promise<Combat> =>
  client.post<Combat>(`/combats/${combatId}/technique/${techniqueId}`).then((r) => r.data)

export const tryCapture = (combatId: number): Promise<Combat> =>
  client.post<Combat>(`/combats/${combatId}/capture`).then((r) => r.data)

export const flee = (combatId: number): Promise<Combat> =>
  client.post<Combat>(`/combats/${combatId}/flee`).then((r) => r.data)

export const getCodex = (playerId: number): Promise<CodexEntry[]> =>
  client.get<CodexEntry[]>(`/critters/codex/player/${playerId}`).then((r) => r.data)
