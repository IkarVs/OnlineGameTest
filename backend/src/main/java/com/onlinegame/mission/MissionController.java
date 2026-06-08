package com.onlinegame.mission;

import com.onlinegame.mission.dto.MissionLogDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<List<Mission>> getAll() {
        return ResponseEntity.ok(missionService.findAll());
    }

    @PostMapping("/run")
    public ResponseEntity<MissionLogDTO> runMission(@RequestBody RunMissionRequest request) {
        return ResponseEntity.ok(missionService.runMission(request.heroId(), request.missionId()));
    }

    @GetMapping("/logs/hero/{heroId}")
    public ResponseEntity<List<MissionLogDTO>> getLogs(@PathVariable Long heroId) {
        return ResponseEntity.ok(missionService.getLogsForHero(heroId));
    }

    public record RunMissionRequest(@NotNull Long heroId, @NotNull Long missionId) {}
}
