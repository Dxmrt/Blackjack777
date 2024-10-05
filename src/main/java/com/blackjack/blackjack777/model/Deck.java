package com.blackjack.blackjack777.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    // Constructor to initialize and shuffle the deck
    public Deck() {
        this.cards = initializeDeck();
        shuffleDeck();
    }

    // Initializes a deck of 52 standard playing cards
    private List<Card> initializeDeck() {
        List<Card> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    // Shuffles the deck
    private void shuffleDeck() {
        Collections.shuffle(this.cards);
    }

    // Method to deal a card from the top of the deck
    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty, cannot deal more cards.");
        }
        return cards.remove(0);  // Removes and returns the top card
    }
}
