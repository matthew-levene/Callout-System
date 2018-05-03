import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ComboControlPanel extends JPanel implements ActionListener {

	private ComboEventListener evListener;
	
	private JLabel operatorLbl, statusLbl;
	
	private JComboBox<String> selectOperator, selectStatus;
	
	private String chosenOperator = "";
	private int chosenState, chosenOperatorID = 0;
	
	private GridBagLayout gbl = new GridBagLayout();
	private GridBagConstraints cons = new GridBagConstraints();
	
	public ComboControlPanel() {
		
		setLayout(gbl);

		operatorLbl = new JLabel("Choose Operative:          ");
		statusLbl = new JLabel("Choose Status:          ");
		
		selectOperator = new JComboBox<String>();
		selectOperator.addActionListener(this);
		
		selectStatus = new JComboBox<String>();
		selectStatus.addActionListener(this);
		
		/////////// Global settings for gridbag cons /////
		
		cons.weightx = 0.1;
		cons.weighty = 0.1;
		cons.insets = new Insets(10,0,0,0);
		
		cons.anchor = GridBagConstraints.FIRST_LINE_END;
	
		/////////////// Add operator label //////////////
		
		cons.gridx = 0;
		cons.gridy = 0;
		
		add(operatorLbl, cons);
		
		/////////////// Add operator combo-box //////////
		
		cons.gridx = 1;
		cons.gridy = 0;
	
		add(selectOperator, cons);
		
		/////////////// Add status label ///////////////
		
		cons.gridx = 0;
		cons.gridy = 1;
		
		add(statusLbl, cons);
		
        ///////////// Add status combo-box ////////////
		
		cons.gridx = 1;
		cons.gridy = 1;
		
		add(selectStatus, cons);
		
		
	}
	
	public void addCBOEventListener(ComboEventListener listener){
		this.evListener = listener;
	}

	public void addOperatives(ArrayList<String> operatives){
		
		selectOperator.addItem("");
		
		for (String operative : operatives){
			selectOperator.addItem(operative);
		}

	}
	
	public void modifyOperativeControl(int choice){
		
		switch(choice){
		case 1:
			selectOperator.setEnabled(true);
			break;
		case 2:
			selectOperator.setEnabled(false);
			break;
			
		}
		
	}
	
	public void setSelectedControls(int operativeID, int stateID){
		
		selectOperator.setSelectedIndex(operativeID);
		selectStatus.setSelectedIndex(stateID);
		
		switch(stateID){
		case 1:
			modifyOperativeControl(1);
			break;
		case 2:
			modifyOperativeControl(2);
			break;
		}
		
	
		
	}
	
	public int getSelectedOperativeIndex(){
		
		int operative = selectOperator.getSelectedIndex();
		
		return operative;
	}
	
	public void addStatuses(ArrayList<String> states){

		selectStatus.addItem("");
		
		for (String state : states){
			selectStatus.addItem(state);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		if(evListener != null){
			
			if (event.getSource() == selectOperator){
			
				chosenOperator = (String) selectOperator.getSelectedItem();
				chosenOperatorID = selectOperator.getSelectedIndex();
	
			}
			
			if (event.getSource() == selectStatus){
				
				chosenState = selectStatus.getSelectedIndex();
			}
			
			evListener.cboEventOccurred(chosenOperator, chosenState, chosenOperatorID);
			
			
		}
	}

}
