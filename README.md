# MacroTrack : Advanced Metabolic Engine

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)

## Le Concept

La plupart des applications de fitness (comme MyFitnessPal, Yazio) se basent sur des formules métaboliques statiques (Mifflin-St Jeor) qui ne s'adaptent pas à la réalité de l'utilisateur. 

**MacroTrack n'est pas un simple compteur de calories.** C'est un moteur algorithmique qui modélise la physiologie humaine. Le fonctionnement de l'application s'articule autour de 5 étapes clés :

1. **Estimation Initiale** : Calcul du métabolisme de base (BMR) via les formules de Katch-McArdle ou Mifflin-St Jeor.
2. **Lissage du Poids Dynamique (EWMA)** : Application d'une moyenne mobile exponentielle adaptative avec filtrage des valeurs aberrantes pour révéler la tendance de poids réelle.
3. **TDEE Empirique & Densité Énergétique** : Calcul du métabolisme réel via la balance énergétique, ajusté selon le ratio de perte (muscle vs graisse).
4. **Adaptations Métaboliques** : Modélisation des freins physiologiques (Thermogenèse adaptative en déficit, régulation du NEAT en surplus).
5. **Projections & Recommandations** : Ajustement quotidien des cibles avec intégration de planchers de sécurité scientifique et génération de scénarios de trajectoire.

**[Lire le détail complet du modèle mathématique (METABOLIC_MODEL.md)](./METABOLIC_MODEL.md)**



## Architecture & Stack Technique

L'application suit les principes de la **Clean Architecture** et s'appuie sur une stack logicielle moderne et industrielle :

### Backend (Core Engine)
- **Langage** : Java 21 (Records, Pattern Matching)
- **Framework** : Spring Boot 3.3
- **Sécurité** : Spring Security 6, JWT (JJWT 0.12), Rate Limiting (Bucket4j), Caffeine Cache
- **Données** : Spring Data JPA, Hibernate, PostgreSQL
- **Mapping & Utilitaires** : MapStruct, Lombok
- **Tests** : JUnit 5, Mockito

### Frontend
- **Langage** : TypeScript
- **Framework** : React 18 (Vite)
- **Styling & UI** : Tailwind CSS, React Hook Form, Lucide React
- **Routing & State Management** : React Router, React Query (@tanstack/react-query)
- **Data Viz** : Recharts
- **Requêtage** : Axios

### DevOps
- Conteneurisation complète avec **Docker** et **Docker Compose**
- Migrations de base de données (si applicable via Flyway/Liquibase)


## Complexité Technique & Défis Relevés

Ce projet a été conçu pour aller au-delà du simple CRUD. Voici les défis techniques d'ingénierie qui ont été résolus :

### 1. Algorithmique Avancée & Mathématiques
- **Lissage EWMA Dynamique** : Implémentation d'une Moyenne Mobile Pondérée Exponentiellement avec un coefficient $\alpha$ adaptatif (selon l'intervalle de pesée) et rejet des valeurs aberrantes (outliers).
- **Moteur de TDEE Empirique** : Résolution de l'équation d'équilibre énergétique avec gestion de la densité énergétique variable (taux de masse grasse / musculaire).
- **Moteur de Projection** : L'algorithme simule la trajectoire de poids jour après jour, en modélisant les mécanismes de défense de l'organisme (Thermogenèse Adaptative et NEAT).

### 2. Sécurité Industrielle
- Authentification Stateless via **JWT** avec gestion sécurisée des *Refresh Tokens* (stockés en base).
- **Rate Limiting** via Bucket4j appliqué sur les filtres pour prévenir les attaques par force brute sur les endpoints de connexion.
- Filtres de sécurité paramétrables et gestion centralisée des erreurs de validation ou d'accès.

### 3. Architecture Logicielle Propre
- Séparation stricte des couches (Controllers $\rightarrow$ Services $\rightarrow$ Repositories).
- Utilisation de **Design Patterns** (Strategy pour les calculs BMR et l'Adaptation Métabolique, Factory pour les assemblages).
- **Gestion globale des exceptions** (`GlobalExceptionHandler`) pour capturer les `ConstraintViolationException`, `DataIntegrityViolationException` et renvoyer des réponses JSON standardisées (RFC 7807 Problem Details).
- Isolation des domaines (Auth, Analytics, Nutrition, Weight, Goals).

---

## Démarrage Rapide (Installation)

L'ensemble de l'écosystème (Frontend, Backend API, Base de Données) est dockerisé pour être lancé en une seule commande.

### Prérequis
- Docker & Docker Compose installés sur votre machine.

### Lancer l'application

```bash
docker-compose up -d --build
```

L'application sera disponible sur les ports suivants :
- **Frontend App** : [http://localhost:5173](http://localhost:5173)
- **Backend API REST** : [http://localhost:8080/api](http://localhost:8080/api)
- **Swagger UI (Documentation API)** : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Base de données (PostgreSQL)** : `localhost:5432`

---

## Structure du Répertoire

```
macrorack/
├── .github/                    # Workflows CI/CD
├── docker-compose.yml          # Déploiement multi-conteneurs
├── start.sh / stop.sh          # Scripts de gestion des conteneurs
├── METABOLIC_MODEL.md          # Documentation scientifique des algorithmes
├── macrotrack-backend/         # Backend Spring Boot (Java 21)
│   ├── src/main/java/com/macrotrack/
│   │   ├── config/             # Configurations globales et sécurité
│   │   ├── controller/         # Points d'entrée REST (API)
│   │   ├── dto/                # Objets de transfert de données (Records)
│   │   ├── exception/          # Handlers d'erreurs (GlobalExceptionHandler)
│   │   ├── mapper/             # Convertisseurs MapStruct
│   │   ├── model/              # Entités JPA (Base de données)
│   │   ├── repository/         # Interfaces Spring Data
│   │   ├── security/           # Filtres JWT et Rate Limiting
│   │   ├── service/            # Logique métier et calculs métaboliques
│   │   └── util/               # Utilitaires mathématiques
│   └── pom.xml
└── macrotrack-frontend/        # Interface Utilisateur React (TypeScript)
    ├── src/
    │   ├── components/         # Composants UI réutilisables
    │   ├── context/            # Contextes globaux (Auth)
    │   ├── hooks/              # Hooks React personnalisés (Data fetching)
    │   ├── layouts/            # Layouts de page (BottomNav, etc.)
    │   ├── pages/              # Vues de l'application
    │   ├── routes/             # Configuration React Router
    │   ├── services/           # Clients API et logique d'appel
    │   ├── styles/             # Fichiers CSS (Tailwind)
    │   ├── types/              # Interfaces et types TypeScript
    │   └── utils/              # Fonctions utilitaires et formatage
    └── package.json
```

