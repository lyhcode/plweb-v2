package plweb.suite.jedit.plugin.compiler;

import java.io.File;
import java.io.IOException;

import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XTask;
import org.plweb.suite.common.xml.XmlFactory;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class ProjectEnvironment {
	private String diskRoot = System.getProperty("plweb.diskroot");
	private String lessonPath = System.getProperty("plweb.lessonpath");
	private String lessonXml = System.getProperty("plweb.lessonxml");
	private String lessonMode = System.getProperty("plweb.lessonmode");
	private String requestUrl = System.getProperty("plweb.urlrequest");
	private String explorer = System.getProperty("plweb.explorer");
	private String shell = System.getProperty("plweb.shell");

	/**
	 * Specify active project, instead of activeLesson
	 */
	private XProject activeProject;
	private XTask activeTask = null;
	private MessageConsoleInterface activeConsole;
	private JWebBrowser activeBrowser;
	private JMultiTextViewer activeViewer;

	private static ProjectEnvironment instance = null;

	public static ProjectEnvironment getInstance() {
		if (instance == null) {
			return instance = new ProjectEnvironment();
		} else {
			return instance;
		}
	}

	/**
	 * Load Project object
	 */
	public void loadActiveProject() {
		File fileLesson = new File(diskRoot, lessonPath);
		File fileLessonXml = new File(diskRoot, lessonXml);

		activeProject = XmlFactory.readProject(fileLessonXml);
		activeProject.setRootPath(fileLesson);
		activeProject.writeToDisk();

		// run project onload commands.
		// activeProject.getPropertyEx("");
		if (getLessonMode().equalsIgnoreCase("student")) {
			for (String key : activeProject.getProperties().keySet()) {
				if (key.startsWith("project.onload.command")) {
					String execmd = activeProject.getPropertyEx(key);
					String[] cmd = getShell(execmd);
					Runtime runtime = Runtime.getRuntime();
					Process process;
					try {
						process = runtime.exec(cmd, null, new File(
								activeProject.getRootPath()));
						process.waitFor();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public String getLessonMode() {
		return lessonMode;
	}

	public void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
	}

	public XProject getActiveProject() {
		return activeProject;
	}

	public String getLessonXml() {
		return lessonXml;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setActiveTask(XTask task) {
		this.activeTask = task;
	}

	public XTask getActiveTask() {
		return this.activeTask;
	}

	public MessageConsoleInterface getActiveConsole() {
		return activeConsole;
	}

	public void setActiveConsole(MessageConsoleInterface activeConsole) {
		this.activeConsole = activeConsole;
	}

	public JWebBrowser getActiveBrowser() {
		return activeBrowser;
	}

	public void setActiveBrowser(JWebBrowser activeBrowser) {
		this.activeBrowser = activeBrowser;
	}

	public JMultiTextViewer getActiveViewer() {
		return activeViewer;
	}

	public void setActiveViewer(JMultiTextViewer activeViewer) {
		this.activeViewer = activeViewer;
	}

	public String getExplorer() {
		return explorer;
	}

	public String getExplorer(String root) {
		return explorer.replace("${root}", root);
	}

	public void setExplorer(String explorer) {
		this.explorer = explorer;
	}

	public String getShell() {
		return shell;
	}

	public String[] getShell(String command) {
		String[] result;
		String[] args = shell.split(" ");
		result = new String[args.length + 1];
		int i;
		for (i = 0; i < args.length; i++) {
			result[i] = args[i];
		}
		result[i] = command;
		return result;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}
}
