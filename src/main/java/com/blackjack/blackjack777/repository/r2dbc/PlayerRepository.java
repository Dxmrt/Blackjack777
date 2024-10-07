package com.blackjack.blackjack777.repository.r2dbc;


import com.blackjack.blackjack777.model.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveCrudRepository<Player, Long> {
    // Custom query to fetch players ordered by score in descending order
    Flux<Player> findAllByOrderByScoreDesc();

    Mono<Player> findByName(String playerName);
}
