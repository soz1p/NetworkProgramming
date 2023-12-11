package Server;

import java.util.Random;

public class Deck {

  public Card[] deck;
  private int counter;
  private static final int CARDS_LEN = 52;
  private static final Random rand = new Random();

  /// create new Deck
  public Deck() {
    String[] rank = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };
    String[] suit = {
        "Heart",
        "Spade",
        "Diamond",
        "Club",
    };

    deck = new Card[CARDS_LEN];
    this.counter = 0;

    for (int i = 0; i < deck.length; i++) {
      deck[i] = new Card(rank[i % 13], suit[i / 13]);
    }
  }

  /// shuffle cards of deck
  public void shuffle() {
    counter = 0;

    for (int idx = 0; idx < this.deck.length; idx++) {
      int random_idx = rand.nextInt(CARDS_LEN);
      Card temp = deck[idx];
      deck[idx] = deck[random_idx];
      deck[random_idx] = temp;
    }

  }

  /// deal one card
  public Card deal() {
    return deck[counter++];
  }

}

class Card {
  public String rank;
  public String suit;

  public Card(String card_val, String suit) {
    this.rank = card_val;
    this.suit = suit;
  }

  public String to_str() {
    return this.suit + "," + this.rank;
  }

}
