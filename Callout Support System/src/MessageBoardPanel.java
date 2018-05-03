import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class MessageBoardPanel extends JPanel {

	private GridBagLayout gbl = new GridBagLayout();
	private GridBagConstraints cons = new GridBagConstraints();

	private JList<String> messageRequests;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JScrollPane scrollPane;

	private int numOfMessagesAdded = 0;

	private MouseEventListener mouseListener;

	public MessageBoardPanel(){
		
		setLayout(gbl);

		messageRequests = new JList<>(listModel);
		
		//Fires off when a mouse event has been detected
		messageRequests.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev){
				if(mouseListener != null){
					mouseListener.mouseHasBeenClicked(ev);
				}
			}
		});
		
		////////////// Declare scroll pane for message requests board ///////////////////////
		
		scrollPane = new JScrollPane(messageRequests); 
		scrollPane.setPreferredSize(new Dimension(300,100));

		add(scrollPane, cons);
	}

	public void setMouseEventListener(MouseEventListener mouseEventListener){
		//Assigns listener object to this class' listener object. 
		this.mouseListener = mouseEventListener;
	}

	public void addMessageItem(ArrayList<String> messages){			

		//Adds messages contained within a string array to the message board
		for(String msg : messages){

			if(listModel.contains(msg)){}
			else{
				
				if(numOfMessagesAdded >= 1){
					listModel.add(0, msg);
				}
				else{
					listModel.addElement(msg);
					numOfMessagesAdded++;
				}
			}

		}

	}

	public DefaultListModel<String> getMessageRequests(){
		//Returns the listModel to the calling object.
		return listModel;
	}

	public void removeMessageItem(String msg){
		//Removes the chosen string from the message list.
		listModel.removeElement(msg);
	}

	public JList<String> getMessageRequestComponent() {
		//Returns the message request board to the calling object.
		return messageRequests;
	}




}
