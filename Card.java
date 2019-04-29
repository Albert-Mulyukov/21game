public class Card {
	private String suit;
	private String rank;
	private int score;

	public Card(String suit, String rank) {
		this.suit = suit;
		this.rank = rank;
		
		switch (rank) {
			case "J":
				score = 2;
				break;
			case "Q":
				score = 3;
				break;
			case "K":
				score = 4;
				break;
			case "A":
				score = 11;
				break;
			default:
				score = Integer.parseInt(rank);
		}
	}

	public String getSuit() {
		return suit;
	}

	public String getRank() {
		return rank;
	}

	public int getScore() {
		return score;
	}
}