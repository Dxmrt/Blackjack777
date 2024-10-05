package com.blackjack.blackjack777.model;

public class Card {

    private String suit;  // e.g., Hearts, Diamonds, Clubs, Spades
    private String value; // e.g., 2, 3, 4, J, Q, K, A

    // Constructor
    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
    }

    // Getter for suit
    public String getSuit() {
        return suit;
    }

    // Setter for suit
    public void setSuit(String suit) {
        this.suit = suit;
    }

    // Getter for value
    public String getValue() {
        return value;
    }

    // Setter for value
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}
