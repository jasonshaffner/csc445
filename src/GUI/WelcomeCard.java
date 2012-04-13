package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class WelcomeCard extends MessageCard implements ActionListener {

	JLabel header = new JLabel("<html><center><h1>Choose whether to Add, Remove or View Data<br></h1></center></html>");
	JButton addButton = new JButton("Add");
	JButton removeButton = new JButton("Remove");
	JButton viewButton = new JButton("View");
	JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(viewButton);
		add(buttonPanel,BorderLayout.SOUTH);
		addActionListeners();
  }
  
  public WelcomeCard() {
    super();
		setLayout(new BorderLayout());
  }
  
	public void addActionListeners() {
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		viewButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) Interface.switchToCard("add");
		if (e.getSource() == removeButton) Interface.switchToCard("remove");
		if (e.getSource() == viewButton) Interface.switchToCard("view");
		
  }
}
