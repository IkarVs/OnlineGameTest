package com.onlinegame.combat.dto;

import com.onlinegame.combat.Combat;
import com.onlinegame.combat.CombatStatus;

import java.util.List;

public record CombatDTO(
        Long id,
        Long heroId,
        SpeciesSummary species,
        int monsterLevel,
        int monsterHp,
        int monsterMaxHp,
        int heroHp,
        int heroMaxHp,
        CombatStatus status,
        int turn,
        List<String> log,
        boolean newCapture
) {
    public record SpeciesSummary(Long id, int codexNumber, String name,
                                 String element, String spriteUrl) {}

    public static CombatDTO from(Combat combat, List<String> log, boolean newCapture) {
        return new CombatDTO(
                combat.getId(),
                combat.getHero().getId(),
                new SpeciesSummary(
                        combat.getSpecies().getId(),
                        combat.getSpecies().getCodexNumber(),
                        combat.getSpecies().getName(),
                        combat.getSpecies().getElement(),
                        combat.getSpecies().getSpriteUrl()
                ),
                combat.getMonsterLevel(),
                combat.getMonsterHp(),
                combat.getMonsterMaxHp(),
                combat.getHeroHp(),
                combat.getHeroMaxHp(),
                combat.getStatus(),
                combat.getTurn(),
                log,
                newCapture
        );
    }
}
