package com.onlinegame.critter.dto;

import com.onlinegame.critter.CritterSpecies;

public record CodexEntryDTO(
        Long id,
        int codexNumber,
        String name,
        String description,
        String element,
        int baseHp,
        int baseAttack,
        int baseDefense,
        double captureRate,
        int minHeroLevel,
        String spriteUrl,
        boolean captured
) {
    public static CodexEntryDTO from(CritterSpecies s, boolean captured) {
        // Les détails d'une espèce non capturée restent mystérieux
        return new CodexEntryDTO(
                s.getId(),
                s.getCodexNumber(),
                captured ? s.getName() : "???",
                captured ? s.getDescription() : "Créature non encore capturée. Son mystère demeure entier.",
                captured ? s.getElement() : "INCONNU",
                captured ? s.getBaseHp() : 0,
                captured ? s.getBaseAttack() : 0,
                captured ? s.getBaseDefense() : 0,
                captured ? s.getCaptureRate() : 0,
                s.getMinHeroLevel(),
                s.getSpriteUrl(),
                captured
        );
    }
}
