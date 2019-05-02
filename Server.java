import java.net.*;
import java.io.*;
  
public class Server { 
	public static void main(String[] args) throws IOException {
		Deck deck = new Deck();
		deck.shuffle();
		ServerSocket ss = new ServerSocket(7777);
      	while (true) {
      		Socket s = null;
      		try {
      			s = ss.accept();
      			System.out.println("A new client connected : " + s);

      			DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

      			Thread t = new ClientHandler(s, dis, dos, deck);
      			t.start();
      		}
      		catch (Exception e) {
      			s.close();
      			e.printStackTrace();
      		}
      	}
	}
}

class ClientHandler extends Thread {
	private final DataInputStream dis; 
    private final DataOutputStream dos; 
    private final Socket s;
    private Deck deck;
    private Hand bank_hand;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Deck deck) {
    	this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.deck = deck;
        bank_hand = new Hand();
    }

    @Override
    public void run() {
    	String received; 
        String toreturn;

        try {
        	dos.writeUTF("Hello from server!");
        	loop: while (true) {
        		Card card_tmp;
        		switch (dis.readUTF()) {
        			case "Exit":
        				System.out.println("Client " + s + " connection closed"); 
                    	s.close(); 
                    	break loop;
                    case "New game":
                    	deck = new Deck();
                    	deck.shuffle();
                    	bank_hand.clear();
                    	bank_hand.takeCard(deck.getCard());
                    	break;
                    case "Card request":
                    	card_tmp = deck.getCard();
                    	dos.writeUTF(card_tmp.getSuit());
                    	dos.writeUTF(card_tmp.getRank());
                    	break;
                    case "Send cards":
                    	dos.writeInt(bank_hand.getCardsN());
                    	for (int i = 0; i < bank_hand.getCardsN(); i++) {
                    		card_tmp = bank_hand.getCard(i);
                    		dos.writeUTF(card_tmp.getSuit());
                    		dos.writeUTF(card_tmp.getRank());
                    	}
                    	break;
                    case "Who wins":
                    	boolean is_player_win = false;
                    	int player_score = dis.readInt();
                    	int bank_score = 99;
                    	if (player_score <= 21) {
                    		while (bank_hand.getScore() <= 16) {
                    			bank_hand.takeCard(deck.getCard());
                    		}
                    		bank_score = bank_hand.getScore();
                    		if ((bank_score > 21) || (player_score > bank_score)) {
                    			is_player_win = true;
                    		}
                    	}
                    	dos.writeBoolean(is_player_win);
                    	break;
                    default:
                    	System.out.println("Unknown command"); 
        		}
        		
        	}

        	dis.close(); 
            dos.close();
        }
    	catch (IOException e) {
  			e.printStackTrace();
  		}
    }
}