package org.plweb.jedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XTask;

import chrriis.dj.nativeswing.swtimpl.components.HTMLEditorSaveEvent;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class CompilerUserInterface extends JPanel implements ActionListener {

	private ProjectEnvironment env = ProjectEnvironment.getInstance();

	private static final long serialVersionUID = 3256720676110022706L;

	private CompilerRunner runner;

	private JComboBox comboTask;
	private JComboBox comboMode;

	private MessageManager mm = MessageManager.getInstance();

	private MessageConsoleInterface console = MessageConsole.getInstance();

	private BufferChangeListener bcl = BufferChangeListener.getInstance();

	public CompilerUserInterface() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 3, 3));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400, 300));

		JPanel p = new JPanel(new BorderLayout());
		p.add(createConsoleTextPane(), BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);

		String[] tasks = {};
		String[] modes;

		if (env.getLessonMode().equals("author")) {
			modes = new String[] { "author", "student", "teacher" };
		} else if (env.getLessonMode().equals("teacher")) {
			modes = new String[] { "teacher", "student" };
		} else {
			modes = new String[] { env.getLessonMode() };
		}

		JToolBar tb1 = new JToolBar();
		tb1.setFloatable(false);
		tb1.add(createButton("上一題", "control_rewind.png", "task.previous", false));
		tb1.add(comboTask = createComboBox(tasks, "task.select"));
		tb1.add(createButton("下一題", "control_fastforward.png", "task.next", false));

		JToolBar tb2 = new JToolBar();
		tb2.setFloatable(false);

		if (env.getLessonMode().equals("author")) {
			tb2.add(createButton("upload project", "database_save.png",
					"project.upload"));
			tb2.add(new JToolBar.Separator());
		}
		tb2.add(comboMode = createComboBox(modes, "mode.select"));
		tb2.add(new JToolBar.Separator());
		tb2.add(createButton("reload exercise", "arrow_refresh.png",
				"task.reload"));

		if (env.getLessonMode().equals("author")) {
			tb2.add(createButton("open explorer", "drive.png", "explorer.open"));

			tb2.add(createButton("edit project", "book_edit.png",
					"project.edit"));

			tb2.add(createButton("html editor", "world_edit.png", "html.edit"));

			tb2.add(new JToolBar.Separator());

			tb2.add(createButton("edit exercise", "page_edit.png", "task.edit"));
			tb2.add(createButton("add exercise", "page_add.png", "task.add"));
			tb2.add(createButton("delete exercise", "page_delete.png",
					"task.delete"));
			tb2.add(createButton("move up exercise", "arrow_up.png", "task.up"));
			tb2.add(createButton("move down exercise", "arrow_down.png",
					"task.down"));
		}

		JPanel p3 = new JPanel(new BorderLayout());
		p3.add(tb1, BorderLayout.CENTER);
		p3.add(tb2, BorderLayout.WEST);

		add(p3, BorderLayout.NORTH);

		// �Ȯ�, �ѨM�s���
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException ex) {
		// ex.printStackTrace();
		// }

		/*
		 * First Time reload
		 */
		refreshTaskComboBox();
		reloadTask();
	}

	// protected void finalize() throws Throwable {
	// // send 'end' message for before task
	// XTask beforeTask = env.getActiveTask();
	//
	// if (beforeTask != null) {
	// mm.saveMessage(beforeTask.getId(), "end", "", "", "", 0, 0, 0);
	// }
	//
	// super.finalize();
	// }

	private JComponent createConsoleTextPane() {
		JTextPane textPaneConsole = new JTextPane();
		((MessageConsole) console).setTextPaneConsole(textPaneConsole);
		env.setActiveConsole(console);

		textPaneConsole.setFont(new Font("Courier New", Font.PLAIN, 13));
		textPaneConsole.setForeground(Color.BLACK);
		textPaneConsole.setBackground(Color.WHITE);
		textPaneConsole.setEditable(false);

		return new JScrollPane(textPaneConsole);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		System.err.println(cmd);
		if (cmd.equals("task.select")) {
			reloadTask();
		} else if (cmd.equals("explorer.open")) {
			openExplorer();
		} else if (cmd.equals("task.up")) {
			upTask();
		} else if (cmd.equals("task.down")) {
			downTask();
		} else if (cmd.equals("task.add")) {
			addTask();
		} else if (cmd.equals("task.delete")) {
			delTask();
		} else if (cmd.equals("task.reload")) {
			reloadTask();
		} else if (cmd.equals("task.previous")) {
			int idx = comboTask.getSelectedIndex();
			if (idx > 0) {
				comboTask.setSelectedIndex(idx - 1);
			}
			reloadTask();
		} else if (cmd.equals("task.next")) {
			int idx = comboTask.getSelectedIndex();
			if (idx + 1 < comboTask.getItemCount()) {
				comboTask.setSelectedIndex(idx + 1);
			}
			reloadTask();
		} else if (cmd.equals("task.edit")) {
			int idx = comboTask.getSelectedIndex();
			if (idx >= 0) {
				XTask task = env.getActiveProject().getTasks().get(idx);
				if (task != null) {
					new FrameTaskEditor(this, task);
				}
			}
		} else if (cmd.equals("project.upload")) {
			saveProject();
		} else if (cmd.equals("project.edit")) {
			new FrameProjectEditor(env.getActiveProject());
		} else if (cmd.equals("html.edit")) {
			html();
		}
	}

	private void openExplorer() {
		XProject project = env.getActiveProject();
		Runtime runtime = Runtime.getRuntime();
		try {
			String root = new File(project.getRootPath()).getPath();
			String[] shell = env.getShell(env.getExplorer(root));
			runtime.exec(shell).waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void saveProject() {
		new Thread(new ProjectUploader()).start();
	}

	/**
	 * Task Move Backward
	 */
	private void upTask() {
		XProject actProject = env.getActiveProject();
		int idx = comboTask.getSelectedIndex();
		if (idx > 0) {
			XTask task = actProject.getTask(idx);

			actProject.insertTask(idx - 1, task);
			actProject.removeTask(idx + 1);
		}
		refreshTaskComboBox();
		int newIdx = idx - 1;
		if (newIdx < 0) {
			newIdx = 0;
		}
		comboTask.setSelectedIndex(newIdx);
	}

	/**
	 * Task Move Forward
	 */
	private void downTask() {
		XProject actProject = env.getActiveProject();
		int idx = comboTask.getSelectedIndex();
		if (idx < comboTask.getItemCount()) {
			XTask task = actProject.getTask(idx + 1);

			actProject.insertTask(idx, task);
			actProject.removeTask(idx + 2);
		}
		refreshTaskComboBox();
		int newIdx = idx + 1;
		if (newIdx > comboTask.getItemCount()) {
			newIdx = comboTask.getItemCount();
		}
		comboTask.setSelectedIndex(newIdx);
	}

	/**
	 * Add New Task
	 */
	private void addTask() {
		XProject project = env.getActiveProject();

		XTask newTask = new XTask();

		int taskCount = getTaskCount();
		project.setProperty("task.identity", String.valueOf(taskCount + 1));
		newTask.setId(String.valueOf(taskCount));
		newTask.setTitle("New Task");

		int idx = comboTask.getSelectedIndex();
		if (project.getTasks().size() > 0 && idx >= 0) {
			XTask srcTask = env.getActiveProject().getTask(idx);
			if (srcTask != null) {
				newTask.setTitle(srcTask.getTitle());
				newTask.setCommands(srcTask.getCommands());

				Map<String, String> newProps = new HashMap<String, String>();
				newProps.putAll(srcTask.getProperties());
				newTask.setProperties(newProps);

				for (String key : newTask.getPropertyKeys("askfor.")) {
					String question = project.getTaskPropertyEx(newTask, key);

					String varName = key.substring(7); // length("askfor.")
					String varValue = JOptionPane.showInputDialog(question);

					newTask.setProperty(varName, varValue);
				}

			}
		}

		project.insertTask(idx + 1, newTask);

		refreshTaskComboBox();
		int newIdx = idx + 1;
		if (newIdx > comboTask.getItemCount()) {
			newIdx = comboTask.getItemCount();
		}
		comboTask.setSelectedIndex(newIdx);
	}

	/**
	 * Remove Specified Task
	 */
	private void delTask() {
		int idx = comboTask.getSelectedIndex();
		if (idx >= 0) {
			env.getActiveProject().removeTask(idx);
		}
		refreshTaskComboBox();
		int newIdx = idx - 1;
		if (newIdx < 0)
			newIdx = 0;
		comboTask.setSelectedIndex(newIdx);
	}

	private int getTaskCount() {
		int result = 1;
		String taskCount = env.getActiveProject().getProperty("task.identity");
		if (taskCount != null) {
			try {
				result = Integer.valueOf(taskCount);
			} catch (NumberFormatException ex) {
			}
		}
		return result;
	}

	public void refreshTaskComboBox() {
		refreshTaskComboBox(false);
	}

	public void refreshTaskComboBox(boolean keepIdx) {
		int idx = comboTask.getSelectedIndex();
		comboTask.removeActionListener(this);
		comboTask.removeAllItems();
		int c = 1;
		
		XProject project = env.getActiveProject();
		
		if (project==null) {
			System.err.println("Project not loaded.");
			return;
		}
		
		for (XTask xtask : env.getActiveProject().getTasks()) {
			comboTask.addItem(String.valueOf(c).concat(" - ")
					.concat(xtask.getTitle()));
			c++;
		}
		comboTask.addActionListener(this);
		if (keepIdx) {
			comboTask.setSelectedIndex(idx);
		}
	}

	/**
	 * Reload Task
	 */
	public void reloadTask() {

		XProject project = env.getActiveProject();

		if (project == null || project.getTasks().size() == 0) {
			return;
		}

		// send 'end' message for before task
		XTask beforeTask = env.getActiveTask();

		// if (beforeTask != null) {
		// mm.saveMessage(
		// beforeTask.getId(),
		// (Long) beforeTask.getTempAttribute("time.begin"),
		// (Long) beforeTask.getTempAttribute("time.begin"),
		// 0,
		// "end",
		// "",
		// "End of exercise.",
		// getMainBufferText(),
		// "",
		// "",
		// 0,
		// ""
		// );
		// }

		View actView = jEdit.getActiveView();
		jEdit.closeAllBuffers(actView);

		int idx = comboTask.getSelectedIndex();
		XTask task = project.getTask(idx);

		if (task == null) {
			return;
		}

		env.setActiveTask(task);

		String rootPath = project.getRootPath();

		String fileMain = project.getTaskPropertyEx(task, "file.main");

		Buffer mainBuffer = null;

		if (fileMain != null) {
			String filePath = new File(rootPath, fileMain).getPath();
			mainBuffer = jEdit.openFile(actView, filePath);
		}

		if (mainBuffer == null) {
			return;
		}

		task.setTempAttribute("time.begin", new Date().getTime());

		// mm.saveMessage(
		// task.getId(),
		// (Long) task.getTempAttribute("time.begin"),
		// (Long) task.getTempAttribute("time.begin"),
		// 0,
		// "begin",
		// "",
		// "Begin of exercise.",
		// "",
		// "",
		// "",
		// 0,
		// ""
		// );

		openProjectHtml(task);

		for (String key : task.getProperties().keySet()) {
			if (env.getLessonMode().equals("author")
					&& key.startsWith("file.author")) {
				String file = new File(rootPath, project.getTaskPropertyEx(
						task, key)).getPath();
				jEdit.openFile(actView, file);
			} else if (key.startsWith("file.attach")) {
				String file = new File(rootPath, project.getTaskPropertyEx(
						task, key)).getPath();
				jEdit.openFile(actView, file);
			}
		}

		openFileViewer(task);

		jEdit.getActiveView().setBuffer(mainBuffer);

		mainBuffer.addBufferListener(bcl);

		console.switchTo(idx);

		// mm.saveMessage(task.getId(), "start", "", "");
	}

	private void openFileViewer(XTask task) {
		XProject project = env.getActiveProject();

		if (project == null) {
			return;
		}

		String rootPath = project.getRootPath();

		env.getActiveViewer().clear();

		for (String key : task.getProperties().keySet()) {
			if (key.startsWith("file.view")) {
				String keyLabel = key.replace("file.view", "label.view");
				String title = project.getTaskPropertyEx(task, keyLabel);
				String path = new File(rootPath, project.getTaskPropertyEx(
						task, key)).getPath();
				String content = TextfileUtilities.readText(path);
				env.getActiveViewer().showText(title, content);
			}
		}
	}

	private void openProjectHtml(XTask task) {

		XProject project = env.getActiveProject();

		if (project == null) {
			return;
		}

		String rootPath = project.getRootPath();

		String fileHtml = project.getTaskPropertyEx(task, "file.html");

		if (fileHtml != null) {
			JWebBrowser browser = env.getActiveBrowser();
			String url = null;

			if (isValidHttpURL(fileHtml)) {
				url = fileHtml.trim();
			} else {
				url = new File(rootPath, fileHtml).toURI().toString();
			}
			// browser
			// .setHTMLContent("<html><head><meta http-equiv=content-type content=\"text/html; charset=UTF-8\"></head><body></body></html>");

			if (url != null) {
				browser.navigate(url);
			}
		}
	}

	/**
	 * URL Validation
	 * 
	 * @param url
	 * @return
	 */
	private boolean isValidHttpURL(String url) {
		return url.startsWith("http://") || url.startsWith("https://");
	}

	/**
	 * Read all buffer text from jEdit
	 * 
	 * @return
	 */
	protected String getMainBufferText() {
		String result = null;

		XProject project = env.getActiveProject();
		XTask task = env.getActiveTask();
		String rootPath = env.getActiveProject().getRootPath();
		View view = jEdit.getActiveView();

		if (task != null) {
			String fileMain = project.getTaskPropertyEx(task, "file.main");

			if (fileMain != null) {
				String pathMain = new File(rootPath, fileMain).getPath();
				Buffer buffer = jEdit.getBuffer(pathMain);

				if (buffer == null) {
					buffer = jEdit.openFile(view, pathMain);
				}
				result = buffer.getText(0, buffer.getLength());
			}
		}

		return result;
	}

	protected ImageIcon createImageIcon(String path) {
		URL imgURL = this.getClass().getResource(path);
		return new ImageIcon(imgURL);
	}

	protected JButton createButton(String text, String icon, String cmd) {
        return createButton(text, icon, cmd, true);
    }

	protected JButton createButton(String text, String icon, String cmd, boolean iconOnly) {
		JButton button = new JButton(createImageIcon(icon));
		button.setMargin(new Insets(5, 5, 5, 5));
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setToolTipText(text);
        if (!iconOnly) {
            button.setText(text);
        }
		button.setFont(new Font("Serif", Font.PLAIN, 13));
		button.setActionCommand(cmd);
		button.addActionListener(this);
		return button;
	}

	protected JComboBox createComboBox(String[] data, String cmd) {
		JComboBox obj = new JComboBox(data);
		obj.setActionCommand(cmd);
		obj.addActionListener(this);
		return obj;
	}

	/**
	 * Action: Run
	 */
	public void run() {
		if (runner != null && runner.isAlive()) {
			View v = jEdit.getActiveView();
			GUIUtilities.message(v, "compiler.dialog0", null);
		} else {
			View actView = jEdit.getActiveView();
			jEdit.saveAllBuffers(actView, false);

			String mode = (String) comboMode.getSelectedItem();

			runner = new CompilerRunner(console, mode, getMainBufferText());
			runner.start();
		}
	}

	/**
	 * Action Interrupt
	 */
	public void interrupt() {
		if (runner != null && runner.isAlive()) {
			runner.interrupt();
		} else {
			View v = jEdit.getActiveView();
			GUIUtilities.message(v, "compiler.dialog1", null);
		}
	}

	//private JHTMLEditor htmlEditor = null;
	private JFrame htmlFrame = null;
	private Buffer htmlBuffer = null;

	/**
	 * Action: HTML Edit
	 */
	public void html() {
        /*
		if (htmlFrame == null) {
			htmlEditor = new JHTMLEditor();
			htmlFrame = new JFrame();
			htmlFrame.setSize(640, 480);
			htmlFrame.add(htmlEditor);
			htmlEditor.addHTMLEditorListener(this);
		}

		Buffer buffer = jEdit.getActiveView().getBuffer();
		String bufferText = buffer.getText(0, buffer.getLength());

		htmlBuffer = buffer;
		htmlEditor.setHTMLContent(bufferText);

		htmlFrame.setTitle("HTML Editor - ".concat(buffer.getPath()));
		htmlFrame.setVisible(true);
        */
	}

	public void saveHTML(HTMLEditorSaveEvent arg0) {
		/*String htmlContent = htmlEditor.getHTMLContent();
		htmlContent = htmlContent.replace("\r\n", "\n");
		htmlBuffer.remove(0, htmlBuffer.getLength());
		htmlBuffer.insert(0, htmlContent);
		htmlFrame.setVisible(false);*/
	}

	class ProjectUploader implements Runnable {

		public void run() {
			console.print("Saving project ");
			XProject project = env.getActiveProject();
			project.readFromDisk();

			Thread thread = mm.saveProject(project);
			while (thread.isAlive()) {
				console.print(".");
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {

				}
			}
			console.println("done");
		}

	}
}
