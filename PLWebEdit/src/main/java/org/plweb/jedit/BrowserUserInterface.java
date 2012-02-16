package org.plweb.jedit;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class BrowserUserInterface extends JPanel {

	private static final long serialVersionUID = -7815610502515979763L;
	private ProjectEnvironment env = ProjectEnvironment.getInstance();

	public BrowserUserInterface() {
		setLayout(new BorderLayout());
		// JPanel panel = new JPanel();

		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				createBrowser(), createDisplayBox());
		pane.setOneTouchExpandable(true);
		pane.setDividerLocation(300);

		add(pane);
	}

	private JComponent createBrowser() {
		JWebBrowser browser = new JWebBrowser();
		browser.setBarsVisible(false);
		env.setActiveBrowser(browser);

		browser.setBorder(BorderFactory.createTitledBorder("HTML Viewer"));
		
		//browser.setHTMLContent("<html><head><meta http-equiv=content-type content=\"text/html; charset=UTF-8\"></head><body></body></html>");
		return browser;
	}

	private JComponent createDisplayBox() {
		JMultiTextViewer viewer = new JMultiTextViewer();
		env.setActiveViewer(viewer);

		return viewer;
	}
}
