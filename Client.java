import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;

/* 
<applet code="Client" width=1280 height=720>
</applet>
*/
  
public class Client extends JApplet implements ActionListener { 
	private JButton button_connect;
	private JButton button_disconnect;
	private JButton take_card;
	private JTextField text;
	private DataInputStream dis; 
    private DataOutputStream dos; 
    private Socket s;
    private boolean is_connected;
    private Hand hand;

	public void init() {
		setLayout(new FlowLayout());

        button_connect = new JButton("Connect");
        button_connect.addActionListener(this);
        add(button_connect);

        button_disconnect = new JButton("Disconnect");
        button_disconnect.addActionListener(this);
        add(button_disconnect);

        take_card = new JButton("Take card");
        take_card.addActionListener(this);
        add(take_card);

        text = new JTextField();
        text.setText("Connect to server to start game");
        text.setEditable(false);
        add(text);
        
        is_connected = false;
        hand = new Hand();
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
        if (hand.getCardsN() != 0) {
        	stride = (width-100) / hand.getCardsN();
        }
       	int card_base_x = 50;
        for (int i = 0; i < hand.getCardsN(); i++) {
        	g.drawImage(getCardImage(i), card_base_x, height/2, 250, 363, this);
        	card_base_x += stride;
        }
	}

   	public void actionPerformed(ActionEvent e) {
   		if ((e.getSource() == button_connect) && (is_connected == false)) {
   			connect();
   			text.setText("You are connected to server");
   			repaint();
   		}
   		else if ((e.getSource() == button_disconnect) && (is_connected == true)) {
   			disconnect();
   			text.setText("Connect to server to start game");
   			repaint();
   		}
   		else if ((e.getSource() == take_card) && (is_connected == true)) {
   			hand.takeCard(cardRequest());
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
		}
		catch (Exception e) {
  			e.printStackTrace();
  		}
   	}

   	private Card cardRequest() {
   		String suit = null;
   		String rank = null;
   		try {
   			dos.writeUTF("Card Request");
   			suit = dis.readUTF();
            rank = dis.readUTF();
   		}
   		catch (Exception e) {
  			e.printStackTrace();
  		}
  		return new Card(suit, rank);
   	}

   	private Image getCardImage(int index) {
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