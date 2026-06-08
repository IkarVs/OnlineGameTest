import client from './client'
import type { Mission, MissionLog } from '../types'

export const getMissions = (): Promise<Mission[]> =>
  client.get<Mission[]>('/missions').then((r) => r.data)

export const runMission = (heroId: number, missionId: number): Promise<MissionLog> =>
  client.post<MissionLog>('/missions/run', { heroId, missionId }).then((r) => r.data)

export const getMissionLogs = (heroId: number): Promise<MissionLog[]> =>
  client.get<MissionLog[]>(`/missions/logs/hero/${heroId}`).then((r) => r.data)
