package com.blackjack.blackjack777.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
        basePackages = "com.blackjack.blackjack777.repository.mongo"  // Adjust the package for MongoDB repositories
)
public class MongoConfig {
}
