import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class RemoveCard extends MessageCard implements ActionListener, FocusListener, DocumentListener {

	JLabel header = new JLabel("<html><center><h1>Enter Date to Remove<br></h1></center></html>");
	JButton removeButton = new JButton("Remove");
	JButton backButton = new JButton("Back");
	JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JTextField dateField = new JTextField("Date (format YYYYMMDD");
	boolean datePrev = false;

	//layout not set in stone... i'll play with it if i have time
  public void initializeThisCard() {
    System.out.println("Initializing pane...");
		topPanel.add(header);
		add(topPanel,BorderLayout.NORTH);
		centerPanel.add(dateField);
		add(centerPanel(BorderLayout.CENTER);
		buttonPanel.add(removeButton);
		add(buttonPanel,BorderLayout.SOUTH);
		enterButton.setEnabled(false);
		addActionListeners();
  }
  
  public RemoveCard() {
    super();
		setLayout(new BorderLayout());
  }
  
	public void addActionListeners() {
		removeButton.addActionListener(this);
		date.addFocusListener(this);
	}

	  //clears preset text from username and password fields when cursor enters said field
	public void focusGained(FocusEvent e) {
		if (date.isFocusOwner())
			if (!datePrev) {
				date.setText("");
				datePrev = true;
			} else dateField.selectAll();
	}

	//not used
	public void focusLost(FocusEvent e) {}

	//controls state of enter button, enabling only when both username and password are filled out
	public void disableIfEmpty(DocumentEvent e) {
		if (e.getDocument() == date.getDocument()) dateFilled = e.getDocument().getLength() > 0;
		enterButton.setEnabled(dateFilled);
	}

	//not used
	public void changedUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void insertUpdate(DocumentEvent e) { disableIfEmpty(e); }
	public void removeUpdate(DocumentEvent e) { disableIfEmpty(e); }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == removeButton)
				if (Client.removeRecord(dateField.getText())) {
					header.setText("<html><center><h1>Record Removed</h1></center></html>
					date.setText("Date (format YYYYMMDD)");
					datePrev = false;
					enterButton.setEnabled(false);
				} else header.setText("<html><center><h1>Unable to Remove Record</h1><br>Check your typing</center></html>
		if (e.getSource() == backButton) Interface.switchToCard("welcome");
  }
}
