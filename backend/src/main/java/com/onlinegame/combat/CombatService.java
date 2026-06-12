package com.onlinegame.combat;

import com.onlinegame.combat.dto.CombatDTO;
import com.onlinegame.critter.CapturedCritter;
import com.onlinegame.critter.CapturedCritterRepository;
import com.onlinegame.critter.CritterSpecies;
import com.onlinegame.critter.CritterSpeciesRepository;
import com.onlinegame.hero.Hero;
import com.onlinegame.hero.HeroRepository;
import com.onlinegame.technique.Technique;
import com.onlinegame.technique.TechniqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CombatService {

    private final CombatRepository combatRepository;
    private final CritterSpeciesRepository speciesRepository;
    private final CapturedCritterRepository capturedCritterRepository;
    private final HeroRepository heroRepository;
    private final TechniqueRepository techniqueRepository;

    private final Random random = new Random();

    /**
     * Démarre un combat contre une créature aléatoire adaptée au niveau du héros.
     * Si un combat actif existe déjà pour ce héros, il est repris.
     */
    @Transactional
    public CombatDTO startCombat(Long heroId) {
        Hero hero = heroRepository.findByIdWithEquipment(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));

        // Reprend un combat actif existant plutôt que d'en créer un autre
        var existing = combatRepository.findFirstByHeroAndStatusOrderByStartedAtDesc(hero, CombatStatus.ACTIVE);
        if (existing.isPresent()) {
            Combat combat = existing.get();
            return CombatDTO.from(combat,
                    List.of("Le combat contre " + combat.getSpecies().getName() + " continue..."), false);
        }

        // Tire une espèce au hasard parmi celles accessibles au niveau du héros
        List<CritterSpecies> candidates = speciesRepository.findByMinHeroLevelLessThanEqual(hero.getLevel());
        if (candidates.isEmpty()) {
            throw new IllegalStateException("Aucune créature disponible pour ce niveau");
        }
        CritterSpecies species = candidates.get(random.nextInt(candidates.size()));

        // Niveau du monstre : niveau du héros ± 1 (minimum 1)
        int monsterLevel = Math.max(1, hero.getLevel() + random.nextInt(3) - 1);
        int monsterMaxHp = species.getBaseHp() + (monsterLevel * 8);

        Combat combat = new Combat(hero, species, monsterLevel, monsterMaxHp, hero.getTotalHp());
        combatRepository.save(combat);

        return CombatDTO.from(combat, List.of(
                "Un " + species.getName() + " sauvage surgit des ténèbres ! (niveau " + monsterLevel + ")"
        ), false);
    }

    /** Attaque de base du héros, puis riposte du monstre s'il survit. */
    @Transactional
    public CombatDTO attack(Long combatId) {
        Combat combat = getActiveCombat(combatId);
        List<String> log = new ArrayList<>();

        Hero hero = heroRepository.findByIdWithEquipment(combat.getHero().getId()).orElseThrow();
        int damage = computeDamage(hero.getTotalAttack(), combat.getMonsterDefense());
        combat.setMonsterHp(Math.max(0, combat.getMonsterHp() - damage));
        log.add("Vous frappez " + combat.getSpecies().getName() + " et infligez " + damage + " dégâts.");

        return resolveTurn(combat, hero, log, false);
    }

    /** Utilise une technique apprise : dégâts plus élevés basés sur la technique. */
    @Transactional
    public CombatDTO useTechnique(Long combatId, Long techniqueId) {
        Combat combat = getActiveCombat(combatId);
        List<String> log = new ArrayList<>();

        Hero hero = heroRepository.findByIdWithTechniques(combat.getHero().getId()).orElseThrow();
        Technique technique = techniqueRepository.findById(techniqueId)
                .orElseThrow(() -> new IllegalArgumentException("Technique introuvable"));

        boolean known = hero.getUnlockedTechniques().stream()
                .anyMatch(t -> t.getId().equals(techniqueId));
        if (!known) {
            throw new IllegalStateException("Votre héros ne connaît pas cette technique");
        }

        // Recharge avec l'équipement pour les stats
        Hero heroWithEquip = heroRepository.findByIdWithEquipment(hero.getId()).orElseThrow();
        int damage = computeDamage(heroWithEquip.getTotalAttack() + technique.getDamage(), combat.getMonsterDefense());
        combat.setMonsterHp(Math.max(0, combat.getMonsterHp() - damage));
        log.add("Vous utilisez " + technique.getName() + " ! " + damage + " dégâts infligés.");

        return resolveTurn(combat, heroWithEquip, log, false);
    }

    /**
     * Tentative de capture façon Pokémon : la chance augmente quand
     * les PV du monstre baissent. Si la capture échoue, le monstre riposte.
     */
    @Transactional
    public CombatDTO tryCapture(Long combatId) {
        Combat combat = getActiveCombat(combatId);
        List<String> log = new ArrayList<>();
        Hero hero = heroRepository.findByIdWithEquipment(combat.getHero().getId()).orElseThrow();

        double hpRatio = (double) combat.getMonsterHp() / combat.getMonsterMaxHp();
        // Pleine vie : ~30% du taux de base. Presque mort : ~100% du taux de base.
        double chance = combat.getSpecies().getCaptureRate() * (1.0 - hpRatio * 0.7);

        log.add("Vous lancez un sceau d'entrave sur " + combat.getSpecies().getName() + "...");

        if (random.nextDouble() < chance) {
            combat.setStatus(CombatStatus.CAPTURED);
            combatRepository.save(combat);

            boolean isNew = !capturedCritterRepository.existsByPlayerAndSpecies(
                    hero.getPlayer(), combat.getSpecies());
            capturedCritterRepository.save(new CapturedCritter(
                    hero.getPlayer(), combat.getSpecies(), combat.getMonsterLevel()));

            log.add("✦ " + combat.getSpecies().getName() + " a été capturé !");
            if (isNew) {
                log.add("Nouvelle entrée ajoutée au Codex !");
            }
            return CombatDTO.from(combat, log, isNew);
        }

        log.add(combat.getSpecies().getName() + " brise le sceau et se libère !");
        return resolveTurn(combat, hero, log, true);
    }

    /** Tentative de fuite : 60% de réussite, sinon le monstre attaque. */
    @Transactional
    public CombatDTO flee(Long combatId) {
        Combat combat = getActiveCombat(combatId);
        List<String> log = new ArrayList<>();
        Hero hero = heroRepository.findByIdWithEquipment(combat.getHero().getId()).orElseThrow();

        if (random.nextDouble() < 0.6) {
            combat.setStatus(CombatStatus.FLED);
            combatRepository.save(combat);
            log.add("Vous fuyez le combat. " + combat.getSpecies().getName() + " retourne dans l'ombre.");
            return CombatDTO.from(combat, log, false);
        }

        log.add("Impossible de fuir ! " + combat.getSpecies().getName() + " vous bloque le passage.");
        return resolveTurn(combat, hero, log, true);
    }

    public CombatDTO getCombat(Long combatId) {
        Combat combat = combatRepository.findById(combatId)
                .orElseThrow(() -> new IllegalArgumentException("Combat introuvable"));
        return CombatDTO.from(combat, List.of(), false);
    }

    // ─── Logique interne ────────────────────────────────────────────────

    /**
     * Termine le tour : vérifie la victoire, fait riposter le monstre,
     * vérifie la défaite, incrémente le tour.
     * skipVictoryCheck = true quand l'action du héros n'inflige pas de dégâts
     * (capture/fuite ratée) : on passe directement à la riposte.
     */
    private CombatDTO resolveTurn(Combat combat, Hero hero, List<String> log, boolean skipVictoryCheck) {
        // Victoire ?
        if (!skipVictoryCheck && combat.getMonsterHp() <= 0) {
            combat.setStatus(CombatStatus.WON);
            int xp = combat.getSpecies().getXpReward() + (combat.getMonsterLevel() * 5);
            hero.addExperience(xp);
            heroRepository.save(hero);
            combatRepository.save(combat);
            log.add(combat.getSpecies().getName() + " s'effondre dans une flaque d'ichor. +" + xp + " XP !");
            return CombatDTO.from(combat, log, false);
        }

        // Riposte du monstre
        int monsterDamage = computeDamage(combat.getMonsterAttack(), hero.getTotalDefense());
        combat.setHeroHp(Math.max(0, combat.getHeroHp() - monsterDamage));
        log.add(combat.getSpecies().getName() + " riposte et inflige " + monsterDamage + " dégâts.");

        // Défaite ?
        if (combat.getHeroHp() <= 0) {
            combat.setStatus(CombatStatus.LOST);
            combatRepository.save(combat);
            log.add("Vous sombrez dans l'inconscience... Le combat est perdu.");
            return CombatDTO.from(combat, log, false);
        }

        combat.setTurn(combat.getTurn() + 1);
        combatRepository.save(combat);
        return CombatDTO.from(combat, log, false);
    }

    /** Dégâts = attaque - défense/2, avec variance ±20%, minimum 1. */
    private int computeDamage(int attack, int defense) {
        double base = Math.max(1, attack - defense / 2.0);
        double variance = 0.8 + random.nextDouble() * 0.4;
        return Math.max(1, (int) Math.round(base * variance));
    }

    private Combat getActiveCombat(Long combatId) {
        Combat combat = combatRepository.findById(combatId)
                .orElseThrow(() -> new IllegalArgumentException("Combat introuvable"));
        if (combat.getStatus() != CombatStatus.ACTIVE) {
            throw new IllegalStateException("Ce combat est déjà terminé (" + combat.getStatus() + ")");
        }
        return combat;
    }
}
