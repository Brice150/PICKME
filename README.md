<div align="center">
<img height="130px" width="130px" src="./src/assets/images/Logo.png">
</div>
  
# PICK ME, a dating web application

Frontend : Angular CLI
<br>
Backend : Spring Boot

## Pages Features

### Connect

- Register with email confirmation
- Login with form control and failure animation
- Logout is available on any page once logged in

### Select

- View profiles that are similar to yours
- Select profile age you want to see
- Like a profile
- View more information about a profile

### Profile

- Modify your profile
- Add pictures and select main profile picture
- Delete your account (needs confirmation)

### Like

- View profiles that liked you
- Like a profile
- View more information about a profile

### Match

- View profiles that matched with you
- Search a user
- Dislike a profile
- View more information about a profile
- Send a message, update or delete it

### Admin

- Admin role needed to view this page
- Search user or message
- See all users or messages
- View more information about a profile
- Delete user or message

## Run Locally

Clone the project

```bash
  git clone https://github.com/Brice150/PICKME.git
```

Go to backend directory 
--> Run application on Intellij

Install dependencies

```bash
  npm install
```

Start the server

```bash
  ng serve -o
```

## API Reference

### Connection

#### Register

```http
  POST /registration
```

#### Confirm email

```http
  GET /registration/confirm
```

#### Login

```http
  GET /login
```

#### Logout

```http
  GET /logout
```

### Admin

#### Get all users

```http
  GET /admin/user/all
```

#### Delete user

```http
  DELETE /admin/user/${email}
```

#### Delete message

```http
  DELETE /admin/message/${messageId}
```

### User

#### Get all users

```http
  GET /user/all
```

#### Get all users that liked

```http
  GET /user/all/like
```

#### Get all users that matched

```http
  GET /user/all/match
```

#### Get connected user

```http
  GET /user
```

#### Get user by id

```http
  GET /user/${userId}
```

#### Update user

```http
  PUT /user
```

#### Delete user

```http
  DELETE /user/${email}
```

### Message

#### Get all user messages

```http
  GET /message/all/${userId}
```

#### Get message sender

```http
  GET /message/sender/${messageId}
```

#### Get user messages number

```http
  GET /message/all/number/${userId}
```

#### Add message

```http
  POST /message
```

#### Update message

```http
  PUT /message
```

#### Delete message

```http
  DELETE /message/${messageId}
```

### Like

#### Get like by foreign keys

```http
  GET /like/${userId1}/${userId2}
```

#### Add like

```http
  POST /like
```

#### Delete like

```http
  DELETE /like/${likeId}
```

### Picture

#### Get all user pictures

```http
  GET /picture/all/${userId}
```

#### Get picture

```http
  GET /picture/${pictureName}
```

#### Add picture

```http
  POST /picture
```

#### Delete picture

```http
  DELETE /picture/${pictureId}
```

### Match

#### Get all user matches

```http
  GET /match/all
```