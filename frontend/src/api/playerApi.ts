import client from './client'
import type { Player } from '../types'

export const login = (name: string): Promise<Player> =>
  client.post<Player>('/players/login', { name }).then((r) => r.data)
