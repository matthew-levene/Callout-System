import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MessageSubmissionPanel extends JPanel implements ActionListener {

	private GridBagLayout gbl = new GridBagLayout();
	private GridBagConstraints cons = new GridBagConstraints();
	
	private JLabel technicalLbl, libraryLbl;
	private JTextField technicalTF, libraryTF;
	private JButton technicalSbmtBtn, librarySbmtBtn;
	
	private ButtonEventListener evListener;
	
	MessageSubmissionPanel(){
		//Instantiate Labels for request submission controls
		technicalLbl = new JLabel("IT: "); 
		libraryLbl = new JLabel("Library: ");  
						
		//Instantiate Text Fields for message requests
		technicalTF = new JTextField(10);
		technicalTF.addActionListener(this);
		
		libraryTF = new JTextField(10);
						
		//Instantiate Buttons for message submission
		technicalSbmtBtn = new JButton("Submit");
		technicalSbmtBtn.addActionListener(this);
		
		librarySbmtBtn = new JButton("Submit");
		librarySbmtBtn.addActionListener(this);
		
		setLayout(gbl);
		setBorder(BorderFactory.createTitledBorder("Submit Request"));
		
		////////////// Row 1 - Request IT Assistance Row //////////////////
		
		cons.weightx = 0.1;
		cons.weighty = 0.1;
		cons.anchor = GridBagConstraints.FIRST_LINE_START;
		cons.insets = new Insets(5,20,12,0);
		
		cons.gridx = 0;
		cons.gridy = 0;
		
		add(technicalLbl, cons);
		
		cons.gridx = 1;
		cons.gridy = 0;
		
		add(technicalTF, cons);
		
		cons.gridx = 2;
		cons.gridy = 0;
		
		add(technicalSbmtBtn, cons);
		
		//////////// Row 2 - Request Library Assistance Row /////////////////////
		cons.gridx = 0;
		cons.gridy = 1;
		
		add(libraryLbl, cons);
		
		cons.gridx = 1;
		cons.gridy = 1;
		
		add(libraryTF, cons);
		
		cons.gridx = 2;
		cons.gridy = 1;
		
		add(librarySbmtBtn, cons);
		
	}
	
	public void addButtonListener(ButtonEventListener listener){
		
		this.evListener = listener;
	}
	
	public String retrieveText(int selection){
		
		//Gets the text from the chosen component
		String message = "";
			
			switch(selection){
			
			case 0:
				message = technicalTF.getText();
				break;
			case 1:
				message = libraryTF.getText();
				break;
			}

		return message;
	}
	
	public void resetTextField(int selection){
		
		//Clears the information stored within the selected text field components
		switch(selection){
		
		case 0:
			technicalTF.setText("");
			break;
		case 1:
			libraryTF.setText("");
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent btnEvent) {
		
		//Fires off an event based on which button was clicked
		if(evListener != null){
			
			if(btnEvent.getSource() == technicalSbmtBtn){
							
				evListener.btnEventOccured(retrieveText(0), 0);
			}
			else if (btnEvent.getSource() == librarySbmtBtn){
				evListener.btnEventOccured(retrieveText(1), 1);
			}
			
		}
	}
}
