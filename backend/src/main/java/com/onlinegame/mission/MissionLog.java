package com.onlinegame.mission;

import com.onlinegame.hero.Hero;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_logs")
@Getter
@Setter
@NoArgsConstructor
public class MissionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private LocalDateTime completedAt = LocalDateTime.now();
    private boolean success;
    private int xpGained;
    private int woodGained;
    private int metalGained;
    private int foodGained;

    public MissionLog(Hero hero, Mission mission, boolean success,
                      int xpGained, int woodGained, int metalGained, int foodGained) {
        this.hero = hero;
        this.mission = mission;
        this.success = success;
        this.xpGained = xpGained;
        this.woodGained = woodGained;
        this.metalGained = metalGained;
        this.foodGained = foodGained;
    }
}
