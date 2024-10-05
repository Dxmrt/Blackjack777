package com.blackjack.blackjack777.repository.mongo;


import com.blackjack.blackjack777.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
