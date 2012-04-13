package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import client.*;
import database.*;

public class RemoveCard extends MessageCard implements ActionListener, FocusListener, DocumentListener {

	JLabel header = new JLabel("<html><center><h1>Enter Date to Remove<br></h1></center></html>");
	final JButton removeButton = new JButton("Remove");
	final JButton backButton = new JButton("Back");
	final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JTextField dateField = new JTextField("Date (format YYYYMMDD",15);
	boolean datePrev = false;
	boolean dateFilled = false;

	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		centerPanel.add(dateField);
		add(centerPanel,BorderLayout.CENTER);
		buttonPanel.add(removeButton);
		buttonPanel.add(backButton);
		add(buttonPanel,BorderLayout.SOUTH);
		removeButton.setEnabled(false);
		addActionListeners();
  }
  
  public RemoveCard() {
    super();
		setLayout(new BorderLayout());
  }
  
	public void addActionListeners() {
		removeButton.addActionListener(this);
		backButton.addActionListener(this);
		dateField.addFocusListener(this);
		dateField.getDocument().addDocumentListener(this);
	}

	  //clears preset text from username and password fields when cursor enters said field
	public void focusGained(FocusEvent e) {
		if (dateField.isFocusOwner())
			if (!datePrev) {
				dateField.setText("");
				datePrev = true;
			} else dateField.selectAll();
	}

	//not used
	public void focusLost(FocusEvent e) {}

	//controls state of enter button, enabling only when both dateField is filled out
	public void disableIfEmpty(DocumentEvent e) {
		if (e.getDocument() == dateField.getDocument()) dateFilled = e.getDocument().getLength() > 0;
		removeButton.setEnabled(dateFilled);
	}

	//not used
	public void changedUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void insertUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void removeUpdate(DocumentEvent e) { disableIfEmpty(e); }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == removeButton)
				if (Interface.client.removeRecord(dateField.getText())) {
					header.setText("<html><center><h1>Record Removed</h1></center></html>");
					dateField.setText("Date (format YYYYMMDD)");
					datePrev = false;
					removeButton.setEnabled(false);
				} else header.setText("<html><center><h1>Unable to Remove Record</h1><br>Check your typing</center></html>");
		if (e.getSource() == backButton) Interface.switchToCard("welcome");
  }
}
