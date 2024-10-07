package com.blackjack.blackjack777.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.List;

@Data
@Document(collection = "games")
public class Game {

    @Id
    private String id;
    private Long playerId;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private String status;
    private int playerSum;
    private int dealerSum;
    private int playerAce;
    private int dealerAce;



    @Transient
    @JsonIgnore
    private Deck deck;

}
