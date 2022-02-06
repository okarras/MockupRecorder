package application.views.controls;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class AboutDialog extends Dialog<Void> {
	/**
	 * Create about dialog.
	 */
	public AboutDialog() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutDialog.fxml"));
		loader.setRoot(getDialogPane());
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	@FXML
	private void initialize() {
		setTitle("Über den MockupRecorder");
		getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		try {	// Sets the icon to the about stage
        	FileInputStream logoInput = new FileInputStream("src/application/images/logo.png");
			Stage stage = (Stage) getDialogPane().getScene().getWindow();
        	stage.getIcons().add(new Image(logoInput));
        } catch(IOException e) {
			e.printStackTrace();
		}
	}
}
