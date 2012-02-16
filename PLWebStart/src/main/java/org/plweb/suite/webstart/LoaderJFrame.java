package org.plweb.suite.webstart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

public class LoaderJFrame extends JFrame {

	private static final long serialVersionUID = 3555732372365461392L;

	private PLWebEnvironment env = PLWebEnvironment.getInstance();

	private String diskRoot = env.getDiskRoot();
	private File jEditHome = new File(diskRoot, env.getJEditPath());

	private Message msg;

	public LoaderJFrame() {
		DefaultListModel listModel = new MessageListModel();
		JList listComp = new JList(listModel);
		listComp.setForeground(Color.DARK_GRAY);
		listComp.setBackground(Color.WHITE);
		listComp.setPreferredSize(new Dimension(400, 120));
		listComp.setBorder(BorderFactory.createEmptyBorder());
		listComp.setAutoscrolls(true);
		listComp.setFocusable(false);
		listComp.setVisibleRowCount(100);

		msg = new Message(listModel, listComp);
		JLabel labelComp;
		try {
			labelComp = new JLabel(new ImageIcon(new URL(env.getAdImage())));
		} catch (MalformedURLException ex) {
			labelComp = new JLabel("AD-NOT-FOUND");
		}
		labelComp.setPreferredSize(new Dimension(400, 100));
		setTitle("PLWeb WebStart Loader");
		setLayout(new BorderLayout());
		add(labelComp, BorderLayout.NORTH);
		add(listComp, BorderLayout.CENTER);
		setResizable(false);
		pack();
		setVisible(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension f = getSize();
		int x = (d.width - f.width) / 2;
		int y = (d.height - f.height) / 2;
		this.setBounds(x, y, f.width, f.height);
	}

	public void autoload() {
		try {
			downloadJEditPackage();

			downloadJEditPlugins();

			downloadLessonXML();

			startJEditor();

			setVisible(false);

		} catch (Exception ex) {
			msg.print(ex.getMessage());
		}
	}

	private void startJEditor() {
		jEditLoader loader = new jEditLoader();
		loader.setSettingsPath(jEditHome);
		loader.setWorkspacePath(new File(jEditHome, "workspace"));
		loader.setMenubarVisible(false);
		loader.load();
	}

	private void downloadJEditPlugins() throws Exception {
		String pluginPath = env.getPluginPath();
		File tempPlugins = new File(diskRoot);
		File filePlugins = new File(diskRoot, pluginPath);

		String[] pluginsAsc = env.getPluginsAsc();
		int i = 0;

		for (String plugin : env.getPlugins()) {
			URL urlPlugin = new URL(plugin);
			String pluginName = new File(urlPlugin.getFile()).getName();

			File tempPlugin = new File(tempPlugins, pluginName);
			File filePlugin = new File(filePlugins, pluginName);

			boolean downloaded = false;

			if (tempPlugin.exists()) {
				msg.print("Plug-in Checksum [" + pluginName + "]: ");

				String asc = pluginsAsc[i];
				if (asc != null) {
					if (asc.trim().equals(
							Checksum.md5(tempPlugin.getPath()).trim())) {
						msg.update("OK");
						downloaded = true;
					}
				}

				if (!downloaded) {
					msg.update("FAILED");
				}
			}

			if (!downloaded) {
				msg.print("Plug-in Download [" + pluginName + "]: ");

				HttpDownloaderRunnable run = new HttpDownloaderRunnable(plugin,
						tempPlugin);

				Thread thread = new Thread(run);
				thread.start();

				int n = 0;
				while (thread.isAlive()) {
					msg.update(String.valueOf(run.getLength()) + " bytes");
					Thread.sleep(200);
					n++;
				}

				msg.update(String.valueOf(tempPlugin.length() / 1204)
						+ " Kbytes");
			}

			AntTask.copyFile(tempPlugin, filePlugin);

			i++;
		}
	}

	private void downloadJEditPackage() throws Exception {

		// *** 檢查jEdit是否已經啟動
		File fileJEditLog = new File(jEditHome, "activity.log");
		if (fileJEditLog.exists() && !fileJEditLog.delete()) {
			throw new JEditExistsException();
		}

		// *** 下載jEdit套件
		URL urlPackage = new URL(env.getUrlPackage());
		String packageName = new File(urlPackage.getFile()).getName();
		File filePackage = new File(diskRoot, packageName);

		boolean downloaded = false;

		if (filePackage.exists()) {
			msg.print("Package Checksum [" + packageName + "]: ");

			String asc = env.getUrlPackageAsc();
			if (asc != null) {
				if (asc.trim().equals(
						Checksum.md5(filePackage.getPath()).trim())) {
					msg.update("OK");
					downloaded = true;
				}
			}

			if (!downloaded) {
				msg.update("FAILED");
			}
		}

		if (!downloaded) {
			msg.print("Package Download [" + packageName + "]: ");

			HttpDownloaderRunnable run;
			run = new HttpDownloaderRunnable(urlPackage, filePackage);

			Thread thread = new Thread(run);
			thread.start();

			int n = 0;
			while (thread.isAlive()) {
				msg.update(String.valueOf(run.getLength()) + " bytes");
				Thread.sleep(200);
				n++;
			}

			msg.update(String.valueOf(filePackage.length() / 1204) + " Kbytes");
		}

		// *** 解壓縮jEdit
		msg.print("Extracting to ".concat(jEditHome.getPath()));

		AntTask.delDir(jEditHome); // 移除舊資料夾
		AntTask.unzipFile(jEditHome, filePackage); // 解壓縮
	}

	private void downloadLessonXML() throws Exception {
		URL urlLesson = new URL(env.getUrlLesson());
		String lessonPath = env.getLessonPath();
		String lessonFile = env.getLessonFile();
		File fileLesson = new File(diskRoot, lessonPath);
		File fileLessonXml = new File(fileLesson.getParent(), lessonFile);

		msg.print("Download XML [" + lessonFile + "]: ");

		HttpDownloaderRunnable run;
		run = new HttpDownloaderRunnable(urlLesson, fileLessonXml, true);

		Thread thread = new Thread(run);
		thread.start();

		while (thread.isAlive()) {
			msg.update(String.valueOf(run.getLength()) + " bytes");
			Thread.sleep(200);
		}

		msg.update(String.valueOf(fileLessonXml.length() / 1204) + " Kbytes");
	}
}

class JEditExistsException extends Exception {
	private static final long serialVersionUID = 339216594956472283L;

	public JEditExistsException() {
		super("ERROR: Close the exist editor and try again.");
	}
}

class Message {
	private JList listComp;
	private DefaultListModel listModel;

	private String last;
	private String temp;

	public Message(DefaultListModel listModel, JList listComp) {
		this.listModel = listModel;
		this.listComp = listComp;
	}

	public void print(String text) {
		last = temp = text;
		listModel.addElement(text);
	}

	public void update(String text) {
		if (!temp.equals(last + text)) {
			listComp.clearSelection();
			listModel.remove(listModel.getSize() - 1);
			listModel.addElement(temp = last + text);
		}
	}
}

class Checksum {
	private static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert a byte array to
	// a HEX string
	public static String md5(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}

class MessageListModel extends DefaultListModel {
	private static final long serialVersionUID = -4987910955258369973L;

	public synchronized Object getElementAt(int index) {
		int size = getSize() - 1;
		if (index >= size) {
			index = size;
		}
		return elementAt(index);
	}
}
