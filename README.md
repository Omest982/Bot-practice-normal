<h2 align="center">🤖Telegram bot🤖</h2>

## 👋 Introduction
Telegram Bot created for self study. The bot has abilities to process prepared commands, save documents and photos in the database, create links for downloading documents and photos and register a user via email message.
## 👩‍💻 Technologies Used
* ☕ Java
* 🌱 Spring Boot
* 🌱🛢️ Spring Data JPA
* 🌱📫 Spring Mail
* 📰 Swagger
* 🐘 PostgreSQL
* 🐋 Docker
* 🌶️ Lombok
* 🐰 RabbitMQ
* ✈️ Telegram API

## 🛢️ Database structure
![Bot-db](https://github.com/Omest982/Bot-practice-normal/assets/93486447/c0563526-c2f8-4094-9051-12b54e8b53fc)


## 🔍 Microservices structure
![image](https://github.com/Omest982/Bot-practice-normal/assets/93486447/8437f952-507f-4551-818c-f29e6437cc3b)


## 👉 API Endpoints

| HTTP method | Endpoint         | Function                            |
| ----------- | ---------------- | ----------------------------------- |
| POST        | /callback/update | Receives message from telegram chat |
| GET         | /file/get-doc    | Get document                        |
| GET         | /file/get-photo  | Get photo                           |
| POST        | /mail/send       | Send mail message                   |


## ➕ Additional microservices

### common-jpa
Contains all entities and repositories that project needs. 

### common-utils 
Contains additional utilities.(hashing for example)

### common-rabbitmq
Contains all rabbitmq queries names.
