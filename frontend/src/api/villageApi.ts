import client from './client'
import type { Village, BuildingType } from '../types'

export const getVillage = (playerId: number): Promise<Village> =>
  client.get<Village>(`/villages/player/${playerId}`).then((r) => r.data)

export const collectResources = (playerId: number): Promise<Village> =>
  client.post<Village>(`/villages/player/${playerId}/collect`).then((r) => r.data)

export const upgradeBuilding = (playerId: number, buildingType: BuildingType): Promise<Village> =>
  client.post<Village>(`/villages/player/${playerId}/buildings/${buildingType}/upgrade`).then((r) => r.data)
