package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import client.*;
import database.*;

public class QueryCard extends MessageCard implements ActionListener, FocusListener, DocumentListener {

	JLabel header = new JLabel("<html><center><h1>Enter Query<br></h1></center></html>");
	final JButton viewButton = new JButton("View");
	final JButton backButton = new JButton("Back");
	final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JTextField queryField = new JTextField("Enter Query",30);
	boolean queryPrev;

	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		centerPanel.add(queryField);
		add(centerPanel,BorderLayout.CENTER);
		buttonPanel.add(viewButton);
		buttonPanel.add(backButton);
		add(buttonPanel,BorderLayout.SOUTH);
		viewButton.setEnabled(false);
		addActionListeners();
  }
  
  public QueryCard() {
    super();
		setLayout(new BorderLayout());
  }
  
	public void addActionListeners() {
		viewButton.addActionListener(this);
		backButton.addActionListener(this);
		queryField.addFocusListener(this);
		queryField.getDocument().addDocumentListener(this);
	}

	  //clears preset text from fields when cursor enters said field
	public void focusGained(FocusEvent e) {
		if (queryField.isFocusOwner()) queryField.selectAll();
	}

	//not used
	public void focusLost(FocusEvent e) {}

	//controls state of view button, enabling only when query field is filled out
	public void disableIfEmpty(DocumentEvent e) {
		if (e.getDocument() == queryField.getDocument()) viewButton.setEnabled(e.getDocument().getLength() > 0);
	}

	//not used
	public void changedUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void insertUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void removeUpdate(DocumentEvent e) { disableIfEmpty(e); }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewButton) {
			String s = queryField.getText();
			Interface.viewData = Interface.client.requestData(s);
			Interface.switchToCard("view");
		} else if (e.getSource() == backButton) Interface.switchToCard("welcome");
  }
}
