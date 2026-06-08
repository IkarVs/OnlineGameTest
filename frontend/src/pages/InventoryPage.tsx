import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getItems } from '../api/itemApi'
import { equipItem, unequipSlot } from '../api/heroApi'
import { useGameStore } from '../stores/gameStore'
import type { Item, ItemType } from '../types'

const SLOT_ICONS: Record<ItemType, string> = {
  ARME:      '⚔️',
  ARMURE:    '🛡️',
  CASQUE:    '🪖',
  BOTTES:    '👢',
  ACCESSOIRE:'💍',
}

const TYPE_ORDER: ItemType[] = ['ARME', 'ARMURE', 'CASQUE', 'BOTTES', 'ACCESSOIRE']

export default function InventoryPage() {
  const { player, hero, setHero } = useGameStore()
  const navigate = useNavigate()
  const [items, setItems] = useState<Item[]>([])
  const [equipping, setEquipping] = useState<number | null>(null)
  const [unequipping, setUnequipping] = useState<string | null>(null)
  const [feedback, setFeedback] = useState('')
  const [filterType, setFilterType] = useState<ItemType | 'ALL'>('ALL')

  useEffect(() => {
    if (!player || !hero) { navigate('/'); return }
    getItems().then(setItems)
  }, [player, hero, navigate])

  const handleEquip = async (item: Item) => {
    if (!hero) return
    setEquipping(item.id)
    setFeedback('')
    try {
      const updated = await equipItem(hero.id, item.id)
      setHero(updated)
      setFeedback(`${item.name} équipé !`)
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setEquipping(null)
    }
  }

  const handleUnequip = async (slot: ItemType) => {
    if (!hero) return
    setUnequipping(slot)
    setFeedback('')
    try {
      const updated = await unequipSlot(hero.id, slot)
      setHero(updated)
      setFeedback(`Emplacement ${slot} libéré.`)
    } catch (err) {
      setFeedback(err instanceof Error ? err.message : 'Erreur')
    } finally {
      setUnequipping(null)
    }
  }

  if (!hero) return null

  const filtered = filterType === 'ALL' ? items : items.filter((i) => i.type === filterType)

  return (
    <div className="space-y-6">
      <h1 className="font-game text-2xl text-amber-500">Inventaire & Équipement</h1>

      {feedback && (
        <div className="bg-emerald-900/50 border border-emerald-700 rounded px-4 py-2 text-emerald-300 text-sm">
          {feedback}
        </div>
      )}

      {/* Équipement actuel */}
      <div className="card">
        <h2 className="font-semibold text-slate-100 mb-3">Équipement de {hero.name}</h2>
        <div className="grid grid-cols-2 gap-2 sm:grid-cols-5">
          {TYPE_ORDER.map((slot) => {
            const equipped = hero.equipment[slot]
            return (
              <div key={slot} className="bg-slate-700 rounded p-3 text-center">
                <div className="text-xl mb-1">{SLOT_ICONS[slot]}</div>
                <div className="text-xs text-slate-500 mb-1">{slot}</div>
                {equipped ? (
                  <>
                    <div className="text-xs font-semibold text-amber-400 mb-1">{equipped.itemName}</div>
                    <button
                      className="text-xs text-red-400 hover:text-red-300 underline"
                      onClick={() => handleUnequip(slot)}
                      disabled={unequipping === slot}
                    >
                      {unequipping === slot ? '...' : 'Retirer'}
                    </button>
                  </>
                ) : (
                  <div className="text-xs text-slate-600 italic">Vide</div>
                )}
              </div>
            )
          })}
        </div>
      </div>

      {/* Filtres */}
      <div className="flex gap-2 flex-wrap">
        {(['ALL', ...TYPE_ORDER] as const).map((t) => (
          <button
            key={t}
            className={`text-sm px-3 py-1 rounded transition-colors ${
              filterType === t ? 'bg-amber-500 text-slate-900 font-semibold' : 'bg-slate-700 text-slate-300 hover:bg-slate-600'
            }`}
            onClick={() => setFilterType(t)}
          >
            {t === 'ALL' ? 'Tous' : `${SLOT_ICONS[t]} ${t}`}
          </button>
        ))}
      </div>

      {/* Liste des objets */}
      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {filtered.map((item) => {
          const canEquip = hero.level >= item.requiredLevel
          const isEquipped = Object.values(hero.equipment).some((e) => e.itemId === item.id)

          return (
            <div key={item.id} className={`card transition-all ${!canEquip ? 'opacity-60' : ''}`}>
              <div className="flex items-start justify-between mb-1">
                <div>
                  <span className="text-lg mr-1">{SLOT_ICONS[item.type]}</span>
                  <span className="font-semibold text-slate-100">{item.name}</span>
                </div>
                <span className="text-xs text-slate-500 bg-slate-700 px-1.5 py-0.5 rounded">
                  Niv. {item.requiredLevel}
                </span>
              </div>

              <p className="text-xs text-slate-400 mb-2">{item.description}</p>

              <div className="flex gap-3 text-xs mb-3">
                {item.attackBonus > 0 && <span className="text-red-400">+{item.attackBonus} ATK</span>}
                {item.defenseBonus > 0 && <span className="text-sky-400">+{item.defenseBonus} DEF</span>}
                {item.hpBonus > 0 && <span className="text-emerald-400">+{item.hpBonus} PV</span>}
              </div>

              {isEquipped ? (
                <span className="text-xs text-amber-500 font-semibold">✓ Équipé</span>
              ) : (
                <button
                  className="btn-secondary text-xs py-1 px-3 w-full"
                  disabled={!canEquip || equipping === item.id}
                  onClick={() => handleEquip(item)}
                >
                  {equipping === item.id ? '...' : canEquip ? 'Équiper' : `Niveau ${item.requiredLevel} requis`}
                </button>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}
