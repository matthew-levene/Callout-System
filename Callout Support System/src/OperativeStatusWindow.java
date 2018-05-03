import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea; 

@SuppressWarnings("serial")
public class OperativeStatusWindow extends JPanel {
	
	private GridBagLayout gbl = new GridBagLayout();
	private GridBagConstraints cons = new GridBagConstraints();
	private JTextArea textArea;
	
	public OperativeStatusWindow(){
		
		setLayout(gbl);
		setBorder(BorderFactory.createTitledBorder("Operative Status"));
		
		textArea = new JTextArea(5,27);
		textArea.setEditable(false);
		
		add(textArea, cons);
		
	}
	
	public void setAvailable(String operative){
		
		//Set the text area to show the 'Available' screen
		textArea.setBackground(new Color(0,204,0));
		textArea.append("\n");
		textArea.append("           The IT Support Assistant on call today is: \n      " + 
		                         "                               "  + operative + "         ");
	}
	public void setBusy(String operative){
		
		//Set the text area to show the 'Busy' screen
		textArea.setBackground(Color.ORANGE);
		textArea.append("\n");
		textArea.append("                The on call IT Support Assistant is \n             "
				+                        " currently helping another customer. \n      " + 
		                                "                               "  + operative + "         ");
	}
	
	public void resetDisplay(){
		
		//Clears the text area display
		textArea.setText("");
		textArea.setBackground(Color.WHITE);
	}

}
