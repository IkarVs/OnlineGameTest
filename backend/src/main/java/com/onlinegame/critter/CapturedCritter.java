package com.onlinegame.critter;

import com.onlinegame.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "captured_critters")
@Getter
@Setter
@NoArgsConstructor
public class CapturedCritter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    private CritterSpecies species;

    private String nickname;
    private int level = 1;
    private LocalDateTime capturedAt = LocalDateTime.now();

    public CapturedCritter(Player player, CritterSpecies species, int level) {
        this.player = player;
        this.species = species;
        this.level = level;
        this.nickname = species.getName();
    }
}
