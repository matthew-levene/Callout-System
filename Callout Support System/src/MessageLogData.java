public class MessageLogData {
	
	private String dateRecorded = "";
	private String timeRecorded = "";
	private String messageRecorded = "";
	private String operatorFullName = "";
	
	public MessageLogData(String dateEntered, String timeEntered, String msgStored, String opName){
		
		this.dateRecorded = dateEntered;
		this.timeRecorded = timeEntered;
		this.messageRecorded = msgStored;
		this.operatorFullName = opName;
		
	}
	
	public String getOperatorRecorded(){
		return operatorFullName;
	}
	public String getDateRecorded() {
		return dateRecorded;
	}

	public String getTimeRecorded() {
		return timeRecorded;
	}

	public String getMessageRecorded() {
		return messageRecorded;
	}




}
