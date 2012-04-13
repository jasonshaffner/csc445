import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.applet.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class Interface extends JApplet {
	private static JPanel cardpanel = new JPanel(new CardLayout());
	private static Map<String, MessageCard> cards = new HashMap<String, MessageCard>();
	static Client client = new Client();
  
  public void init() {
    System.out.println("Initializing...");
     try {
      javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          createGUI();
        }
      });
    } catch (Exception e) {
      System.err.println("createGUI didn't successfully complete");
			e.printStackTrace();
    }
    System.out.println("Initialized.");
  }
 
  public void stop() {
    // stub
  }
  
  public static void switchToCard(String cardname) {
    CardLayout cl = (CardLayout)(cardpanel.getLayout());
    cl.show(cardpanel, cardname);
    initializeCard(cardname);
  }
  
  public static void addCard(MessageCard card, String cardname) {
    cardpanel.add(card, cardname);
    cards.put(cardname, card);
  }
  
  public static void initializeCard(String cardname) {
    cards.get(cardname).initializeThisCard();
  }
	
  public void createGUI() {
    // add cards here
    WelcomeCard welcomeCard = new WelcomeCard();
    
    addCard(welcomeCard, "welcome");
    
    // add card
		AddCard addCard = new AddCard();
		addCard(addCard, "add");
    
    // remove card
    RemoveCard removeCard = new RemoveCard();
    addCard(removeCard, "remove");
    
 /*   // query card
    QueryCard queryCard = new QueryCard();
		addCard(queryCard, "query");
    
    // view cards
		ViewCard viewCard = new ViewCard();
		addCard(viewCard, "view");
   */ 
    // don't touch this stuff
    this.getContentPane().add(cardpanel);
    this.validate();
    switchToCard("welcome");
  }
}
