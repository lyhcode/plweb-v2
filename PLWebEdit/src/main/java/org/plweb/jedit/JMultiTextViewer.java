package org.plweb.jedit;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class JMultiTextViewer extends JTabbedPane {

	private static final long serialVersionUID = 515087633114228581L;
	
	public void clear() {
		this.removeAll();
	}
	
	public void showText(String title, String content) {
		this.add(title, new JScrollPane(new JTextArea(content)));
	}
}
