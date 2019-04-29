import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards;
	private int score;

	public Hand() {
		cards = new ArrayList<Card>();
		score = 0;
	}

	public void takeCard(Card card) {
		cards.add(card);
		score += card.getScore();
	}

	public void clear() {
		cards.clear();
		score = 0;
	}

	public int getScore() {
		return score;
	}

	public Card getCard(int index) {
		if (index < cards.size()) {
			return cards.get(index);
		}
		else {
			return null;
		}
	}

	public int getCardsN() {
		return cards.size();
	}
}
