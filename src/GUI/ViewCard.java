package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import client.*;
import database.*;

public class ViewCard extends MessageCard implements ActionListener {

	final JButton backButton = new JButton("Back");
	final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JScrollPane scrollPanel = new JScrollPane(centerPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	
	final JLabel header = new JLabel("<html>\n<center><h1>Queried Data:</h1></center></html>");
	JLabel dataLabel = new JLabel("data");
	    
	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		centerPanel.add(dataLabel);
		add(scrollPanel,BorderLayout.CENTER);
		buttonPanel.add(backButton);
		add(buttonPanel,BorderLayout.SOUTH);
		addActionListeners();
		initializeData();
  }
  
  public ViewCard() {
    super();
		setLayout(new BorderLayout());
  }
  
	public void addActionListeners() {
		backButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) Interface.switchToCard("welcome");
  }

	private void initializeData() {
		dataLabel.setText(Interface.viewData);
	}
}
