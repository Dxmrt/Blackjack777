package com.blackjack.blackjack777.service;

import com.blackjack.blackjack777.excepcion.GameNotFoundException;
import com.blackjack.blackjack777.model.Card;
import com.blackjack.blackjack777.model.Deck;
import com.blackjack.blackjack777.model.Game;
import com.blackjack.blackjack777.model.Player;
import com.blackjack.blackjack777.repository.mongo.GameRepository;
import com.blackjack.blackjack777.repository.r2dbc.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j  // For logging
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository; // MongoDB
    private final PlayerRepository playerRepository; // MySQL (R2DBC)

    public Mono<Game> createGame(String playerName) {
        Game game = new Game();
        Deck deck = new Deck(); // Initialize deck once

        // Step 1: Check if the player exists in the database
        return findPlayerByName(playerName)
                .flatMap(player -> {
                    // If player exists, set playerId from the existing player
                    game.setPlayerId(player.getId()); // Ensure you get the ID as a String
                    return Mono.just(player);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // If player does not exist, create and save the player with the given name
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName); // Set the player name
                    return savePlayer(String.valueOf(newPlayer))
                            .doOnNext(savedPlayer -> game.setPlayerId(savedPlayer.getId())); // Set playerId after saving
                }))
                .then(Mono.defer(() -> {
                    // Step 2: Set up game details
                    game.setPlayerHand(new ArrayList<>());
                    game.setDealerHand(new ArrayList<>());
                    game.setStatus("IN_PROGRESS");

                    // Store the deck in the game
                    game.setDeck(deck);

                    // Step 3: Deal initial cards to player and dealer
                    addCardsPlayer(game, deck);
                    addCardsDealer(game, deck);

                    // Step 4: Save the new game to the MongoDB repository
                    return gameRepository.save(game);
                }));
    }

    // Update findPlayer method to search by name
    private Mono<Player> findPlayerByName(String playerName) {
        return playerRepository.findByName(playerName); // Assuming you have a method in PlayerRepository
    }


    private Mono<Player> savePlayer(String playerName) {
        // Create a new player with an auto-generated UUID and save to the R2DBC MySQL database
        Player player = new Player();  // UUID will be automatically generated
        player.setName(playerName);    // Set the player's name
        return playerRepository.save(player);  // Save the player in the database
    }


    private void addCardsPlayer(Game game, Deck deck) {
        game.getPlayerHand().add(deck.dealCard());
        game.getPlayerHand().add(deck.dealCard());
    }

    private void addCardsDealer(Game game, Deck deck) {
        game.getDealerHand().add(deck.dealCard());
        game.getDealerHand().add(deck.dealCard());
    }

    // Player makes a move (hit or stand)
    public Mono<Game> play(String gameId, String playerId, String action) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!game.getPlayerId().equals(playerId)) {
                        return Mono.error(new IllegalStateException("Player ID does not match game"));
                    }
                    log.info("Player {} performs action: {}", playerId, action);
                    return handlePlayerAction(game, action);
                });
    }

    // Handle player action (hit or stand)
    private Mono<Game> handlePlayerAction(Game game, String action) {
        if ("hit".equalsIgnoreCase(action)) {
            return handlePlayerHit(game);
        } else if ("stand".equalsIgnoreCase(action)) {
            return handlePlayerStand(game);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid action"));
        }
    }

    // Handle player hit action
    private Mono<Game> handlePlayerHit(Game game) {
        Deck deck = game.getDeck();  // Use the deck stored in the game
        game.getPlayerHand().add(deck.dealCard());
        updatePlayerScore(game);

        if (game.getPlayerSum() > 21) {
            game.setStatus("GAME_OVER");
            log.info("Player busts with a score of {}.", game.getPlayerSum());
        }

        return gameRepository.save(game);
    }

    // Handle player stand action and play dealer turn
    private Mono<Game> handlePlayerStand(Game game) {
        game.setStatus("PLAYER_STAND");
        log.info("Player stands. Dealer's turn.");
        return playDealerTurn(game);
    }

    // Dealer plays until they reach a score of 17 or more
    private Mono<Game> playDealerTurn(Game game) {
        Deck deck = game.getDeck();  // Use the deck stored in the game
        while (game.getDealerSum() < 17) {
            game.getDealerHand().add(deck.dealCard());
            updateDealerScore(game);
        }
        determineWinner(game);
        return gameRepository.save(game);
    }

    // Determine the winner between player and dealer
    private void determineWinner(Game game) {
        if (game.getDealerSum() > 21 || game.getPlayerSum() > game.getDealerSum()) {
            game.setStatus("PLAYER_WINS");
            log.info("Player wins with score: {}", game.getPlayerSum());
        } else if (game.getPlayerSum() == game.getDealerSum()) {
            game.setStatus("DRAW");
            log.info("Game is a draw.");
        } else {
            game.setStatus("PLAYER_LOSES");
            log.info("Dealer wins with score: {}", game.getDealerSum());
        }
    }

    // Calculate and update the player's score
    private void updatePlayerScore(Game game) {
        game.setPlayerSum(calculateScore(game.getPlayerHand()));
    }

    // Calculate and update the dealer's score
    private void updateDealerScore(Game game) {
        game.setDealerSum(calculateScore(game.getDealerHand()));
    }

    // Calculate the score for a hand of cards
    private int calculateScore(List<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            switch (card.getValue()) {
                case "A":
                    total += 11;  // Initially count Ace as 11
                    aceCount++;   // Keep track of the number of Aces
                    break;
                case "K":
                case "Q":
                case "J":
                    total += 10;  // Face cards are worth 10
                    break;
                default:
                    total += Integer.parseInt(card.getValue());  // Number cards (2-10) are worth their face value
                    break;
            }
        }

        // Adjust Ace values if the total score exceeds 21
        while (aceCount > 0 && total > 21) {
            total -= 10;  // Convert an Ace from 11 to 1
            aceCount--;   // One less Ace to potentially adjust
        }

        return total;
    }

    // Get game by ID, throw GameNotFoundException if not found
    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundException(gameId)));
    }

    // Get players ranking based on their score
    public Flux<Player> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc();
    }

    // Change a player's name
    public Mono<Player> changeName(String playerId, String newPlayerName) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setName(newPlayerName);
                    log.info("Player {}'s name changed to {}", playerId, newPlayerName);
                    return playerRepository.save(player);
                });
    }

    // Delete a game by ID
    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.deleteById(gameId);
    }
}
