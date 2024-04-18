<div align="center">
<img height="130px" width="130px" src="./src/assets/images/Logo.png">
</div>
  
# PICK ME, a dating web application

Frontend : Angular
<br>
Backend : Spring Boot

<details>
  <summary>Pages Features</summary>

### Connect

- Register by completing mandatory information with form control
- Login with failure animation for bad credentials
- Logout is available on menu once logged in

### Demo

- Demo of the application when you have registered
- View and try key features of the application

### Select

- View profiles that are similar to yours by distance
- Swipe profiles and go back if you want to
- Like or Dislike a profile
- Match animation
- View more information about a profile

### Profile

- Add or Delete pictures and Select main profile picture
- Modify your profile
- Cancel modifications retrieves previous profile information
- Delete your account (needs confirmation)

### Match

- View profiles that matched with you
- Search a user
- Dislike a profile
- View more information about a profile and messages
- Add, Update or Delete a message

### Admin

- Admin role needed to view this page
- Search users and deleted accounts by email and sort them
- Get application stats
- View more information about a profile
- Delete a user

### More Info

- View full user profile
- Swiper user pictures
- Like, Dislike or Delete if you are an admin user

### Notifications

- New notification for each message or match
- Mark notifications as seen

</details>

<details>
  <summary>Run Locally</summary>

### Clone the project

```bash
  git clone https://github.com/Brice150/PICKME.git
```

### Go to backend directory

    --> Run application on Intellij

### Install dependencies

```bash
  npm install
```

### Start the server

```bash
  ng serve -o
```

</details>

<details>
  <summary>API Reference</summary>

  <br>

  <details>
  <summary>Connection</summary>

### Register

```http
  POST /registration
```

### Login

```http
  GET /login
```

### Logout

```http
  GET /logout
```

  </details>

  <details>
  <summary>Admin</summary>

### Get admin stats

```http
  GET /admin/stats
```

### Get all users

```http
  POST /admin/user/all/${page}
```

### Get all deleted accounts

```http
  POST /admin/deleted-account/all/${page}
```

### Delete user

```http
  DELETE /admin/${userId}
```

  </details>

  <details>
  <summary>User</summary>

### Get all selected users

```http
  GET /user/all/${page}
```

### Get connected user

```http
  GET /user
```

### Update user

```http
  PUT /user
```

### Delete connected user

```http
  DELETE /user
```

  </details>

  <details>
  <summary>Message</summary>

### Add message

```http
  POST /message
```

### Update message

```http
  PUT /message
```

### Delete message

```http
  DELETE /message/${messageId}
```

  </details>

  <details>
  <summary>Like</summary>

### Add like

```http
  POST /like/${userId}
```

  </details>

  <details>
  <summary>Dislike</summary>

### Add dislike

```http
  POST /dislike/${userId}
```

  </details>

  <details>
  <summary>Picture</summary>

### Add picture

```http
  POST /picture
```

### Select main picture

```http
  PUT /picture/${pictureId}
```

### Delete picture

```http
  DELETE /picture/${pictureId}
```

  </details>

  <details>
  <summary>Match</summary>

### Get all user matches

```http
  GET /match/all
```

  </details>

  <details>
  <summary>Notification</summary>

### Get all user notifications

```http
  GET /notification/all
```

### mark all user notifications as seen

```http
  PUT /notification
```

  </details>

</details>
