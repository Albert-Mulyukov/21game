import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/* 
<applet code="Client" width=1280 height=720>
</applet>
*/
  
public class Client extends JApplet implements ActionListener { 
	private JButton button_connect;
	private JButton button_disconnect;
	private JButton new_game;
	private JButton take_card;
	private JButton show_cards;
	private JTextField text;
	private final int card_width = 200;
	private final int card_height = 290;

	private DataInputStream dis; 
    private DataOutputStream dos; 
    private Socket s;
    private boolean is_connected;
    private boolean is_in_game;
    private Hand my_hand;
    private Hand bank_hand;

	public void init() {
		setLayout(new FlowLayout());

        button_connect = new JButton("Connect");
        button_connect.addActionListener(this);
        add(button_connect);

        button_disconnect = new JButton("Disconnect");
        button_disconnect.addActionListener(this);
        add(button_disconnect);

        new_game = new JButton("New game");
        new_game.addActionListener(this);
        add(new_game);

        take_card = new JButton("Take card");
        take_card.addActionListener(this);
        add(take_card);

        show_cards = new JButton("Show cards");
        show_cards.addActionListener(this);
        add(show_cards);

        text = new JTextField();
        text.setText("Connect to server to start game");
        text.setEditable(false);
        add(text);
        
        is_connected = false;
        is_in_game = false;
        my_hand = new Hand();
        bank_hand = new Hand();
      	repaint();
	}

	public void start() {
		repaint();
	}

	public void stop() {}

	public void destroy() {
		if (is_connected == true) {
			disconnect();
		}
	}

	public void paint(Graphics g) {
		int width = getSize().width;
        int height = getSize().height;
        g.clearRect(0, 0, width, height);
       
        int stride = 0;
        int card_base_x = 50;
        if (my_hand.getCardsN() != 0) {
        	stride = (width-100) / my_hand.getCardsN();
        }
        for (int i = 0; i < my_hand.getCardsN(); i++) {
        	g.drawImage(getCardImage(my_hand, i), card_base_x, height/2, card_width, card_height, this);
        	card_base_x += stride;
        }

        stride = 0;
        card_base_x = 50;
        if (bank_hand.getCardsN() != 0) {
        	stride = (width-100) / bank_hand.getCardsN();
        }
        for (int i = 0; i < bank_hand.getCardsN(); i++) {
        	g.drawImage(getCardImage(bank_hand, i), card_base_x, 50, card_width, card_height, this);
        	card_base_x += stride;
        }
	}

   	public void actionPerformed(ActionEvent e) {
   		if ((e.getSource() == button_connect) && !is_connected) {
   			connect();
   			text.setText("You are connected to server");
   			repaint();
   		}
   		else if ((e.getSource() == button_disconnect) && is_connected) {
   			disconnect();
   			text.setText("Connect to server to start game");
   			repaint();
   		}
   		else if ((e.getSource() == new_game) && is_connected) {
   			newGameRequest();
   			getBankCards();
   			text.setText("");
   			repaint();
   		}
   		else if ((e.getSource() == take_card) && is_connected && is_in_game) {
   			my_hand.takeCard(cardRequest());
   			repaint();
   		}
   		else if ((e.getSource() == show_cards) && is_connected && is_in_game) {
   			boolean is_win = isWin();
   			getBankCards();
   			if (is_win) {
   				text.setText("WIN! Bank score: " + bank_hand.getScore() + " You score: " + my_hand.getScore());
   			}
   			else {
   				text.setText("LOSE! Bank score: " + bank_hand.getScore() + " You score: " + my_hand.getScore());
   			}
   			repaint();
   		}
   	}

   	private void connect() {
   		try {
			InetAddress ip = InetAddress.getByName("localhost");
		  	s = new Socket(ip, 7777);

		  	dis = new DataInputStream(s.getInputStream()); 
		    dos = new DataOutputStream(s.getOutputStream());

			is_connected = true;
			System.out.println(dis.readUTF());
		}
		catch (Exception e) {
  			e.printStackTrace();
  		}
   	}

   	private void disconnect() {
   		try {
			dos.writeUTF("Exit");
			s.close();
			dis.close();
			dos.close();

			is_connected = false;
			is_in_game = false;
		}
		catch (Exception e) {
  			e.printStackTrace();
  		}
   	}

   	private void newGameRequest() {
   		try {
   			dos.writeUTF("New game");
   			is_in_game = true;
   			my_hand.clear();
   			my_hand.takeCard(cardRequest());
   		}
   		catch (Exception e) {
  			e.printStackTrace();
  		}
   	}

   	private Card cardRequest() {
   		String suit = null;
   		String rank = null;
   		try {
   			dos.writeUTF("Card request");
   			suit = dis.readUTF();
            rank = dis.readUTF();
   		}
   		catch (Exception e) {
  			e.printStackTrace();
  		}
  		return new Card(suit, rank);
   	}

   	private boolean isWin() {
   		boolean is_win = false;
   		try {
   			dos.writeUTF("Who wins");
   			dos.writeInt(my_hand.getScore());
   			is_win = dis.readBoolean();
   			is_in_game = false;
   		}
   		catch (Exception e) {
  			e.printStackTrace();
  		}
  		return is_win;
   	}

   	private void getBankCards() {
   		bank_hand.clear();
   		try {
   			dos.writeUTF("Send cards");
   			int n_cards = dis.readInt();
   			String suit, rank;
   			for (int i = 0; i < n_cards; i++) {
   				suit = dis.readUTF();
            	rank = dis.readUTF();
            	bank_hand.takeCard(new Card(suit, rank));
   			}
   		}
   		catch (Exception e) {
  			e.printStackTrace();
  		}
   	}

   	private Image getCardImage(Hand hand, int index) {
   		Image image = null;
		label: switch (hand.getCard(index).getSuit()) {
			case "C":
				switch (hand.getCard(index).getRank()) {
					case "J": image = getImage(getDocumentBase(), "./PNG-cards/jack_of_clubs2.png"); break label;
					case "Q": image = getImage(getDocumentBase(), "./PNG-cards/queen_of_clubs2.png"); break label;
					case "K": image = getImage(getDocumentBase(), "./PNG-cards/king_of_clubs2.png"); break label;
					case "6": image = getImage(getDocumentBase(), "./PNG-cards/6_of_clubs.png"); break label;
					case "7": image = getImage(getDocumentBase(), "./PNG-cards/7_of_clubs.png"); break label;
					case "8": image = getImage(getDocumentBase(), "./PNG-cards/8_of_clubs.png"); break label;
					case "9": image = getImage(getDocumentBase(), "./PNG-cards/9_of_clubs.png"); break label;
					case "10": image = getImage(getDocumentBase(), "./PNG-cards/10_of_clubs.png"); break label;
					case "A": image = getImage(getDocumentBase(), "./PNG-cards/ace_of_clubs.png"); break label;
				}
			case "D":
				switch (hand.getCard(index).getRank()) {
					case "J": image = getImage(getDocumentBase(), "./PNG-cards/jack_of_diamonds2.png"); break label;
					case "Q": image = getImage(getDocumentBase(), "./PNG-cards/queen_of_diamonds2.png"); break label;
					case "K": image = getImage(getDocumentBase(), "./PNG-cards/king_of_diamonds2.png"); break label;
					case "6": image = getImage(getDocumentBase(), "./PNG-cards/6_of_diamonds.png"); break label;
					case "7": image = getImage(getDocumentBase(), "./PNG-cards/7_of_diamonds.png"); break label;
					case "8": image = getImage(getDocumentBase(), "./PNG-cards/8_of_diamonds.png"); break label;
					case "9": image = getImage(getDocumentBase(), "./PNG-cards/9_of_diamonds.png"); break label;
					case "10": image = getImage(getDocumentBase(), "./PNG-cards/10_of_diamonds.png"); break label;
					case "A": image = getImage(getDocumentBase(), "./PNG-cards/ace_of_diamonds.png"); break label;
				}
			case "H":
				switch (hand.getCard(index).getRank()) {
					case "J": image = getImage(getDocumentBase(), "./PNG-cards/jack_of_hearts2.png"); break label;
					case "Q": image = getImage(getDocumentBase(), "./PNG-cards/queen_of_hearts2.png"); break label;
					case "K": image = getImage(getDocumentBase(), "./PNG-cards/king_of_hearts2.png"); break label;
					case "6": image = getImage(getDocumentBase(), "./PNG-cards/6_of_hearts.png"); break label;
					case "7": image = getImage(getDocumentBase(), "./PNG-cards/7_of_hearts.png"); break label;
					case "8": image = getImage(getDocumentBase(), "./PNG-cards/8_of_hearts.png"); break label;
					case "9": image = getImage(getDocumentBase(), "./PNG-cards/9_of_hearts.png"); break label;
					case "10": image = getImage(getDocumentBase(), "./PNG-cards/10_of_hearts.png"); break label;
					case "A": image = getImage(getDocumentBase(), "./PNG-cards/ace_of_hearts.png"); break label;
				}
			case "S":
				switch (hand.getCard(index).getRank()) {
					case "J": image = getImage(getDocumentBase(), "./PNG-cards/jack_of_spades2.png"); break label;
					case "Q": image = getImage(getDocumentBase(), "./PNG-cards/queen_of_spades2.png"); break label;
					case "K": image = getImage(getDocumentBase(), "./PNG-cards/king_of_spades2.png"); break label;
					case "6": image = getImage(getDocumentBase(), "./PNG-cards/6_of_spades.png"); break label;
					case "7": image = getImage(getDocumentBase(), "./PNG-cards/7_of_spades.png"); break label;
					case "8": image = getImage(getDocumentBase(), "./PNG-cards/8_of_spades.png"); break label;
					case "9": image = getImage(getDocumentBase(), "./PNG-cards/9_of_spades.png"); break label;
					case "10": image = getImage(getDocumentBase(), "./PNG-cards/10_of_spades.png"); break label;
					case "A": image = getImage(getDocumentBase(), "./PNG-cards/ace_of_spades.png"); break label;
				}
		}
    	return image;
   	}
}