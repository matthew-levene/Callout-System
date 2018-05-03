
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class CalloutSystemUI extends JPanel{

	private DatabaseConnector dbconn;
	private MessageSubmissionPanel rsPanel;
	private MessageBoardPanel drPanel;
	private OperativeStatusWindow osPanel;
	private ComboControlPanel mosPanel;

	private Timer cboTimer;
	private ArrayList<String> opNames, states, messages;

	private int tmrCounter = 0;

	public CalloutSystemUI(JFrame frame){

		//Instantiate all class objects needed for the program
		instantiateControls();

		//Run and assign database to program startup tasks
		preloadControls();

		//Instantiate message retrieval timer at 500ms intervals
		Timer timer = new Timer(750, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Checks to see if any new messages have been added to the database
				checkForNewMessages();
				//Removes any messages that are in the message board, but no longer in the 'message' table
				removeArchivedMessages();
			}

		});

		timer.start();

		//Instantiate the combo-box (drop-down list) timer
		cboTimer = new Timer(5000, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Check which operator has the duty ID and populates the program with information 
				updateOperativeOnDuty();
			}

		}){}; //End of combo-box timer

		cboTimer.start();

		////////////////////// Panel which contains the message submission controls /////////////////////////////////////////////
		add(rsPanel);
		rsPanel.addButtonListener(new ButtonEventListener(){

			@Override
			public void btnEventOccured(String message, int choice) {

				//Submits the message to the database
				addMessageToDatabase(message, choice);

			}		
		});

		//////////////////////// Panel which contains the message requests board and message log window /////////////////////////
		add(drPanel);

		drPanel.setMouseEventListener(new MouseEventListener(){

			@Override
			public void mouseHasBeenClicked(MouseEvent mev) {

				//Checks to see which mouse button has been clicked
				//If left -- Attempts to remove message from message board
				//If right -- Shows the reporting data table
				checkMouseEvent(mev, frame);

			} 
		});

		//Loads the message request board with an array list of strings
		drPanel.addMessageItem(messages);

		/////////////////////////// Panel which contains the operative status window //////////////////////////////////////////////
		add(osPanel);

		/////////////////////////// panel which contains the combobox controls ///////////////////////////////////////////////////
		add(mosPanel);
		mosPanel.addOperatives(opNames);
		mosPanel.addStatuses(states);


		//Pull last known operator and state from database at startup and apply to controls
		lastKnownOperativeState();

		mosPanel.addCBOEventListener(new ComboEventListener(){

			@Override
			public void cboEventOccurred(String operative, int state, int operativeID) {
				//Sets new data in combo box controls and change operative statuses in the database
				setComboBoxData(operative, state, operativeID);


			}
		});

	}

	public void instantiateControls(){

		dbconn = new DatabaseConnector();
		rsPanel = new MessageSubmissionPanel();
		drPanel = new MessageBoardPanel();
		osPanel = new OperativeStatusWindow();
		mosPanel = new ComboControlPanel();
	}

	public void preloadControls(){
		try {
			opNames = dbconn.getOperatives();   //Pull operative names from database
			states = dbconn.getStatuses();     //Pull state types (Available, Busy) from database
			messages = dbconn.pullMessages(0); //Pull all messages stored in database at startup

		} catch (SQLException e) {

			System.err.println("Error at JDBC Connector initialisation variables");
		}
	}

	public void checkForNewMessages(){

		ArrayList<String> arr = new ArrayList<>();
		String message = "";
		int messageID = 0;

		//Checks the database for any new messages -- If there are any, adds them to the message board.
		try {

			ResultSet res = dbconn.getPushedMessage();

			while(res.next()){

				messageID = res.getInt("MessageID");
				message = res.getString("MessageRequest");

				arr.add(message); 
			}

			drPanel.addMessageItem(arr);

			tmrCounter++;

			//if 4 seconds have passed (500ms * 8), set the message flag to 2.
			if(tmrCounter == 8){
				tmrCounter = 0;
				dbconn.setMessageReadFlag(messageID);
			}

		} catch (SQLException e) {
			System.err.println("Error occurred at message retrieval process");

		}

	}

	public void removeArchivedMessages(){
		//Checks if the messages in the message board are still active within the 'message' table
		//If not e.g. moved to archived, then delete them from the message board.

		try {

			ResultSet rs = dbconn.pullAllDBMessages();
			String msgRequest = "";
			ArrayList<String> dbArray = new ArrayList<>();
			ArrayList<String> msgArray = new ArrayList<>();

			while(rs.next()){
				msgRequest = rs.getString("MessageRequest");
				dbArray.add(msgRequest);
			}

			DefaultListModel<String> dlm = drPanel.getMessageRequests();

			for(int i = 0; i < dlm.getSize(); i++){  //Copies the DLM into the an ArrayList
				String tmpMessage = dlm.get(i);
				msgArray.add(tmpMessage);
			}

			if(dlm.isEmpty()){

			}
			else{

			}

			for(String request : msgArray){              

				if(!dbArray.contains(request)){
					if(!msgArray.contains(request)){

					}
					else{
						drPanel.removeMessageItem(request);
					}
				}
			}
		}
		catch(SQLException sqlexcpt){

		}
	}

	public void updateOperativeOnDuty(){

		try {
			ResultSet rs = dbconn.getOperativeDutyID();
			int operativeID = 0;
			int statusID = 0;

			while(rs.next()){

				operativeID = rs.getInt("OperativeID");
				statusID = rs.getInt("StatusID");

			}

			mosPanel.setSelectedControls(operativeID, statusID);

		} catch (SQLException e) {
			System.err.println("Error occurred at duty ID retrieval process");
		}	
	}

	public void addMessageToDatabase(String message, int choice){

		String receivedMessage = "";

		switch(choice){

		case 0:
			receivedMessage = message;

			if(receivedMessage.isEmpty() || receivedMessage.contains("');") || receivedMessage.contains("; --")){}
			else{
				//Attempt to push the message onto the database
				try {dbconn.pushMessage("IT: " + receivedMessage);} 
				catch (SQLException e) {	e.printStackTrace();}

				rsPanel.resetTextField(0);
			}
			break;
		case 1:
			receivedMessage = message;
			if(receivedMessage.isEmpty() || receivedMessage.contains("');") || receivedMessage.contains("; --")){}
			else{
				//Attempt to push the message onto the database
				try { dbconn.pushMessage("Library: " + receivedMessage);} 
				catch (SQLException e) {e.printStackTrace(); }

				rsPanel.resetTextField(1);
			}
			break;
		}

	}

	@SuppressWarnings("unchecked")
	public void checkMouseEvent(MouseEvent mev, JFrame frame){

		//Gets the messageboard component
		JList<String> messageReqs = drPanel.getMessageRequestComponent();

		//Casts the input source into the message board component
		messageReqs = (JList<String>) mev.getSource();

		//If button equals left mouse-click
		if(mev.getButton() == MouseEvent.BUTTON1){

			//Get value from the message board component
			String selectedMessage = messageReqs.getSelectedValue();

			//Throw a confirmation dialog to offer user a choice
			int index = JOptionPane.showConfirmDialog(CalloutSystemUI.this, "Do you really want to remove: \n" + selectedMessage);

			//if yes button was clicked, then begin message removal
			if(index == JOptionPane.YES_OPTION){
				try {
					//Removes the message record from the 'message' table and adds it to 'archivedMessages'
					//Adds the operative on duty to the database 'acceptedBy' column
					int operativeIndex = mosPanel.getSelectedOperativeIndex();

					dbconn.removeRecord(selectedMessage, operativeIndex);

				} catch (SQLException e) {
					e.getStackTrace();
					System.out.println("Issue removing message from DB");
				}

			}
		}
		//If button equals right mouse-click
		else if(mev.getButton() == MouseEvent.BUTTON3){

			//Open message log dialog window
			MessageLogWindow log = new MessageLogWindow(frame);

			//While the dialog window hasn't been closed
			while(!log.getWindowClosedFlag()){
				//Retrieves the messages from the database based on the given date
				try {
					//Clears the stored table data ready for new data
					log.clearTableModel();

					//instantiate local variables
					ResultSet result = null;
					Timestamp timeEntered = null;
					MessageLogData reportData = null;

					String message = "";
					String dateEntered = "";
					String timeRecorded = "";

					int opIndex = 0;
					String opFirstName = "";
					String opLastName = "";
					String opFullName = "";

					//Get date selected by user and return result
					result = dbconn.getMessagesByDate(log.getDate());

					//While the result object has another record
					while(result.next()){
						//Get message from record
						message = result.getString("MessageRequest");

						//Get timestamp from record and extract into date and time variables 
						timeEntered = result.getTimestamp("TimeEntered");
						dateEntered = new SimpleDateFormat("dd/MM/yyyy").format(timeEntered);
						timeRecorded = new SimpleDateFormat("HH:mm").format(timeEntered);

						//Get operator name that accepted the job
						opIndex = result.getInt("acceptedBy");
						ResultSet operatorResult = dbconn.getOperativeName(opIndex);
						while(operatorResult.next()){
							opFirstName = operatorResult.getString("FirstName");
							opLastName = operatorResult.getString("LastName");
						}

						//Put the first name and last name of the operator together
						opFullName = opFirstName + " " + opLastName;

						//Place message, date, time and operator's name in an object with associated getters/
						reportData = new MessageLogData(dateEntered, timeRecorded, message, opFullName); 

						//Adds the newly created object to the 'addNewRow' method for table processing
						log.addNewRow(reportData);
					}
					//Once all of the data has been added to the table; the dialog is brought back.
					log.setVisible(true);

				} catch (SQLException e) { e.printStackTrace();}

			} //End of while loop
		} //End of else-if block
	}

	public void lastKnownOperativeState(){

		ResultSet rs;
		try {

			rs = dbconn.getOperativeDutyID();
			int operativeID = 0;
			int statusID = 0;

			while(rs.next()){

				operativeID = rs.getInt("OperativeID");
				statusID = rs.getInt("StatusID");
			}

			mosPanel.setSelectedControls(operativeID, statusID);

		} catch (SQLException e) {}
	}

	public void setComboBoxData(String operative, int state, int operativeID){
		osPanel.resetDisplay(); //Clears the display ready for the next update

		switch(state){
		case 1: 
			try { 
				dbconn.setOperativeStatus(operativeID, state);

				dbconn.setOperativeDutyID(operativeID, 0);
				dbconn.setOperativeDutyID(operativeID, 1);

			} 
			catch (SQLException e1) { e1.printStackTrace();}

			osPanel.setAvailable(operative);

			break;

		case 2:
			try { 

				dbconn.setOperativeStatus(operativeID, state);


				dbconn.setOperativeDutyID(operativeID, 0);
				dbconn.setOperativeDutyID(operativeID, 1);

			} 
			catch (SQLException e) { e.printStackTrace();}

			osPanel.setBusy(operative);

			break;		
		}
	}

	public static void main(String[] args) throws InterruptedException {

		JFrame frame = new JFrame();
		CalloutSystemUI calloutUI = new CalloutSystemUI(frame);

		frame.setTitle("Callout Support System");
		frame.setSize(350,450);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(calloutUI);		

	}



}
