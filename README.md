<div align="center">
<img height="130px" width="130px" src="./src/assets/images/Logo.png">
</div>

# PICK ME, une application web de rencontre

[![CI](https://github.com/Brice150/PICKME/actions/workflows/ci.yml/badge.svg)](https://github.com/Brice150/PICKME/actions/workflows/ci.yml)
![Coverage](https://img.shields.io/badge/couverture-100%25-brightgreen)
![Angular](https://img.shields.io/badge/Angular-21-dd0031)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6db33f)
![Java](https://img.shields.io/badge/Java-21-007396)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)

Application de rencontre complète : sélection de profils par distance et affinité,
messagerie entre profils ayant matché, notifications et back office d'administration.

Frontend : Angular 21 (composants standalone, signaux, routes lazy)
<br>
Backend : Spring Boot 4 / Java 21 / PostgreSQL (API REST, Spring Security, JPA)

<details>
  <summary>Features</summary>

### 🔐 Connexion

- Inscription via un formulaire avec validation des champs obligatoires
- Connexion avec animation en cas d’erreur d’identifiants
- Déconnexion accessible depuis le menu une fois connecté

---

### 🎯 Démo

- Accès à une démonstration de l’application après inscription
- Possibilité de tester les fonctionnalités principales

---

### 💘 Sélection

- Affichage de profils similaires au vôtre en fonction de la distance
- Navigation entre les profils (swipe) avec possibilité de revenir en arrière
- Like ou Dislike d’un profil
- Animation lors d’un match
- Consultation des informations détaillées d’un profil

---

### 👤 Profil

- Ajout ou suppression de photos et sélection de la photo de profil principale
- Modification du profil utilisateur
- Annulation des modifications (retour aux anciennes données)
- Suppression du compte (avec confirmation obligatoire)

---

### 💬 Match

- Consultation des profils ayant matché avec vous
- Recherche d’un utilisateur
- Dislike d’un profil
- Accès aux informations détaillées et aux messages
- Ajout, modification ou suppression de messages

---

### 🛠️ Administration

- Accès réservé aux administrateurs
- Recherche et tri des utilisateurs et comptes supprimés par email
- Consultation des statistiques de l’application
- Consultation des profils utilisateurs
- Suppression d’un utilisateur

---

### ℹ️ Informations

- Consultation complète du profil utilisateur
- Navigation dans les photos (swiper)
- Like, Dislike ou suppression (si admin)

---

### 🔔 Notifications

- Nouvelle notification pour chaque message ou match
- Marquer les notifications comme vues

---

</details>

<details>
  <summary>Installation locale</summary>

### Cloner le projet

```bash
  git clone https://github.com/Brice150/PICKME.git
```

### Installer les dépendances

```bash
  npm install
```

### Lancer l'application

```bash
  ng serve -o
```

### Lancer les tests du front

```bash
  ng test
```

### Lancer le back (Java 21, PostgreSQL sur le port 5432)

```bash
  cd backend && ./mvnw spring-boot:run
```

### Lancer les tests du back

```bash
  cd backend && ./mvnw verify
```

</details>

<details>
  <summary>Qualité et tests</summary>

### Architecture

Le back est découpé en couches, chacune avec une seule responsabilité :

| Couche       | Rôle                                                                     |
| ------------ | ------------------------------------------------------------------------ |
| `controller` | Traduction HTTP : codes de retour, validation du payload, rien de métier |
| `service`    | Règles métier, derrière une interface par service                        |
| `repository` | Accès aux données via Spring Data JPA                                    |
| `model/dto`  | Vues exposées au client, distinctes des entités JPA                      |
| `security`   | Chaîne de filtres, CORS, chiffrement des mots de passe                   |

Les entités ne sortent jamais telles quelles : `UserDTO` expose la vue complète du
compte connecté, `UserDTOMapperRestricted` la vue réduite d'un autre profil, sans
mot de passe, rôle ni statistiques.

### Stratégie de test

| Niveau                        | Outils                            | Ce qui est vérifié                                            |
| ----------------------------- | --------------------------------- | ------------------------------------------------------------- |
| Services back                 | JUnit 5 + Mockito                 | Chaque règle métier et chaque branche, dépendances mockées    |
| Contrôleurs back              | `@WebMvcTest` + MockMvc           | Codes HTTP, sérialisation, validation, gestion des erreurs    |
| Règles de sécurité            | `@SpringBootTest`                 | 401 anonyme, 403 hors rôle, endpoints publics, préflight CORS |
| Services, guards, pipes front | Jasmine + `HttpTestingController` | Requêtes émises, redirections, transformations                |
| Composants front              | Jasmine + `TestBed`               | Rendu, entrées et sorties, logique de chaque écran            |

### Couverture

Le build back échoue si la couverture d'instructions **ou** de branches des
couches `service.impl`, `controller` et `exception` descend sous 100 %. La règle
est portée par JaCoCo et s'applique classe par classe :

```bash
  cd backend && ./mvnw verify   # rapport dans backend/target/site/jacoco
```

### Intégration continue

Chaque push et chaque pull request déclenchent deux jobs :

- **Frontend** : vérification du formatage Prettier, ESLint, tests Karma en
  headless, build de production
- **Backend** : suite de tests complète et contrôle de couverture, le rapport
  JaCoCo étant publié en artefact

Dependabot surveille par ailleurs les dépendances npm, Maven et GitHub Actions.

</details>

<details>
  <summary>APIs</summary>

  <br>

  <details>
  <summary>Connexion</summary>

### Inscription

```http
  POST /registration
```

### Connexion

```http
  GET /login
```

### Déconnexion

```http
  GET /logout
```

  </details>

  <details>
  <summary>Admin</summary>

### Récupérer les statistiques admin

```http
  GET /admin/stats
```

### Récupérer tous les utilisateurs

```http
  POST /admin/user/all/${page}
```

### Récupérer tous les comptes supprimés

```http
  POST /admin/deleted-account/all/${page}
```

### Supprimer un utilisateur

```http
  DELETE /admin/${userId}
```

  </details>

  <details>
  <summary>User</summary>

### Récupérer les utilisateurs sélectionnés

```http
  GET /user/all/${page}
```

### Récupérer l’utilisateur connecté

```http
  GET /user
```

### Mettre à jour l’utilisateur

```http
  PUT /user
```

### Supprimer l’utilisateur connecté

```http
  DELETE /user
```

  </details>

  <details>
  <summary>Message</summary>

### Ajouter un message

```http
  POST /message
```

### Modifier un message

```http
  PUT /message
```

### Supprimer un message

```http
  DELETE /message/${messageId}
```

  </details>

  <details>
  <summary>Like</summary>

### Ajouter un like

```http
  POST /like/${userId}
```

  </details>

  <details>
  <summary>Dislike</summary>

### Ajouter un dislike

```http
  POST /dislike/${userId}
```

  </details>

  <details>
  <summary>Picture</summary>

### Ajouter une photo

```http
  POST /picture
```

### Définir la photo principale

```http
  PUT /picture/${pictureId}
```

### Supprimer une photo

```http
  DELETE /picture/${pictureId}
```

  </details>

  <details>
  <summary>Match</summary>

### Récupérer tous les matchs utilisateur

```http
  GET /match/all
```

  </details>

  <details>
  <summary>Notification</summary>

### Récupérer toutes les notifications utilisateur

```http
  GET /notification/all
```

### Marquer toutes les notifications comme lues

```http
  PUT /notification
```

  </details>

</details>
