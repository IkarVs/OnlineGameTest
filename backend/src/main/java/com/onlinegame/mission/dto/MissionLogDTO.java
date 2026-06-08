package com.onlinegame.mission.dto;

import com.onlinegame.mission.MissionLog;

import java.time.LocalDateTime;

public record MissionLogDTO(
        Long id,
        MissionSummary mission,
        HeroSummary hero,
        LocalDateTime completedAt,
        boolean success,
        int xpGained,
        int woodGained,
        int metalGained,
        int foodGained
) {
    public record MissionSummary(Long id, String name, int difficulty) {}
    public record HeroSummary(Long id, String name) {}

    public static MissionLogDTO from(MissionLog log) {
        return new MissionLogDTO(
                log.getId(),
                new MissionSummary(
                        log.getMission().getId(),
                        log.getMission().getName(),
                        log.getMission().getDifficulty()
                ),
                new HeroSummary(
                        log.getHero().getId(),
                        log.getHero().getName()
                ),
                log.getCompletedAt(),
                log.isSuccess(),
                log.getXpGained(),
                log.getWoodGained(),
                log.getMetalGained(),
                log.getFoodGained()
        );
    }
}
