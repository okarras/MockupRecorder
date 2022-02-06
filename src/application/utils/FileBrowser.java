package application.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File browser utility class.
 * 
 * @author Lennart Glauer
 * 
 * @notice java.awt.Desktop does not work on linux.
 *
 */
public final class FileBrowser {

	private FileBrowser() {
		// Utility class.
	}

	/**
	 * Opens a file or directory in the users default browser.
	 * 
	 * @param file File/Folder to open
	 * @return True if successful
	 * @throws IOException 
	 */
	public static boolean open(final Path file) throws IOException {
		if (!Files.exists(file)) {
			throw new IOException("Datei wurde nicht gefunden.");
		}

		if (OS.isWindows()) {
			try {
				// Windows long path names workaround.
				if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
					throw new IOException("Java AWT Desktop wird nicht unterstützt.");
				}

				Desktop.getDesktop().open(file.toFile());
				return true;

			} catch (IOException e) {
				return runCommand("explorer", file.toAbsolutePath().toString());
			}

		} else if (OS.isMacOS()) {
			return runCommand("open", file.toAbsolutePath().toString());

		} else if (OS.isLinux()) {
			final String filePath = file.toAbsolutePath().toString();

			return runCommand("xdg-open", filePath)			// All
					|| runCommand("kde-open", filePath)		// KDE
					|| runCommand("exo-open", filePath)		// Xfce
					|| runCommand("gvfs-open", filePath)	// GNOME
					|| runCommand("gnome-open", filePath)	// GNOME (deprecated)
					|| runCommand("pcmanfm", filePath);		// LXDE

		} else {
			return false;
		}
	}

	/**
	 * Appends the suffix to the filename (before file extension).
	 * 
	 * @param fileName Original filename
	 * @param suffix Suffix to append
	 * @return New filename
	 */
	public static String appendFilename(final String fileName, final String suffix) {
		final int ext = fileName.lastIndexOf('.');

		return ext == -1
				? fileName + suffix
				: fileName.substring(0, ext) + suffix + fileName.substring(ext);
	}

	/*
	 * Run a shell command.
	 * 
	 * @param cmd Command to execute
	 * @return True if successful
	 */
	private static boolean runCommand(final String... cmd) {
		try {
			final Process process = Runtime.getRuntime().exec(cmd);
			return process.isAlive();

		} catch (IOException e) {
			return false;
		}
	}
}
