import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

@SuppressWarnings("serial")
public class ViewerCellRenderer extends DefaultListCellRenderer {
	
	public static final String HTML_1 = "<html><body style ='width: ";
	public static final String HTML_2 = "px'>";
	public static final String HTML_3 = "</html>";
	private int width;
	
	public ViewerCellRenderer(int width){
		this.width = width;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	
		String text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString() + HTML_3;
	
		Component comp = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
		
		//Sets the background colour of each cell. If index is even, colour is blue, else cell colour is white.
		if(index % 2 == 0){
			comp.setBackground(new Color(135,206,250));
		}
		else{
			comp.setBackground(new Color(255,255,240));
		}
		
		
		return comp;
	}
}
