package application.utils;

import java.util.Locale;

/**
 * Operating system helper class.
 * 
 * @author Lennart Glauer
 *
 */
public enum OS {

	UNKNOWN,
	WINDOWS,
	MACOS,
	LINUX,
	SOLARIS;

	private static final OS CURRENT_OS = detectOS();

	/**
	 * Get current operating system.
	 * 
	 * @return
	 */
	public static OS get() {
		return CURRENT_OS;
	}

	/**
	 * Test if current operating system is windows.
	 * 
	 * @return
	 */
	public static boolean isWindows() {
		return CURRENT_OS == WINDOWS;
	}

	/**
	 * Test if current operating system is macOS.
	 * 
	 * @return
	 */
	public static boolean isMacOS() {
		return CURRENT_OS == MACOS;
	}

	/**
	 * Test if current operating system is linux/solaris.
	 * 
	 * @return
	 */
	public static boolean isLinux() {
		return CURRENT_OS == LINUX || CURRENT_OS == SOLARIS;
	}

	/*
	 * Detect current operating system.
	 * 
	 * @return Current operating system
	 */
	private static OS detectOS() {
		final String os = System.getProperty("os.name").toLowerCase(Locale.GERMANY);

		if (os.contains("win")) {
			return WINDOWS;

		} else if (os.contains("mac")) {
			return MACOS;

		} else if (os.contains("linux") || os.contains("unix")) {
			return LINUX;

		} else if (os.contains("solaris") || os.contains("sunos")) {
			return SOLARIS;

		} else {
			return UNKNOWN;
		}
	}

}
