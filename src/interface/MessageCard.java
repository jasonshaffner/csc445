import java.awt.*;
import javax.swing.*;

public class MessageCard extends JPanel {
  public void initializeThisCard() {
    // card initialization code here
    System.out.println("Initializing pane...");
  }

	public void initializeThisCard() {}
  
  public MessageCard() {
    super();
  }
  
  public MessageCard(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }
  
  public MessageCard(LayoutManager layout) {
    super(layout);
  }
  
  public MessageCard(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }
}
