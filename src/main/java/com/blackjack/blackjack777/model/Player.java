package com.blackjack.blackjack777.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table; // Import the Table annotation

import java.util.UUID;

@Setter
@Getter
@Table("players")  // Specify the SQL table name
public class Player {

    @Id  // This tells R2DBC to use this field as the unique identifier
    private String id;   // Player ID will be a generated UUID

    private String name;
    private int score;

    // Constructor that generates a new UUID for the player
    public Player() {
        this.id = UUID.randomUUID().toString();  // Generate a unique UUID as a String
    }
}
