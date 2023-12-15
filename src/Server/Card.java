package Server;

public class Card {
	public String rank;
	public String suit;
	
	public Card(String card_val, String suit) {
		this.rank = card_val;
		this.suit = suit;
	}

	public String to_str() {
        return this.suit + " of " + this.rank;
    }
	
	public String getSuit() {
        return suit;
    }
	
	public String getRank() {
        return rank;
    }

}
