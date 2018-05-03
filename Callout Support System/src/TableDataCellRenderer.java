import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

//Sourced from: https://stackoverflow.com/questions/37768335/how-to-word-wrap-inside-a-jtable-row

@SuppressWarnings("serial")
public class TableDataCellRenderer extends JTextArea implements TableCellRenderer{
	
	TableDataCellRenderer(){
		
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		//Provides word-wrapping for the jtable's cells.
		
		setText(value.toString());
		setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
		
		if (table.getRowHeight(row) != getPreferredSize().height){
			table.setRowHeight(row, getPreferredSize().height);
		}
		
		return this;
	}

}
