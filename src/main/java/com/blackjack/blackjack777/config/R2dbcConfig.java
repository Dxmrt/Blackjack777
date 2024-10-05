package com.blackjack.blackjack777.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(
        basePackages = "com.blackjack.blackjack777.repository.r2dbc"  // Adjust the package for MySQL R2DBC repositories
)
public class R2dbcConfig {
}
