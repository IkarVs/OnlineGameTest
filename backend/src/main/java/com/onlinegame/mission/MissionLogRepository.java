package com.onlinegame.mission;

import com.onlinegame.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionLogRepository extends JpaRepository<MissionLog, Long> {
    List<MissionLog> findByHeroOrderByCompletedAtDesc(Hero hero);
}
