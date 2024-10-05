package com.blackjack.blackjack777.controller;

import com.blackjack.blackjack777.model.Game;
import com.blackjack.blackjack777.model.Player;
import com.blackjack.blackjack777.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "Game Controller", description = "Controller for managing Blackjack games")
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    @Operation(summary = "Create a new game", description = "Creates a new Blackjack game for a player")
    public Mono<ResponseEntity<Game>> createGame(@RequestBody Map<String, String> payload) {
        String playerId = payload.get("playerId");
        return gameService.createGame(playerId)
                .map(game -> ResponseEntity.status(201).body(game));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a game", description = "Fetch a game by its ID")
    public Mono<ResponseEntity<Game>> getGame(@PathVariable String id) {
        return gameService.getGame(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Return 404 if game is not found
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a game", description = "Deletes a game by its ID")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{id}/play")
    @Operation(summary = "Play a game", description = "Make a move in the game by ID")
    public Mono<ResponseEntity<Game>> play(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String playerId = payload.get("playerId");
        String action = payload.get("action");
        return gameService.play(id, playerId, action)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking")
    @Operation(summary = "Get player ranking", description = "Fetch the ranking of players based on their scores")
    public Flux<Player> getRanking() {
        return gameService.getRanking();
    }

    // PUT endpoint to change player name
    @PutMapping("/player/{playerId}")
    @Operation(summary = "Change player name", description = "Change the name of a player in a game")
    public Mono<ResponseEntity<Player>> changeName(@PathVariable String playerId, @RequestBody Map<String, String> payload) {
        String newPlayerId = payload.get("newPlayerId");
        return gameService.changeName(playerId, newPlayerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Return 404 if player is not found
    }
}
