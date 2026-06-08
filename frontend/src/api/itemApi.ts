import client from './client'
import type { Item } from '../types'

export const getItems = (): Promise<Item[]> =>
  client.get<Item[]>('/items').then((r) => r.data)
