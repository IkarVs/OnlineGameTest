package com.onlinegame.mission;

import com.onlinegame.hero.Hero;
import com.onlinegame.hero.HeroRepository;
import com.onlinegame.mission.dto.MissionLogDTO;
import com.onlinegame.village.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionLogRepository missionLogRepository;
    private final HeroRepository heroRepository;
    private final VillageRepository villageRepository;

    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    @Transactional
    public MissionLogDTO runMission(Long heroId, Long missionId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));

        if (hero.getLevel() < mission.getRequiredLevel()) {
            throw new IllegalStateException(
                    "Niveau insuffisant pour cette mission. Requis : " + mission.getRequiredLevel()
            );
        }

        hero.addExperience(mission.getXpReward());
        heroRepository.save(hero);

        villageRepository.findByPlayer(hero.getPlayer()).ifPresent(village -> {
            village.setWood(village.getWood() + mission.getWoodReward());
            village.setMetal(village.getMetal() + mission.getMetalReward());
            village.setFood(village.getFood() + mission.getFoodReward());
            villageRepository.save(village);
        });

        MissionLog log = new MissionLog(hero, mission, true,
                mission.getXpReward(), mission.getWoodReward(),
                mission.getMetalReward(), mission.getFoodReward());
        return MissionLogDTO.from(missionLogRepository.save(log));
    }

    @Transactional(readOnly = true)
    public List<MissionLogDTO> getLogsForHero(Long heroId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        return missionLogRepository.findByHeroOrderByCompletedAtDesc(hero)
                .stream()
                .map(MissionLogDTO::from)
                .toList();
    }
}
