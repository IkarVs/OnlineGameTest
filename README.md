# Chroniques de l'Abîme

Jeu RPG en navigateur avec gestion de village. Le joueur incarne un héros qui part en mission pour gagner de l'expérience, choisit ses techniques et son équipement, et gère un village produisant des ressources.

---

## Prérequis

### Backend (Java / Spring Boot)
| Outil | Version minimale |
|-------|-----------------|
| Java  | 21              |
| Maven | 3.9+            |

Vérifier les installations :
```bash
java -version
mvn -version
```

### Frontend (React / TypeScript)
| Outil   | Version minimale |
|---------|-----------------|
| Node.js | 18+             |
| npm     | 9+              |

Vérifier :
```bash
node -v
npm -v
```

### Base de données
| Outil          | Version |
|----------------|---------|
| Docker         | 24+     |
| Docker Compose | 2+      |

Vérifier :
```bash
docker -v
docker compose version
```

---

## Lancement en local

### Étape 1 — Démarrer PostgreSQL

Depuis la **racine du projet** :

```bash
docker compose up -d
```

Cela démarre un conteneur PostgreSQL sur le port `5432`.
Pour vérifier que la base est prête :

```bash
docker compose ps
# onlinegame-db doit afficher "healthy"
```

Pour arrêter la base :
```bash
docker compose down
```

> **Note** : les données sont persistées dans un volume Docker (`postgres_data`).
> Pour repartir d'une base vide : `docker compose down -v`

---

### Étape 2 — Lancer le Backend

```bash
cd backend
mvn spring-boot:run
```

Le serveur démarre sur **http://localhost:8080**.

Au premier démarrage, Spring Boot crée automatiquement les tables et insère les données initiales (missions, objets, techniques).

**Logs attendus :**
```
Initialisation des missions...    Missions créées : 8
Initialisation des objets...      Objets créés : 20
Initialisation des techniques...  Techniques créées : 10
Started OnlineGameApplication in X seconds
```

Pour builder un JAR exécutable :
```bash
mvn clean package -DskipTests
java -jar target/online-game-backend-1.0.0.jar
```

---

### Étape 3 — Lancer le Frontend

Dans un **nouveau terminal** :

```bash
cd frontend
npm install        # à faire une seule fois
npm run dev
```

L'application est disponible sur **http://localhost:5173**.

> Le frontend passe par un proxy Vite pour contacter le backend.
> Il n'y a donc pas de problème CORS à configurer manuellement.

---

## Structure du projet

```
OnlineGameTest/
├── docker-compose.yml        <- Base de données PostgreSQL
├── README.md
│
├── backend/                  <- Spring Boot (Java 21)
│   ├── pom.xml
│   └── src/main/java/com/onlinegame/
│       ├── config/           <- CORS
│       ├── player/           <- Gestion des joueurs
│       ├── hero/             <- Personnage (stats, XP, équipement, techniques)
│       ├── item/             <- Objets & équipement
│       ├── technique/        <- Techniques de combat
│       ├── mission/          <- Missions & historique
│       ├── village/          <- Village, bâtiments, ressources
│       └── init/             <- Données initiales au démarrage
│
└── frontend/                 <- React 18 + TypeScript + Tailwind
    ├── index.html
    ├── vite.config.ts
    └── src/
        ├── api/              <- Appels HTTP vers le backend
        ├── components/       <- Navbar, Layout, XpBar
        ├── pages/            <- HomePage, CharacterPage, VillagePage, MissionsPage, InventoryPage
        ├── stores/           <- État global Zustand (player, hero, village)
        └── types/            <- Interfaces TypeScript partagées
```

---

## API Backend — Résumé des endpoints

| Méthode  | Endpoint                                          | Description                                       |
|----------|---------------------------------------------------|---------------------------------------------------|
| POST     | `/api/players/login`                              | Crée ou charge un joueur par son nom              |
| POST     | `/api/heroes`                                     | Crée un héros                                     |
| GET      | `/api/heroes/player/{id}`                         | Liste les héros d'un joueur                       |
| GET      | `/api/heroes/{id}`                                | Détail d'un héros (stats, équipement, techniques) |
| POST     | `/api/heroes/{id}/equip`                          | Équipe un objet                                   |
| DELETE   | `/api/heroes/{id}/equip/{slot}`                   | Désequipe un emplacement                          |
| POST     | `/api/heroes/{id}/techniques/{tid}`               | Apprend une technique                             |
| GET      | `/api/items`                                      | Liste tous les objets                             |
| GET      | `/api/techniques`                                 | Liste toutes les techniques                       |
| GET      | `/api/missions`                                   | Liste toutes les missions                         |
| POST     | `/api/missions/run`                               | Lance une mission (résolution immédiate)          |
| GET      | `/api/missions/logs/hero/{id}`                    | Historique des missions d'un héros                |
| GET      | `/api/villages/player/{id}`                       | Détail du village                                 |
| POST     | `/api/villages/player/{id}/collect`               | Collecte les ressources accumulées                |
| POST     | `/api/villages/player/{id}/buildings/{type}/upgrade` | Améliore un bâtiment                          |
| POST     | `/api/combats/start`                              | Démarre un combat contre un God Critter aléatoire |
| POST     | `/api/combats/{id}/attack`                        | Attaque de base                                   |
| POST     | `/api/combats/{id}/technique/{tid}`               | Utilise une technique apprise                     |
| POST     | `/api/combats/{id}/capture`                       | Tentative de capture (façon Pokémon)              |
| POST     | `/api/combats/{id}/flee`                          | Tentative de fuite                                |
| GET      | `/api/critters/codex/player/{id}`                 | Codex complet (espèces masquées si non capturées) |

---

## Fonctionnalités du jeu

### Héros
- 3 classes : **Guerrier**, **Mage**, **Archer**
- Statistiques calculées : PV, Attaque, Défense (base + bonus équipement)
- Système d'XP avec montée de niveau automatique (niveau × 100 XP par niveau)
- 5 emplacements d'équipement : Arme, Armure, Casque, Bottes, Accessoire
- 10 techniques à débloquer selon le niveau du héros

### Village
- 3 bâtiments : **Scierie** (bois), **Mine** (métal), **Ferme** (nourriture)
- Production calculée sur le temps écoulé depuis la dernière collecte
- Amélioration jusqu'au niveau 5 (coût : `niveau × 100` bois + `niveau × 50` métal)

### Missions
- 8 missions disponibles, de difficulté 1 à 5 étoiles
- Récompenses immédiates en XP et ressources
- Les ressources vont directement dans le village du joueur
- Historique des 10 dernières missions affiché

### Combat & God Critters
- Combat **tour par tour** contre 10 créatures lovecraftiennes (les God Critters)
- Actions : Attaquer, Technique (parmi celles apprises), Capturer, Fuir
- **Capture façon Pokémon** : plus la créature est blessée, plus la chance de capture augmente
- Rencontres aléatoires adaptées au niveau du héros (les créatures puissantes exigent un niveau élevé)
- **Codex** : encyclopédie des 10 créatures — silhouettes mystérieuses tant qu'elles ne sont pas capturées
- Victoire = XP ; sprites SVG 2D dans `frontend/public/critters/`

---

## Problèmes courants

**Le backend ne démarre pas**
- Vérifiez que Docker est lancé et que `docker compose ps` affiche `onlinegame-db` en `healthy`
- Vérifiez que le port 5432 n'est pas déjà occupé par une autre instance PostgreSQL

**`npm install` échoue**
- Vérifiez que Node.js >= 18 est installé : `node -v`

**Le frontend ne contacte pas le backend**
- Vérifiez que le backend tourne bien sur le port 8080
- Le proxy Vite redirige automatiquement `/api/*` vers `http://localhost:8080`
