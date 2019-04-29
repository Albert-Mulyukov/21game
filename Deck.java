import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private String[] suits = {"C", "D", "H", "S"};
	private String[] ranks = {"J", "Q", "K", "6", "7", "8", "9", "10", "A"};
	private ArrayList<Card> deck;

	public Deck() {
		deck = new ArrayList<Card>();
		for (String suit : suits) {
			for (String rank : ranks) {
				deck.add(new Card(suit, rank));
			}
		}
	}

	public void shuffle() {
		ArrayList<Card> shuffled_deck = new ArrayList<Card>();
		Random rand = new Random();
		int deck_size = deck.size();

		for (int new_index = 0; new_index < deck_size; new_index++) {
			int index = rand.nextInt(deck_size - new_index);
			Card card = deck.get(index);
			deck.remove(index);
			shuffled_deck.add(new_index, card);
		}
		deck = shuffled_deck;
	}

	public Card getCard() {
		Card card = deck.get(0);
		deck.remove(0);
		return card;
	}

	public int getDeckSize() {
		return deck.size();
	}
}