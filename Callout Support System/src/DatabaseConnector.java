//Step 1 import the JBC API LIbrary
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseConnector {

	private Connection conn;
	private Statement stmt;
	private ResultSet rs;

	ArrayList<String> messageArray = new ArrayList<>();

	private int messageCounter = 0, timesEntered = 0;
	
	private int opsIndex = 0;

	public DatabaseConnector(){

		try{
			Class.forName("com.mysql.jdbc.Driver"); //Step 2: Load the driver class. 
			conn = DriverManager.getConnection("jdbc:mysql://databaseAddress","username","password"); //Establish the connection object			
		}
		catch(Exception ex){
			System.out.println("There is an issue with the connection object");
		}

	}

	public ArrayList<String> getOperatives() throws SQLException{
		
		//Return only the 'accounts' that are allowed to be visible in the database.
		String visibility = "True";
		String sql = "SELECT FirstName, LastName, isVisible FROM operative WHERE isVisible = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, visibility);
		rs = stmt.executeQuery();

		ArrayList<String> array = new ArrayList<>();

		while(rs.next()){

			String firstName = rs.getString("FirstName");
			String lastName = rs.getString("LastName");
			String name = new String(firstName + " " + lastName);

			array.add(name);
		} 

		return array;

	}

	public void setOperativeDutyID(int OperativeID, int choice) throws SQLException{

		//Set the operative which is now on duty (Specified by the program cbo boxes)
		stmt = conn.createStatement();

		switch(choice){

		case 0:
			stmt.executeUpdate("UPDATE operative SET OperativeDutyID = 0");
			break;

		case 1:
			stmt.executeUpdate("UPDATE operative SET OperativeDutyID = 1 WHERE OperativeID =" + OperativeID);
			break;
		}
	}

	public ResultSet getOperativeDutyID() throws SQLException{
		
		//Get the operative that is on duty from the database
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT OperativeID, StatusID FROM operative WHERE OperativeDutyID = 1");

		return rs; 
	}


	public ArrayList<String> getStatuses() throws SQLException{

		//Get the status availabities from the database
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT StatusCondition FROM state");

		ArrayList<String> array = new ArrayList<>();

		while(rs.next()){

			String state = rs.getString("StatusCondition");
			array.add(state);
		} 

		return array;
	}

	public void pushMessage(String message) throws SQLException{

		/*Push the message from the program to the database.
		 * String is piped into a prepared statement to reduce the chance of direct SQL injections to the db
		*/
		Date date = new Date();
		Long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		String sql = "INSERT INTO message(MessageRequest, MessageReadFlag, TimeEntered)" + "VALUES (?, 1, '"+ts+"')";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, message);

		stmt.executeUpdate();
	}

	public void setOperativeStatus(int operativeID, int choice) throws SQLException{

		//Set an operative's status condition on the database (e.g. Available/Busy)
		stmt = conn.createStatement();

		switch(choice){
		case 1: ///Set DB StatusID value of selected operative to available (1)
			stmt.executeUpdate("UPDATE operative SET StatusID = " + choice + " WHERE OperativeID = " + operativeID + "");
			break;

		case 2: ///Set DB StatusID value of selected operative to busy (2)
			stmt.executeUpdate("UPDATE operative SET StatusID = " + choice + " WHERE OperativeID = " + operativeID + "");
			break;
		}

	}

	public ResultSet getOperativeStatus() throws SQLException{

		//Get the operative's status condition on the database (e.g. Available/Busy)
		stmt = conn.createStatement();
		rs = stmt.executeQuery("Select OperativeID, StatusID from operative where StatusID = 2");

		return rs;

	}

	public void clearMessageArray(){
		//Clear message array list
		messageArray.clear();
	}

	public ResultSet getPushedMessage() throws SQLException{
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT MessageID, MessageRequest FROM message WHERE MessageReadFlag = 1");

		return rs;
	}

	public void setMessageReadFlag(int messageID) throws SQLException{

		stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE message SET MessageReadFlag = 2 WHERE MessageID = " + messageID);

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


			while(rs.next()){
				String pulledMessage = rs.getString("MessageRequest");
				messageArray.add(pulledMessage);
			}

			messageCounter++;
		}

		return messageArray;
	}

	public ResultSet pullAllDBMessages() throws SQLException{
		
		//Retrieves all messages from the message table
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT MessageRequest FROM message");

		return rs;

	}

	public void removeRecord(String selectedMessage, int operativeIndex) throws SQLException {

		//Duplicates the record into the archivedMessages table and removes the original copy from the messages table.
		int msgID = 0;
		String msgRequest = "";
		int msgReadFlag = 0;
		Timestamp msgTimeEntered = null;
		
		opsIndex = operativeIndex;

		String sql = "Select MessageID, MessageRequest, MessageReadFlag, TimeEntered FROM message WHERE MessageRequest = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, selectedMessage);

		rs = stmt.executeQuery();

		while(rs.next()){
			msgID = rs.getInt("MessageID");
			msgRequest = rs.getString("MessageRequest");
			msgReadFlag = rs.getInt("MessageReadFlag");
			msgTimeEntered = rs.getTimestamp("timeEntered");
		}

		//Checks if the message is marked for library, if so sets the index to the library account.
		if(selectedMessage.contains("Library:")){
			opsIndex = 5;
		}

		//Copy record from main message table into archivedMessages table
		stmt.executeUpdate("INSERT INTO archivedMessages(MessageID, MessageRequest, MessageReadFlag, TimeEntered, acceptedBy )" + "VALUES ('"+msgID+"', '"+msgRequest+"', '"+msgReadFlag+"','"+msgTimeEntered+"', '"+opsIndex+"')");

		//Deletes record from main message table
		stmt.executeUpdate("DELETE FROM message WHERE MessageID = " + msgID);

	}
	
	public ResultSet getMessagesByDate(String date) throws SQLException{
		
		//Return messages by a given date. Order the result by ascending (Earliest at the top)
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT MessageRequest, TimeEntered, acceptedBy FROM archivedMessages WHERE TimeEntered LIKE '%"+date+"%' ORDER BY TimeEntered ASC");
		
		return rs;

	}
	
	public ResultSet getOperativeName(int index) throws SQLException{

		//Gets the operative name by db index number.
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT FirstName, LastName FROM operative WHERE OperativeID = '"+index+"'");
		
		return rs;
	}



}
