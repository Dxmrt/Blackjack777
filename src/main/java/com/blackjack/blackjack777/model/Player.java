package com.blackjack.blackjack777.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table; // Import the Table annotation

import java.util.UUID;

@Setter
@Getter
@Table("players")  //SQL table name
public class Player {

    @Id
    private Long id;

    private String name;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
