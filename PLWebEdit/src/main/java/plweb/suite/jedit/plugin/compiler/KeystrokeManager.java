package plweb.suite.jedit.plugin.compiler;

public class KeystrokeManager {
	private static KeystrokeManager SINGLETON;

	public static KeystrokeManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new KeystrokeManager();
		}
		return SINGLETON;
	}
	
	
}
