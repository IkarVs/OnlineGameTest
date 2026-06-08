package com.onlinegame.hero;

import com.onlinegame.hero.dto.HeroDTO;
import com.onlinegame.item.HeroItem;
import com.onlinegame.item.HeroItemRepository;
import com.onlinegame.item.Item;
import com.onlinegame.item.ItemRepository;
import com.onlinegame.item.ItemType;
import com.onlinegame.player.Player;
import com.onlinegame.player.PlayerRepository;
import com.onlinegame.technique.Technique;
import com.onlinegame.technique.TechniqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final HeroRepository heroRepository;
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;
    private final HeroItemRepository heroItemRepository;
    private final TechniqueRepository techniqueRepository;

    /**
     * Charge un héros avec ses deux collections initialisées via deux requêtes
     * séparées dans la même transaction (évite le MultipleBagFetchException).
     * La première requête charge equippedItems+item, la seconde charge
     * unlockedTechniques — Hibernate fusionne les résultats dans le même contexte.
     */
    @Transactional(readOnly = true)
    public Hero loadWithDetails(Long heroId) {
        Hero hero = heroRepository.findByIdWithEquipment(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable : " + heroId));
        // Deuxième requête : initialise unlockedTechniques sur la même entité
        heroRepository.findByIdWithTechniques(heroId);
        return hero;
    }

    @Transactional
    public HeroDTO create(String name, HeroClass heroClass, Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));
        Hero hero = heroRepository.save(new Hero(name, heroClass, player));
        return HeroDTO.from(hero);
    }

    @Transactional(readOnly = true)
    public List<HeroDTO> findByPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));
        return heroRepository.findByPlayer(player).stream()
                .map(h -> HeroDTO.from(loadWithDetails(h.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public HeroDTO findById(Long heroId) {
        return HeroDTO.from(loadWithDetails(heroId));
    }

    @Transactional
    public HeroDTO equipItem(Long heroId, Long itemId) {
        Hero hero = heroRepository.findByIdWithEquipment(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Objet introuvable"));

        if (hero.getLevel() < item.getRequiredLevel()) {
            throw new IllegalStateException("Niveau insuffisant pour équiper cet objet (requis : " + item.getRequiredLevel() + ")");
        }

        hero.getEquippedItems().removeIf(hi -> hi.getSlot() == item.getType());
        hero.getEquippedItems().add(new HeroItem(hero, item));
        heroRepository.save(hero);

        return HeroDTO.from(loadWithDetails(heroId));
    }

    @Transactional
    public HeroDTO unequipSlot(Long heroId, String slot) {
        Hero hero = heroRepository.findByIdWithEquipment(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        ItemType slotType = ItemType.valueOf(slot);
        hero.getEquippedItems().removeIf(hi -> hi.getSlot() == slotType);
        heroRepository.save(hero);
        return HeroDTO.from(loadWithDetails(heroId));
    }

    @Transactional
    public HeroDTO unlockTechnique(Long heroId, Long techniqueId) {
        // Charge les techniques pour vérifier si déjà connue
        Hero hero = heroRepository.findByIdWithTechniques(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        Technique technique = techniqueRepository.findById(techniqueId)
                .orElseThrow(() -> new IllegalArgumentException("Technique introuvable"));

        if (hero.getLevel() < technique.getRequiredLevel()) {
            throw new IllegalStateException("Niveau insuffisant pour apprendre cette technique (requis : " + technique.getRequiredLevel() + ")");
        }
        boolean alreadyKnown = hero.getUnlockedTechniques().stream()
                .anyMatch(t -> t.getId().equals(techniqueId));
        if (!alreadyKnown) {
            hero.getUnlockedTechniques().add(technique);
            heroRepository.save(hero);
        }
        return HeroDTO.from(loadWithDetails(heroId));
    }
}
