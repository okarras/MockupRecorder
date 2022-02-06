package application.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 * 
 * @author Lennart Glauer
 *
 */
public final class SimpleAlert {
	private SimpleAlert() {
		// Utility class.
	}

	/**
	 * Build custom alert.
	 * 
	 * @param type
	 * @param title
	 * @param header
	 * @param content
	 * @return
	 */
	public static Optional<ButtonType> alert(final AlertType type, final String title, final String header, final String content) {
		final Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.setResizable(true);
		return alert.showAndWait();
	}

	/**
	 * Show exception dialog.
	 * 
	 * @param throwable
	 */
	public static void exception(final Throwable throwable) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception");
		alert.setHeaderText(null);
		alert.setContentText("Upps, da ist etwas schief gelaufen.");
		alert.setResizable(true);

		final StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));

		final TextArea textArea = new TextArea(writer.toString());
		textArea.setEditable(false);

		alert.getDialogPane().setExpandableContent(textArea);
		alert.showAndWait();
	}

	/**
	 * Show error dialog.
	 * 
	 * @param content
	 */
	public static void error(final String content) {
		alert(AlertType.ERROR, "Fehler", null, content);
	}

	/**
	 * Show warning dialog.
	 * 
	 * @param content
	 */
	public static void warning(final String content) {
		alert(AlertType.WARNING, "Warnung", null, content);
	}

	/**
	 * Show information dialog.
	 * 
	 * @param content
	 */
	public static void info(final String content) {
		alert(AlertType.INFORMATION, "Info", null, content);
	}

	/**
	 * Show confirm dialog.
	 * 
	 * @param content
	 * @return 
	 */
	public static ButtonType confirm(final String content) {
		final Optional<ButtonType> result = alert(AlertType.CONFIRMATION, "Best�tigen", null, content);
		return result.isPresent() ? result.get() : ButtonType.CLOSE;
	}
}
