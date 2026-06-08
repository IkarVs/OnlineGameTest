package com.onlinegame.village;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByVillageAndType(Village village, BuildingType type);
}
