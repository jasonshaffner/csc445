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
	final JLabel header = new JLabel("<html>\n<center><h1>Queried Data:</h1></center>\n<table cellpadding =\"2\" cellspacing =\"2\">\n<tr>" + 
		"<td width=\"90\"><b>Date</td><td width=\"90\"><b>City</td><td width = \"90\"><b>Visits</td><td width=\"90\"><b>Pages Per Visit</td>" +
		"<td width=\"90\"><b>Avg Visit Length</td><td width=\"90\"><b>% New Visitors</td><td width=\"90\"><b>Bounce Rate</td></tr></table></html>");
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
		StringBuilder dataContents = new StringBuilder();
		dataContents.append("<html>\n<table cellpadding =\"2\" cellspacing =\"2\">\n");
		DataSet data[] = Interface.viewData;
		for (int i = 0; i < data.length; i++) {
			dataContents.append("<tr><td width=\"90\">" + data[i].date + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].city + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].visits + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].pagesPerVisit + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].avgVisitDuration + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].percentNewVisits + "</td>");
			dataContents.append("<td width=\"90\">" + data[i].bounceRate + "</td></tr>");
		}
		if (data.length > 1)
			dataContents.append("</table><br><hr /><table cellpadding=\"2\" cellspacing=\"2\"><tr><td width=\"90\">Total</td>");
			

		dataLabel.setText(dataContents.toString());
	}
}
