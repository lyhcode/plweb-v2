package org.plweb.suite.webstart;

import java.awt.Toolkit;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import javax.swing.UIManager;
import javax.swing.JFrame;

import org.gjt.sp.jedit.proto.jeditresource.Handler;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

/**
 * PLWeb Editor Startup
 * 
 * @author Yan-hong Lin
 * 
 */
public class RunEditor {
	public static void main(String args[]) {

		// Unlock Security Management
		System.setSecurityManager(new UnsafeSecurityManager());

		// Set URL Stream Handler for jeditresource
		try {
			URLStreamHandlerFactory factory = new JEditURLStreamHandlerFactory();
			if (factory != null) {
				URL.setURLStreamHandlerFactory(factory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// NativeInterfaceHandler.init();
		// Here goes the rest of the initialization
		// NativeInterfaceHandler.runEventPump();
		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
        }

		// Loader Window
		LoaderJFrame loader = new LoaderJFrame();
		loader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loader.autoload();
	}
}

/**
 * solve linux plug-in problems
 *
 */
class JEditURLStreamHandlerFactory implements URLStreamHandlerFactory {
	public JEditURLStreamHandlerFactory() {
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (protocol.equals("jeditresource")) {
			return new Handler();
		}
		return null;
	}
}
