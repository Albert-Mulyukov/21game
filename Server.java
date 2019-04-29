import java.net.*;
import java.io.*;
  
public class Server { 
	public static void main(String[] args) throws IOException {
		Deck deck = new Deck();
		deck.shuffle();
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

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Deck deck) {
    	this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.deck = deck;
    }

    @Override
    public void run() {
    	String received; 
        String toreturn;

        try {
        	dos.writeUTF("Hello from server!");
        	loop: while (true) {
        		switch (dis.readUTF()) {
        			case "Exit":
        				System.out.println("Client " + s + " connection closed"); 
                    	s.close(); 
                    	break loop;
                    case "Card Request":
                    	Card card = deck.getCard();
                    	String suit = card.getSuit();
                    	String rank = card.getRank();
                    	dos.writeUTF(suit);
                    	dos.writeUTF(rank);
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