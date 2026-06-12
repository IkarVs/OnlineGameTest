package com.onlinegame.critter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CritterSpeciesRepository extends JpaRepository<CritterSpecies, Long> {
    List<CritterSpecies> findAllByOrderByCodexNumberAsc();
    List<CritterSpecies> findByMinHeroLevelLessThanEqual(int heroLevel);
}
