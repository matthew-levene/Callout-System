import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

@SuppressWarnings("serial")
public class MessageLogWindow extends JDialog {

	private JPanel contentPanel;
	private GridBagConstraints cons = new GridBagConstraints();

	private JTable table;
	private DefaultTableModel dataModel;
	private JScrollPane scrollPane;

	private String dbMonth = "";
	private String dbDay = "";

	private String selectedDate = "";

	private boolean windowClosedFlag = false;

	public MessageLogWindow(JFrame parent) {
		super(parent, "Message Log Window", true);

		// Define the JPanel which will hold the JDialog's components
		contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());

		// Instantiate the JTable's data model
		Object[] colNames = { "Date", "Time", "Message", "Accepted By" };
		dataModel = new DefaultTableModel(colNames, 0);

		// Instantiate the JTable and disable editing
		table = new JTable(dataModel);
		table.setEnabled(false);
		setTableColumnWidth();

		// Add table to scroll-pane -> Is a must for displaying column headers.
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 180));

		/////////////////////// Add Calendar object to the JPanel /////////////////////// //////////////////////////
		// Source: https://stackoverflow.com/questions/26794698/how-do-i-implement-jdatepicker

		Properties prop = new Properties();
		prop.put("text.today", "Today");
		prop.put("text.month", "Month");
		prop.put("text.year", "Year");

		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model, prop);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getOldValue() == pce.getNewValue()) {

				} else {
					dateFormat(model);
					String dbDate = "" + model.getYear() + "-" + dbMonth + "-" + dbDay;
					selectedDate = dbDate;

					// closes off the dialog, so that the program can retrieve the message low
					setVisible(false);
				}
			}

		});

		cons.gridx = 0;
		cons.gridy = 0;
		cons.insets = new Insets(25, 0, 0, 0);

		// Add calendar to the JPanel
		contentPanel.add(datePicker, cons);

		////////////////// Set the condition variables for the JTable ////////////////////////////////////
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = 0.1;
		cons.weighty = 0.1;
		;

		cons.gridx = 0;
		cons.gridy = 1;

		cons.insets = new Insets(0, 0, 10, 0);

		// Add the ScrollPane containing JTable to the JPanel
		contentPanel.add(scrollPane, cons);

		// Add the JPanel to the JDialog
		add(contentPanel);

		// Set the JDialog setup variables
		setSize(450, 300);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Add a listener to the window-closed operation
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent arg0) {
				setWindowClosedFlag(true);
				setVisible(false);
			}
		});

	}

	public void dateFormat(UtilDateModel model) {
		//Appends a '0' to the front of the date if value is less than 10.
		//e.g. 3-4-2018 becomes 03-04-2018
		if (model.getMonth() < 10) {
			dbMonth = "0" + (model.getMonth() + 1);
		} else {
			dbMonth = Integer.toString(model.getMonth());
		}

		if (model.getDay() < 10) {
			dbDay = "0" + model.getDay();
		} else {
			dbDay = Integer.toString(model.getDay());
		}
	}

	public void addNewRow(MessageLogData reportData) {

		// Get the data from the object that has been passed in and put in an Object[]
		Object[] row = { reportData.getDateRecorded(), reportData.getTimeRecorded(), reportData.getMessageRecorded(),
				reportData.getOperatorRecorded() };

		// Pass the Object[] reference into the addRow method of the dataModel.
		dataModel.addRow(row);

	}

	public void setTableColumnWidth() {
		//Sets the preferred width of each table column
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);

		// Adds the word wrapping to the JTable 'message' cells.
		table.getColumnModel().getColumn(2).setCellRenderer(new TableDataCellRenderer());

		table.getColumnModel().getColumn(3).setPreferredWidth(100);
	}

	public void clearTableModel() {
		//Set number of rows to 0 (Which clears the table)
		dataModel.setRowCount(0);
	}

	public String getDate() {
		//Returns the selected date to the calling object.
		return selectedDate;
	}

	public void setWindowClosedFlag(boolean flag) {
		//Toggles the windowClosedFlag boolean
		windowClosedFlag = flag;
	}

	public boolean getWindowClosedFlag() {
		//Return the status of the windowClosedFlag variable (e.g. true/false)
		return windowClosedFlag;
	}
}
