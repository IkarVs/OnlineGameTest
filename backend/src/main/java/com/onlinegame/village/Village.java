package com.onlinegame.village;

import com.onlinegame.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "villages")
@Getter
@Setter
@NoArgsConstructor
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", unique = true, nullable = false)
    private Player player;

    private int wood = 100;
    private int metal = 50;
    private int food = 100;

    private LocalDateTime lastCollected = LocalDateTime.now();

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Building> buildings = new ArrayList<>();

    public Village(Player player) {
        this.player = player;
        this.buildings.add(new Building(this, BuildingType.SCIERIE));
        this.buildings.add(new Building(this, BuildingType.MINE));
        this.buildings.add(new Building(this, BuildingType.FERME));
    }
}
