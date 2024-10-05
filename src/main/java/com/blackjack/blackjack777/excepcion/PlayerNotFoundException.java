package com.blackjack.blackjack777.excepcion;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String playerId) {
        super("Player with ID " + playerId + " not found.");
    }
}
