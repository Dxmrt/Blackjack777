# Blackjack API

## Overview
This is a Blackjack game API built using Spring Boot, WebFlux, and MongoDB + MySQL (R2DBC).

## Prerequisites
- Java 17+
- MongoDB
- MySQL

## Running the Application
1. Clone the repository.
2. Set up MongoDB and MySQL databases.
3. Configure `application.yml` for the correct database credentials.
4. Run the project


## API Endpoints

- **POST /game/new**: Create a new game.
- **GET /game/{id}**: Get game details.
- **POST /game/{id}/play**: Make a move in a game.
- **DELETE /game/{id}/delete**: Delete a game.
- **GET /ranking**: Get player rankings.
- **PUT /player/{playerId}**: Change player name.

## Swagger Documentation
Visit `/swagger-ui.html` for the full API documentation.
