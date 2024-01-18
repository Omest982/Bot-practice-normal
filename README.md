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
![Untitled (1)](https://github.com/Omest982/Bot-practice-normal/assets/93486447/6693f6e9-4143-4cf5-8c75-e981a3a90e62)

## 🔍 Microservices structure
![image](https://github.com/Omest982/Bot-practice-normal/assets/93486447/8437f952-507f-4551-818c-f29e6437cc3b)


## 👉 API Endpoints


# Bot-practice-normal
Телеграм бот который может сохранять в БД и отдавать обратно фото и документы, регистрировать пользователя через email сообщение.

## Microservices

### dispatcher
Принимает сообщения с телеграмма и определяет в какую очередь RabbitMQ его поместить.
Принимает ответы от микросервиса node и отправляет в чат пользователю.

### node
Отслеживает и принимает сообщения с очередей RabbitMQ и обрабатывает их в соотвествии их типу, перед этим сохраня необработанное сообщение и пользователя в БД.
Для сообщений содержащих фото или документ создает ссылку на скачивание, id документа в которой зашифровано, после сохранения их в БД. Отправляет ссылку на скачивание в очередь RabbitMQ которую отслеживает микросервис dispatcher.
Отвечает за регистрацию пользователя через отправку ссылки на регистрацию на email.

### common-jpa
Содержит все базы данных и репозитории работающие с ними, которые используются в проете.

### common-utils 
Содержит вспомогательные утилиты.(шифрование)

### rest-service
Содержит 2 контроллера
#### FileController:
Отвечает за отдачу файлов и фото из БД пользователюю
#### UserActivationController:
Отвечает за активацию пользователя в программе.

### mail-service
Содержит контроллер который принимает обьект MailParams(описанный в common-utils) и отправляет сообщение на email.
