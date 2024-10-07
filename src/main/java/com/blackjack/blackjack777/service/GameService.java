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
        Deck deck = new Deck(); // Initialize and shuffle the deck in memory

        // Step 1: Check if the player exists in the database
        return findPlayerByName(playerName)
                .flatMap(player -> {
                    // Set playerId from the existing player
                    game.setPlayerId(player.getId());
                    return Mono.just(player);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // Create and save the player if they don't exist
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    return savePlayer(newPlayer)
                            .doOnNext(savedPlayer -> game.setPlayerId(savedPlayer.getId()));
                }))
                .then(Mono.defer(() -> {
                    // Deal initial cards to player and dealer, no need to store the deck
                    game.setPlayerHand(new ArrayList<>());
                    game.setDealerHand(new ArrayList<>());
                    game.setStatus("IN_PROGRESS");

                    addCardsPlayer(game, deck);  // Deal cards to player from shuffled deck
                    addCardsDealer(game, deck);  // Deal cards to dealer from shuffled deck

                    // Calculate playerSum, dealerSum, playerAce, and dealerAce
                    int playerSum = calculateScore(game.getPlayerHand());
                    int dealerSum = calculateScore(game.getDealerHand());
                    int playerAceCount = countAces(game.getPlayerHand());
                    int dealerAceCount = countAces(game.getDealerHand());

                    // Set these values in the game object
                    game.setPlayerSum(playerSum);
                    game.setDealerSum(dealerSum);
                    game.setPlayerAce(playerAceCount);
                    game.setDealerAce(dealerAceCount);

                    // Save the new game to MongoDB without storing the deck itself
                    return gameRepository.save(game);
                }));
    }

    private int countAces(List<Card> hand) {
        int aceCount = 0;
        for (Card card : hand) {
            if ("A".equals(card.getValue())) {
                aceCount++;
            }
        }
        return aceCount;
    }
    public Mono<Game> play(String gameId, Long playerId, String action) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    // Convert playerId to Long
                    if (!game.getPlayerId().equals(playerId)) {
                        return Mono.error(new IllegalStateException("Player ID does not match the game"));
                    }

                    Deck deck = new Deck();  // Initialize a new deck in memory for the play action
                    return handlePlayerAction(game, action, deck);  // Pass deck in memory
                });
    }



    // Update findPlayer method to search by name
    private Mono<Player> findPlayerByName(String playerName) {
        return playerRepository.findByName(playerName); // Assuming you have a method in PlayerRepository
    }


    private Mono<Player> savePlayer(Player player) {
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


    // Handle player action (hit or stand)
    private Mono<Game> handlePlayerAction(Game game, String action, Deck deck) {
        if ("hit".equalsIgnoreCase(action)) {
            return handlePlayerHit(game, deck);
        } else if ("stand".equalsIgnoreCase(action)) {
            return handlePlayerStand(game, deck);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid action"));
        }
    }


    // Handle player hit action
    private Mono<Game> handlePlayerHit(Game game, Deck deck) {
        // Deal a card to the player from the in-memory deck
        game.getPlayerHand().add(deck.dealCard());
        updatePlayerScore(game);

        if (game.getPlayerSum() > 21) {
            game.setStatus("GAME_OVER");
            log.info("Player busts with a score of {}.", game.getPlayerSum());
        }

        return gameRepository.save(game);  // Save the updated game state
    }


    // Handle player stand action and play dealer turn
    private Mono<Game> handlePlayerStand(Game game, Deck deck) {
        game.setStatus("PLAYER_STAND");
        log.info("Player stands. Dealer's turn.");
        return playDealerTurn(game, deck);
    }


    // Dealer plays until they reach a score of 17 or more
    private Mono<Game> playDealerTurn(Game game, Deck deck) {
        while (game.getDealerSum() < 17) {
            game.getDealerHand().add(deck.dealCard());  // Deal card to dealer from in-memory deck
            updateDealerScore(game);
        }
        determineWinner(game);
        return gameRepository.save(game);  // Save the updated game state
    }


    // Determine the winner between player and dealer
    private void determineWinner(Game game) {
        Long playerId = game.getPlayerId();

        if (game.getDealerSum() > 21 || game.getPlayerSum() > game.getDealerSum()) {
            game.setStatus("PLAYER_WINS");
            log.info("Player wins with score: {}", game.getPlayerSum());

            // Incrementar el puntaje del jugador si gana
            playerRepository.findById(playerId)
                    .flatMap(player -> {
                        int newScore = player.getScore() + 10;  // Incrementar el score en 10
                        log.info("Updating score for player {} to {}", player.getName(), newScore);
                        player.setScore(newScore);
                        return playerRepository.save(player);  // Guardar en MySQL
                    }).subscribe();

        } else if (game.getPlayerSum() == game.getDealerSum()) {
            game.setStatus("DRAW");
            log.info("Game is a draw.");
            // No cambiar el puntaje en un empate

        } else {
            game.setStatus("PLAYER_LOSES");
            log.info("Dealer wins with score: {}", game.getDealerSum());

            // Decrementar el puntaje si pierde
            playerRepository.findById(playerId)
                    .flatMap(player -> {
                        int newScore = Math.max(0, player.getScore() - 5);  // Decrementar el score (pero no por debajo de 0)
                        log.info("Updating score for player {} to {}", player.getName(), newScore);
                        player.setScore(newScore);
                        return playerRepository.save(player);  // Guardar en MySQL
                    }).subscribe();
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
    public Mono<Player> changeName(Long playerId, String newPlayerName) {
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
