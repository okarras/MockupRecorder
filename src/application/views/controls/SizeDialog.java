package application.views.controls;

import java.io.FileInputStream;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class SizeDialog extends Dialog<Pair<Integer, Integer>> {
	@FXML
	private TextField widthInput;
	@FXML
	private TextField heightInput;

	/**
	 * Create size dialog.
	 */
	public SizeDialog() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("SizeDialog.fxml"));
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
		setTitle("Viewport ändern");
		try {	// Sets the icon to the about stage
        	FileInputStream logoInput = new FileInputStream("src/application/images/logo.png");
			Stage stage = (Stage) getDialogPane().getScene().getWindow();
        	stage.getIcons().add(new Image(logoInput));
        } catch(IOException e) {
			e.printStackTrace();
		}
		getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
		setResultConverter(type -> {
			if (type == ButtonType.APPLY) {
				final IntegerStringConverter converter = new IntegerStringConverter();
				final int width = converter.fromString(widthInput.getText());
				final int height = converter.fromString(heightInput.getText());
				return new Pair<Integer, Integer>(width, height);
			}
			return null;
		});
	}
}
