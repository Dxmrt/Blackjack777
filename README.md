
---

🎮 Blackjack777

Blackjack777 is a reactive API-based Blackjack game built with Spring Boot, leveraging Spring WebFlux for its reactive components and connecting to two different data stores—MongoDB and MySQL. The application manages Blackjack games, handles player data, and provides a clean, efficient interface for various Blackjack-related actions via RESTful API endpoints.

The project is designed to provide a seamless, high-performance experience using reactive programming principles, making it suitable for modern, scalable applications. Additionally, it implements various best practices, including exception handling, testing, and API documentation.


---

📜 Table of Contents

Project Overview

Key Features

Tech Stack

API Endpoints

Data Storage

Installation

Testing

Known Issues

Future Improvements

Contributing



---

📝 Project Overview

The Blackjack777 API provides a way for users to play a simplified version of the Blackjack card game through RESTful endpoints. Each game is created, played, and managed entirely through API requests. The application is divided into player management (using MySQL) and game management (using MongoDB). This separation ensures a clear distinction between game state data and persistent player data.

The application includes:

Management of multiple game sessions.

Real-time score calculation and game state handling.

Player ranking system based on game outcomes.

Reactive architecture with Spring WebFlux for scalability and high throughput.



---

🚀 Key Features

Reactive API: Built with Spring WebFlux for non-blocking, reactive endpoints.

Dual Data Store: Player data is stored in MySQL (using R2DBC), while game sessions are stored in MongoDB.

API Documentation: Auto-generated documentation using Swagger.

Exception Handling: Global exception handling for cleaner error responses.

Testing: Includes unit and integration tests with JUnit and Mockito.

Postman Support: Endpoints can be easily tested using Postman.



---

⚙️ Tech Stack

Spring Boot: Primary framework for building the application.

Spring WebFlux: For creating reactive, non-blocking API endpoints.

MongoDB: Used for storing game-related data (games, game states, etc.).

MySQL with R2DBC: For storing player data.

Swagger: For documenting the API.

JUnit & Mockito: For unit and integration testing.



---

🔗 API Endpoints

🎲 Game Endpoints

Create a new game: POST /game/new

Get game details: GET /game/{id}

Play a move: POST /game/{id}/play

Delete a game: DELETE /game/{id}/delete


🧑‍💼 Player Endpoints

Fetch player ranking: GET /ranking

Change a player’s name: PUT /player/{playerId}


📋 Example Requests

Create a New Game

POST /game/new
Content-Type: application/json
{
  "playerName": "John Doe"
}

Fetch Player Ranking

GET /ranking

Play a Move in a Game

POST /game/{id}/play
Content-Type: application/json
{
  "action": "hit"  // or "stand"
}


---

🗄️ Data Storage

🧑 Player Data (MySQL):

Table: players

Managed using R2DBC for reactive interactions.

Contains player information such as player ID, name, and ranking score.


🎮 Game Data (MongoDB):

Collection: games

Stores active games and game states such as the deck, player hands, and dealer hands.



---

🛠️ Installation

Prerequisites

Java 17 or later

Maven 3.6+

MongoDB and MySQL databases installed and configured.


Clone the Repository

git clone https://github.com/Dxmrt/Blackjack777.git
cd Blackjack777

Configure Databases

1. Set up a MySQL database and MongoDB instance.


2. Update the application.yml configuration file with your database connection details for MySQL and MongoDB.



Build and Run the Application

mvn clean install
mvn spring-boot:run


---

🧪 Testing

The project includes unit tests and integration tests using JUnit and Mockito. To run the tests, simply use the Maven test command:

mvn test

You can also test the API manually using Postman. The Swagger documentation can be accessed at http://localhost:8080/swagger-ui.html when the application is running.


---

🐞 Known Issues

Swagger Compatibility: Some issues with Swagger may cause problems when testing the POST /game/new endpoint. This issue does not occur when using Postman.

Game Session Persistence: The current implementation does not persist the deck between rounds. The deck is kept in memory for simplicity, which may lead to some limitations in scaling.



---

🔮 Future Improvements

Modularization: A future version of the API will include better separation of concerns and modularization. This will involve breaking down the monolithic structure into smaller, independently deployable modules.

Game Module: Will handle all operations related to the game state and game actions, interacting with MongoDB.

Player Module: Will manage player-related operations such as ranking and profile updates, interacting with MySQL.


Service Layer Refactoring: Current services will be further split to handle specific responsibilities, ensuring clean and manageable code.

Deck Persistence: Future updates may introduce more complex game session management, possibly allowing for persistent deck states or multi-game sessions.

Enhanced Testing: The next iteration will focus on more robust testing, including additional integration tests and performance testing for scaling scenarios.



---

🤝 Contributing

We welcome contributions! Please follow the steps below to contribute:

1. Fork the repository.


2. Create a new branch (git checkout -b feature/your-feature).


3. Make your changes.


4. Commit and push your changes (git commit -am 'Add your feature').


5. Create a pull request.




---
