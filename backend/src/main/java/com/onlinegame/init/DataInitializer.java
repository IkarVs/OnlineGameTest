package com.onlinegame.init;

import com.onlinegame.item.Item;
import com.onlinegame.item.ItemRepository;
import com.onlinegame.item.ItemType;
import com.onlinegame.mission.Mission;
import com.onlinegame.mission.MissionRepository;
import com.onlinegame.technique.Technique;
import com.onlinegame.technique.TechniqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MissionRepository missionRepository;
    private final ItemRepository itemRepository;
    private final TechniqueRepository techniqueRepository;

    @Override
    public void run(String... args) {
        initMissions();
        initItems();
        initTechniques();
    }

    private void initMissions() {
        if (missionRepository.count() > 0) return;
        log.info("Initialisation des missions...");

        missionRepository.save(new Mission("Forêt des Ombres",
                "Une forêt hantée au nord du village. Parfait pour les débutants.",
                1, 1, 50, 20, 0, 10));
        missionRepository.save(new Mission("Champs Maudits",
                "Des champs infestés de créatures sombres. De la nourriture attend les courageux.",
                1, 1, 40, 0, 0, 25));
        missionRepository.save(new Mission("Mine Abandonnée",
                "Une ancienne mine pleine de dangers et de précieux minerais.",
                2, 3, 100, 0, 30, 0));
        missionRepository.save(new Mission("Tour du Sorcier",
                "Un sorcier fou retranche dans sa tour. Ses trésors valent le risque.",
                3, 5, 200, 20, 20, 20));
        missionRepository.save(new Mission("Crypte des Morts-Vivants",
                "Les profondeurs de la crypte abritent des horreurs indicibles.",
                3, 6, 250, 30, 15, 30));
        missionRepository.save(new Mission("Donjon des Trolls",
                "Des trolls géants gardent un trésor de ressources précieuses.",
                4, 8, 350, 50, 50, 30));
        missionRepository.save(new Mission("Château des Vampires",
                "Le château maudit du seigneur vampire. Seuls les plus forts survivent.",
                5, 12, 500, 100, 100, 100));
        missionRepository.save(new Mission("Abîme du Dragon Ancien",
                "Un dragon millénaire dort dans les profondeurs. La gloire ou la mort.",
                5, 15, 800, 150, 150, 150));

        log.info("Missions créées : {}", missionRepository.count());
    }

    private void initItems() {
        if (itemRepository.count() > 0) return;
        log.info("Initialisation des objets...");

        // Armes
        itemRepository.save(new Item("Épée Rouillée", "Une vieille épée abîmée mais fonctionnelle.",
                ItemType.ARME, 5, 0, 0, 1));
        itemRepository.save(new Item("Épée de Fer", "Forgée par un artisan compétent.",
                ItemType.ARME, 12, 2, 0, 3));
        itemRepository.save(new Item("Lame de Feu", "Une lame imprégnée de flammes éternelles.",
                ItemType.ARME, 25, 3, 0, 7));
        itemRepository.save(new Item("Bâton Mystique", "Canalise la magie avec efficacité.",
                ItemType.ARME, 8, 0, 20, 3));
        itemRepository.save(new Item("Sceptre du Mage Noir", "Amplifie la puissance des sorts sombres.",
                ItemType.ARME, 20, 0, 40, 10));
        itemRepository.save(new Item("Arc Long", "Permet de frapper de loin avec précision.",
                ItemType.ARME, 15, 0, 10, 4));
        itemRepository.save(new Item("Arc Enchanté", "Chaque flèche portée d'une énergie mystique.",
                ItemType.ARME, 30, 5, 15, 9));

        // Armures
        itemRepository.save(new Item("Tunique de Cuir", "Protection légère mais flexible.",
                ItemType.ARMURE, 0, 5, 20, 1));
        itemRepository.save(new Item("Cotte de Mailles", "Protection solide contre les coups tranchants.",
                ItemType.ARMURE, 0, 12, 30, 4));
        itemRepository.save(new Item("Armure de Plates", "Lourde mais presque impénétrable.",
                ItemType.ARMURE, 0, 25, 50, 8));
        itemRepository.save(new Item("Robe du Mage", "Légère et imprégnée de runes protectrices.",
                ItemType.ARMURE, 5, 3, 40, 2));

        // Casques
        itemRepository.save(new Item("Casque de Cuir", "Protection basique pour la tête.",
                ItemType.CASQUE, 0, 3, 10, 1));
        itemRepository.save(new Item("Heaume de Fer", "Lourd mais très protecteur.",
                ItemType.CASQUE, 0, 8, 20, 5));
        itemRepository.save(new Item("Chapeau du Sorcier", "Amplifie la concentration magique.",
                ItemType.CASQUE, 3, 2, 25, 3));

        // Bottes
        itemRepository.save(new Item("Bottes de Voyage", "Confortables pour les longues marches.",
                ItemType.BOTTES, 0, 2, 10, 1));
        itemRepository.save(new Item("Bottes de Guerre", "Renforcées pour le combat.",
                ItemType.BOTTES, 2, 5, 15, 6));
        itemRepository.save(new Item("Sandales du Vent", "Légères comme une plume.",
                ItemType.BOTTES, 3, 1, 20, 4));

        // Accessoires
        itemRepository.save(new Item("Anneau de Vitalité", "Augmente légèrement les points de vie.",
                ItemType.ACCESSOIRE, 0, 0, 30, 1));
        itemRepository.save(new Item("Amulette de Force", "Renforce la puissance physique.",
                ItemType.ACCESSOIRE, 8, 0, 0, 5));
        itemRepository.save(new Item("Orbe du Dragon", "Contient l'essence d'un ancien dragon.",
                ItemType.ACCESSOIRE, 15, 10, 50, 12));

        log.info("Objets créés : {}", itemRepository.count());
    }

    private void initTechniques() {
        if (techniqueRepository.count() > 0) return;
        log.info("Initialisation des techniques...");

        techniqueRepository.save(new Technique("Coup Puissant",
                "Un coup concentrant toute la force du héros.", 20, 10, 1, "PHYSIQUE"));
        techniqueRepository.save(new Technique("Esquive",
                "Réduit les dégâts subis lors du prochain assaut.", 0, 8, 1, "PHYSIQUE"));
        techniqueRepository.save(new Technique("Tir Précis",
                "Une flèche visant un point vital de l'ennemi.", 25, 15, 2, "PHYSIQUE"));
        techniqueRepository.save(new Technique("Boule de Feu",
                "Une sphère de feu incandescente propulsée sur l'ennemi.", 35, 25, 3, "FEU"));
        techniqueRepository.save(new Technique("Soin Mineur",
                "Restaure une partie des points de vie.", 0, 20, 2, "LUMIERE"));
        techniqueRepository.save(new Technique("Frappe Glaciale",
                "Un coup imprégné de glace qui ralentit l'ennemi.", 30, 22, 4, "GLACE"));
        techniqueRepository.save(new Technique("Foudre",
                "Un éclair de foudre qui frappe l'ennemi.", 50, 35, 5, "FOUDRE"));
        techniqueRepository.save(new Technique("Coup Double",
                "Deux attaques rapides consécutives.", 28, 25, 6, "PHYSIQUE"));
        techniqueRepository.save(new Technique("Nova de Feu",
                "Une explosion de flammes ravageant tous les ennemis proches.", 60, 45, 8, "FEU"));
        techniqueRepository.save(new Technique("Lame d'Ombre",
                "Une attaque imprégnée d'énergie des ténèbres.", 70, 50, 10, "OMBRE"));

        log.info("Techniques créées : {}", techniqueRepository.count());
    }
}
