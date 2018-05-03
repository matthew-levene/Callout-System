import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MessageViewingUI extends JPanel {

	private DefaultListModel<String> listModel = null;
	private JList<String> messageViewer = null;
	private ViewerCellRenderer cellRenderer;

	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private DatabaseConnector dbconn;

	private Timer timer;

	private boolean canPlaySound = false;

	public MessageViewingUI(){

		dbconn = new DatabaseConnector();

		this.setBackground(Color.BLACK);

		setupViewer();

		//Pull all messages from database and add them to the display at startup
		try{
			addMessageToViewer(dbconn.pullMessages(0));

		}
		catch(SQLException sqlexcpt){
			System.err.println("Unable to pull messages from DB at startup");
		}

		////////////////////////// Timer to check if new message has been added or removed////////////////////////////
		timer = new Timer(750, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Checks for new messages. If there are any, adds them to the list model.
				try{

					ResultSet rs = dbconn.getPushedMessage();

					String pulledMsg = "";
					ArrayList<String> msgArray = new ArrayList<>();

					while(rs.next()){
						pulledMsg = rs.getString("MessageRequest");
						msgArray.add(pulledMsg);
					}

					//Adds the message array to the JList control.
					addMessageToViewer(msgArray);

					//Enables the use of sound
					toggleSoundFeature(true);
				}
				catch(SQLException sqlexcpt){
				}

				//Pull all messages from the database for comparison with the list model.
				try {

					ArrayList<String> dbArray = new ArrayList<>();
					String message = "";
					ResultSet rs = dbconn.pullAllDBMessages();


					while(rs.next()){
						message = rs.getString("MessageRequest");
						dbArray.add(message);
					}

					//Removes any redundant messages from the JList control
					removeMessageFromViewer(dbArray);
				}
				catch(SQLException sqlexcpt){

				}

			}

		});

		timer.start();
	}

	public void setupViewer(){

		//Initialise the cell renderer (tool to word-wrap the JList content)
		cellRenderer = new ViewerCellRenderer(1490);

		//Initialise the content controller for the list
		listModel = new DefaultListModel<>();

		//Initialise the message viewer and set the preferred size
		messageViewer = new JList<>(listModel);
		messageViewer.setPreferredSize(screenSize);
		messageViewer.setBackground(Color.black);

		//Applies the rendering to each item within the list.
		messageViewer.setCellRenderer(cellRenderer);

		//Set the font text and size
		messageViewer.setFont(new Font("Arial", Font.BOLD, 54));


		//Add the message viewer to the JPanel
		add(messageViewer);

	}

	public void addMessageToViewer(ArrayList<String> msgArray) {

		//Accepts an array of strings and parses each string object to determine if it is an IT or Library Issues
		//This is done to determine which sound should play when a new message arrives.

		for(String msg : msgArray){

			if(listModel.contains(msg)){}
			else{

				if(msg.contains("IT:") && canPlaySound()){

					//Plays the sawing-wood sound file
					playSound("src/sound-effects/SawingWood.wav");

				}

				else if(msg.contains("Library:") && canPlaySound()){
					
					//Plays the doorbell sound file
					playSound("src/sound-effects/Doorbell.wav");
				}

				//If the number of items is the list is less than or equal to zero, add the element to the list.
				//If the element is greater than or equal to 1, add the element to the list at position 0. (Places the message on top of the prev. one)
				if(listModel.getSize() <= 0){
					listModel.addElement(msg);
				}
				else{
					listModel.add(0, msg);
				}
			}

		}  //End of for-loop

	}

	public void removeMessageFromViewer(ArrayList<String> dbArray){

		//Checks the message board and the database.
		//If there is a message on the board that isn't in the message table, then the message is removed from the board.

		ArrayList<String> msgArray = new ArrayList<>();
		String tmpMessage = "";

		for(int i = 0; i < listModel.getSize(); i++){
			tmpMessage = listModel.get(i);
			msgArray.add(tmpMessage);
		}

		for(String message : msgArray){
			if(!dbArray.contains(message)){
				listModel.removeElement(message);
			}
		}
	}	

	public void toggleSoundFeature(boolean soundOption){
		
		//Sets the true or false condition for the canPlaySound boolean.
		
		canPlaySound = soundOption;	
	}
	
	public boolean canPlaySound(){
		
		//Returns the condition of the canPlaySound boolean.
		
		return canPlaySound;
	}

	public void playSound(String filedir){

		//Plays soundclip through the speakers. File to play is determined by the file directory path
		
		try {

			AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(filedir));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} 

		catch (UnsupportedAudioFileException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); } 
		catch (LineUnavailableException e) { e.printStackTrace(); }	

	}

	public static void main(String[] args) throws InterruptedException {

		JFrame frame = new JFrame();
		MessageViewingUI msgViewer = new MessageViewingUI();

		frame.setTitle("Message Viewer");

		//Forces the program to take full screen.
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//Removes the outside frame
		//frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(msgViewer);

	}


}
