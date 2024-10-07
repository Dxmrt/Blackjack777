package com.blackjack.blackjack777.controller;

import com.blackjack.blackjack777.model.Game;
import com.blackjack.blackjack777.model.Player;
import com.blackjack.blackjack777.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        String playerName = payload.get("playerName");

        return gameService.createGame(playerName)

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
    @Operation(summary = "Delete a game", description = "Delete a game by ID")
    public Mono<ResponseEntity<String>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.ok("Game deleted")))  //
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Couldn't find game with ID " + id)));  //
    }


    @PostMapping("/{id}/play")
    @Operation(summary = "Play a game", description = "Make a move in the game by ID")
    public Mono<ResponseEntity<Game>> play(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Long playerId = ((Number) payload.get("playerId")).longValue();  // Extraer playerId como Long
        String action = (String) payload.get("action");

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
    @Operation(summary = "Change player name", description = "Change the name of a player by ID")
    public Mono<ResponseEntity<Player>> changeName(@PathVariable Long playerId, @RequestBody Map<String, String> payload) {
        // Extraer el nuevo nombre del jugador desde el payload
        String newPlayerName = payload.get("newName");

        // Validar que el nombre no sea nulo o vacío
        if (newPlayerName == null || newPlayerName.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(null));  // Retornar error 400 si el nombre es inválido
        }

        // Llamar al servicio para cambiar el nombre del jugador
        return gameService.changeName(playerId, newPlayerName)
                .flatMap(player -> Mono.just(ResponseEntity.ok(player)))  // Mapear directamente a ResponseEntity.ok
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Retornar 404 si no se encuentra el jugador
    }


}
