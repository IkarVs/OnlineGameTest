package com.onlinegame.mission;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    boolean existsByName(String name);
}
