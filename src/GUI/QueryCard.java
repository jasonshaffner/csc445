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

	JLabel header = new JLabel("<html><center><h1>Enter Date Range (and optionally city) to View<br></h1></center></html>");
	final JButton viewButton = new JButton("View");
	final JButton backButton = new JButton("Back");
	final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JTextField dateStartField = new JTextField("Start Date (format YYYYMMDD)",15);
	JTextField dateEndField = new JTextField("End Date (format YYYYMMDD)",15);
	JTextField cityField = new JTextField("City (Optional)",15);

	boolean dateStartPrev = false;
	boolean dateEndPrev = false;
	boolean cityPrev = false;
	boolean dateStartFilled = false;
	boolean dateEndFilled = false;

	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		centerPanel.add(dateStartField);
		centerPanel.add(dateEndField);
		centerPanel.add(cityField);
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
		dateStartField.addFocusListener(this);
		dateStartField.getDocument().addDocumentListener(this);
		dateEndField.addFocusListener(this);
		dateEndField.getDocument().addDocumentListener(this);
		cityField.addFocusListener(this);
	}

	  //clears preset text from fields when cursor enters said field
	public void focusGained(FocusEvent e) {
		if (dateStartField.isFocusOwner()) {
			if (!dateStartPrev) {
				dateStartField.setText("");
				dateStartPrev = true;
			} else dateStartField.selectAll();
		} else if (dateEndField.isFocusOwner()) {
			if (!dateEndPrev) {
				dateEndField.setText("");
				dateEndPrev = true;
			} else dateEndField.selectAll();
		} 
	}

	//not used
	public void focusLost(FocusEvent e) {}

	//controls state of add button, enabling only when all required fields are filled out
	public void disableIfEmpty(DocumentEvent e) {
		if (e.getDocument() == dateStartField.getDocument()) dateStartFilled = e.getDocument().getLength() > 0;
		if (e.getDocument() == dateEndField.getDocument()) dateEndFilled = e.getDocument().getLength() > 0;
		viewButton.setEnabled(dateStartFilled && dateEndFilled);
	}

	//not used
	public void changedUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void insertUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void removeUpdate(DocumentEvent e) { disableIfEmpty(e); }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewButton) {
				String s = dateStartField.getText();
				s += dateEndField.getText();
				s += cityField.getText();
				Interface.viewData = Interface.client.requestData(s);
				Interface.switchToCard("view");
		}
		if (e.getSource() == backButton) Interface.switchToCard("welcome");
  }
}
