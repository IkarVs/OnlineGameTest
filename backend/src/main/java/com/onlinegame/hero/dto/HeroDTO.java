package com.onlinegame.hero.dto;

import com.onlinegame.hero.Hero;
import com.onlinegame.hero.HeroClass;
import com.onlinegame.item.HeroItem;
import com.onlinegame.item.ItemType;
import com.onlinegame.technique.Technique;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record HeroDTO(
        Long id,
        String name,
        HeroClass heroClass,
        int level,
        int experience,
        int xpToNextLevel,
        int totalAttack,
        int totalDefense,
        int totalHp,
        Long playerId,
        Map<String, EquippedItemDTO> equipment,
        List<TechniqueDTO> unlockedTechniques
) {
    public record EquippedItemDTO(Long id, Long itemId, String itemName,
                                  int attackBonus, int defenseBonus, int hpBonus) {}

    public record TechniqueDTO(Long id, String name, String description,
                               int damage, int manaCost, String element) {}

    public static HeroDTO from(Hero hero) {
        Map<String, EquippedItemDTO> equipment = hero.getEquippedItems().stream()
                .collect(Collectors.toMap(
                        hi -> hi.getSlot().name(),
                        hi -> new EquippedItemDTO(
                                hi.getId(), hi.getItem().getId(), hi.getItem().getName(),
                                hi.getItem().getAttackBonus(), hi.getItem().getDefenseBonus(), hi.getItem().getHpBonus()
                        )
                ));

        List<TechniqueDTO> techniques = hero.getUnlockedTechniques().stream()
                .map(t -> new TechniqueDTO(t.getId(), t.getName(), t.getDescription(),
                        t.getDamage(), t.getManaCost(), t.getElement()))
                .toList();

        return new HeroDTO(
                hero.getId(),
                hero.getName(),
                hero.getHeroClass(),
                hero.getLevel(),
                hero.getExperience(),
                hero.getXpToNextLevel(),
                hero.getTotalAttack(),
                hero.getTotalDefense(),
                hero.getTotalHp(),
                hero.getPlayer().getId(),
                equipment,
                techniques
        );
    }
}
