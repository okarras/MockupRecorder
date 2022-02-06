package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class MockupChooserItem extends BorderPane {
	/**
	 * 
	 */
	public static final DataFormat DATA_FORMAT = new DataFormat("text/MockupChooserItem");

	@FXML
	private ImageView previewImage;
	@FXML
	private Label fileNameLabel;

	private final MockupChooser mockupChooser;
	private final Path mockupPath;
	private final WritableImage mockupPreview;

	/**
	 * Create mockup chooser item.
	 * 
	 * @param mockupChooser
	 * @param mockupPath
	 * @throws IOException
	 */
	public MockupChooserItem(final MockupChooser mockupChooser, final Path mockupPath) throws IOException {
		super();

		this.mockupChooser = mockupChooser;
		this.mockupPath = mockupPath;

		// Create preview image.
		final FXMLLoader previewLoader = new FXMLLoader(mockupPath.toUri().toURL());
		final Scene scene = new Scene(previewLoader.load());
		mockupPreview = scene.getRoot().snapshot(null, null);

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("MockupChooserItem.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	@FXML
	private void initialize() {
		fileNameLabel.setText(mockupPath.getFileName().toString());
		previewImage.setImage(mockupPreview);
	}

	@FXML
	private void handleClicked(final MouseEvent event) {
		mockupChooser.setActiveItem(this);
		event.consume();
	}

	@FXML
	private void handleDragDetected(final MouseEvent event) {
		final Dragboard dragBoard = startDragAndDrop(TransferMode.COPY);
		final ClipboardContent content = new ClipboardContent();
		content.put(DATA_FORMAT, mockupPath.toString());
		dragBoard.setContent(content);
		event.consume();
	}

	public Path getMockupPath() {
		return mockupPath;
	}
}
