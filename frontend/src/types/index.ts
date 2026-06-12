export type HeroClass = 'GUERRIER' | 'MAGE' | 'ARCHER'
export type ItemType = 'ARME' | 'ARMURE' | 'CASQUE' | 'BOTTES' | 'ACCESSOIRE'
export type BuildingType = 'SCIERIE' | 'MINE' | 'FERME'

export interface Player {
  id: number
  name: string
}

export interface EquippedItem {
  id: number
  itemId: number
  itemName: string
  attackBonus: number
  defenseBonus: number
  hpBonus: number
}

export interface Technique {
  id: number
  name: string
  description: string
  damage: number
  manaCost: number
  element: string
  requiredLevel: number
}

export interface Hero {
  id: number
  name: string
  heroClass: HeroClass
  level: number
  experience: number
  xpToNextLevel: number
  totalAttack: number
  totalDefense: number
  totalHp: number
  playerId: number
  equipment: Partial<Record<ItemType, EquippedItem>>
  unlockedTechniques: Technique[]
}

export interface Item {
  id: number
  name: string
  description: string
  type: ItemType
  attackBonus: number
  defenseBonus: number
  hpBonus: number
  requiredLevel: number
}

export interface Building {
  id: number
  type: BuildingType
  level: number
  productionPerHour: number
  upgradeCostWood: number
  upgradeCostMetal: number
  maxLevel: boolean
}

export interface Village {
  id: number
  playerId: number
  wood: number
  metal: number
  food: number
  buildings: Building[]
}

export interface Mission {
  id: number
  name: string
  description: string
  difficulty: number
  requiredLevel: number
  xpReward: number
  woodReward: number
  metalReward: number
  foodReward: number
}

export interface MissionLog {
  id: number
  mission: Mission
  hero: { id: number; name: string }
  completedAt: string
  success: boolean
  xpGained: number
  woodGained: number
  metalGained: number
  foodGained: number
}

export type CombatStatus = 'ACTIVE' | 'WON' | 'LOST' | 'CAPTURED' | 'FLED'

export interface CombatSpecies {
  id: number
  codexNumber: number
  name: string
  element: string
  spriteUrl: string
}

export interface Combat {
  id: number
  heroId: number
  species: CombatSpecies
  monsterLevel: number
  monsterHp: number
  monsterMaxHp: number
  heroHp: number
  heroMaxHp: number
  status: CombatStatus
  turn: number
  log: string[]
  newCapture: boolean
}

export interface CodexEntry {
  id: number
  codexNumber: number
  name: string
  description: string
  element: string
  baseHp: number
  baseAttack: number
  baseDefense: number
  captureRate: number
  minHeroLevel: number
  spriteUrl: string
  captured: boolean
}
