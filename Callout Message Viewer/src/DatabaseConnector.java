//Step 1 import the JBC API LIbrary
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseConnector {

	private Connection conn;
	private Statement stmt;
	private ResultSet rs;

	ArrayList<String> messageArray = new ArrayList<>();

	private int messageCounter = 0, timesEntered = 0;

	public DatabaseConnector(){

		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://databaseAddress","username","password");
		}
		catch(Exception ex){
			System.out.println("There is an issue with the connection object");
		}
	}

	public void clearMessageArray(){
		
		//Clears the message array
		messageArray.clear();
	}

	public ResultSet getPushedMessage() throws SQLException{
		
		//Get messages from the database
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT MessageID, MessageRequest FROM message WHERE MessageReadFlag = 1");

		return rs;
	}


	public ArrayList<String> pullMessages(int choice) throws SQLException{

		switch(choice){
		case 0:
			//Pull all messages stored in database at start of program
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT MessageRequest FROM message");

			while(rs.next()){

				String pulledMessage = rs.getString("MessageRequest");
				messageArray.add(pulledMessage);

				messageCounter++;
			}

			break;

		case 1:
			//Pull messages through from database as the program is running -- (Excludes messages pulled at startup) 
			//Returns a single string each time new message is sent to the db and return the latest result based on number of messaged
			//pulled through

			int msgCounter = messageCounter + 1;  //counts number of messages pulled from database + 1 (1 is the new message not pulled)

			if(timesEntered == 0){       //Clears all of the previous items out of the ArrayList and adds session variables to list.
				messageArray.clear();
				timesEntered++;
			}

			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT MessageRequest FROM message WHERE MessageID = "+ msgCounter +""); //Pull new message from db


			//Pulls the messages out of the result set and adds them to an array.
			while(rs.next()){
				String pulledMessage = rs.getString("MessageRequest");
				messageArray.add(pulledMessage);
			}

			messageCounter++;
		}

		return messageArray;
	}
	
	public ResultSet pullAllDBMessages() throws SQLException{
		
		//Pull all messages in the database
		//Used to check if messages aren't in db, but are on the message board
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT MessageRequest FROM message");
		
		return rs;
		
	}


}
