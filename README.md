<div align="center">
<img height="130px" width="130px" src="./src/assets/images/Logo.png">
</div>
  
# PICK ME, une application web de rencontre

Frontend : Angular
<br>
Backend : Spring Boot

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
  git clone https://github.com/Brice150/Life-Rise.git
```

### Installer les dépendances

```bash
  npm install
```

### Lancer l'application

```bash
  ng serve -o
```

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
