package Server;

import java.util.ArrayList;

public class Hand {

	private boolean bust = false;
	public boolean stay = false;
	private int total = 0;

	private ArrayList<Card> card;
	private ArrayList<Card> aces;

	public Hand(Card card1, Card card2) {
		card = new ArrayList<Card>();
		aces = new ArrayList<Card>();

		if (card1.rank == "1") {
			aces.add(card1);
		} else {
			card.add(card1);
		}

		if (card2.rank == "1") {
			aces.add(card2);
		} else {
			card.add(card2);
		}

		set_total();
	}

	public int get_total() {
		return total;
	}

	/// get more card
	public void hit(Card new_card) {

		if (new_card.rank == "1") {
			aces.add(new_card);
		} else {
			card.add(new_card);
		}

		if (aces.size() != 0) {
			set_total();
		} else if (new_card.rank == "11" || new_card.rank == "12" || new_card.rank == "13") {
			total += 10;
		} else {
			total += Integer.parseInt(new_card.rank);
		}

		check_bust();

	}

	/// calculate total
	private void set_total() {

		total = 0;
		for (Card c : card) {
			String r = c.rank;
			if (r == "11" || r == "12" || r == "13") {
				total += 10;
			} else {
				total += Integer.parseInt(r);
			}

		}

		for (Card a : aces) {
			if (total <= 10) {
				total += 11;
			} else {
				total += 1;
			}

		}
	}

	public boolean check_bust() {
		if (total > 21) {
			bust = true;
		} else {
			bust = false;
		}

		return bust;
	}

}
