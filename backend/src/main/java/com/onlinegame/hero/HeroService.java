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

    @Transactional
    public HeroDTO create(String name, HeroClass heroClass, Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));
        Hero hero = heroRepository.save(new Hero(name, heroClass, player));
        return HeroDTO.from(hero);
    }

    public List<HeroDTO> findByPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur introuvable"));
        return heroRepository.findByPlayer(player).stream()
                .map(h -> heroRepository.findByIdWithDetails(h.getId()).map(HeroDTO::from).orElseThrow())
                .toList();
    }

    public HeroDTO findById(Long heroId) {
        Hero hero = heroRepository.findByIdWithDetails(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable : " + heroId));
        return HeroDTO.from(hero);
    }

    @Transactional
    public HeroDTO equipItem(Long heroId, Long itemId) {
        Hero hero = heroRepository.findByIdWithDetails(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Objet introuvable"));

        if (hero.getLevel() < item.getRequiredLevel()) {
            throw new IllegalStateException("Niveau insuffisant pour équiper cet objet (requis : " + item.getRequiredLevel() + ")");
        }

        // Remplace l'item existant dans le même slot s'il y en a un
        hero.getEquippedItems().removeIf(hi -> hi.getSlot() == item.getType());
        hero.getEquippedItems().add(new HeroItem(hero, item));
        heroRepository.save(hero);

        return HeroDTO.from(heroRepository.findByIdWithDetails(heroId).orElseThrow());
    }

    @Transactional
    public HeroDTO unequipSlot(Long heroId, String slot) {
        Hero hero = heroRepository.findByIdWithDetails(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héros introuvable"));
        ItemType slotType = ItemType.valueOf(slot);
        hero.getEquippedItems().removeIf(hi -> hi.getSlot() == slotType);
        heroRepository.save(hero);
        return HeroDTO.from(heroRepository.findByIdWithDetails(heroId).orElseThrow());
    }

    @Transactional
    public HeroDTO unlockTechnique(Long heroId, Long techniqueId) {
        Hero hero = heroRepository.findByIdWithDetails(heroId)
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
        return HeroDTO.from(heroRepository.findByIdWithDetails(heroId).orElseThrow());
    }
}
