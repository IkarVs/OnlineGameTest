import client from './client'
import type { Technique } from '../types'

export const getTechniques = (): Promise<Technique[]> =>
  client.get<Technique[]>('/techniques').then((r) => r.data)
