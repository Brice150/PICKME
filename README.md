<div align="center">
<img height="130px" width="130px" src="./src/assets/images/Logo.png">
</div>
  
# PICK ME, a dating web application

This application is a Full-Stack demo of my skills in Angular and Spring Boot.

## Pages Features

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