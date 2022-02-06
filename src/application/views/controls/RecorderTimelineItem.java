package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import application.interaction.IAction;
import application.utils.SimpleAlert;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class RecorderTimelineItem extends BorderPane {
	/**
	 * Drag &amp; drop data format.
	 */
	public static final DataFormat DATA_FORMAT = new DataFormat("text/TimeLineMockupItem");

	@FXML
	private ImageView previewImage;
	@FXML
	private Label fileNameLabel;

	private final ObservableList<IAction> actions = FXCollections.observableArrayList();
	private final ContextMenu contextMenu = new ContextMenu();
	private final RecorderTimeline recorderTimeline;
	private final Path mockupPath;

	/**
	 * Create recorder timeline item.
	 * 
	 * @param recorderTimeline
	 * @param mockupPath
	 * @throws IOException
	 */
	public RecorderTimelineItem(final RecorderTimeline recorderTimeline, final Path mockupPath) throws IOException {
		super();

		this.recorderTimeline = recorderTimeline;
		this.mockupPath = mockupPath;

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("RecorderTimelineItem.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	/**
	 * Load preview image.
	 * 
	 * @throws IOException 
	 */
	public void loadPreview() throws IOException {
		final FXMLLoader previewLoader = new FXMLLoader(mockupPath.toUri().toURL());
		final Scene scene = new Scene(previewLoader.load());
		previewImage.setImage(scene.getRoot().snapshot(null, null));
	}

	@FXML
	private void initialize() {
		try {
			loadPreview();
		} catch (IOException e) { // NOPMD
			// ignore
		}
		fileNameLabel.setText(String.format("%s [0]", mockupPath.getFileName().toString()));
		actions.addListener((Observable obsVal) -> fileNameLabel.setText(String.format(Locale.GERMANY, "%s [%d]", mockupPath.getFileName().toString(), actions.size())));
	}

	@FXML
	private void handleClicked(final MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			contextMenu.hide();
			recorderTimeline.setActiveItem(this);
		}
		event.consume();
	}

	@FXML
	private void handleContextMenu(final ContextMenuEvent event) {
		contextMenu.hide();
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
		event.consume();
	}

	@FXML
	private void handleDragDetected(final MouseEvent event) {
		final Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
		final ClipboardContent content = new ClipboardContent();
		content.put(DATA_FORMAT, mockupPath.toString());
		dragBoard.setContent(content);
		event.consume();
	}

	@FXML
	private void handleDragDropped(final DragEvent event) {
		final Dragboard dragBoard = event.getDragboard();
		if (dragBoard.hasContent(DATA_FORMAT)) {
			handleMoveEvent(event);
		} else if (dragBoard.hasContent(MockupChooserItem.DATA_FORMAT)) {
			handleInsertEvent(event);
		}
	}

	private void handleMoveEvent(final DragEvent event) {
		final RecorderTimelineItem source = (RecorderTimelineItem) event.getGestureSource();
		boolean success = false;

		if (!equals(source)) {
			success = recorderTimeline.moveItemTo(this, source);
		}

		event.setDropCompleted(success);
		event.consume();
	}

	private void handleInsertEvent(final DragEvent event) {
		boolean success = false;
		try {
			final Dragboard dragBoard = event.getDragboard();
			final Path mockup = Paths.get((String) dragBoard.getContent(MockupChooserItem.DATA_FORMAT));
			recorderTimeline.setActiveItem(recorderTimeline.insertMockupAfter(this, mockup));
			success = true;
		} catch (IOException e) {
			SimpleAlert.exception(e);
		} finally {
			event.setDropCompleted(success);
			event.consume();
		}
	}

	@FXML
	private void handleDragEntered(final DragEvent event) {
		if (equals(event.getGestureSource())) {
			return;
		}

		final Dragboard dragBoard = event.getDragboard();
		if (dragBoard.hasContent(DATA_FORMAT)) {
			final ObservableList<RecorderTimelineItem> siblings = recorderTimeline.getItems();
			final boolean isDragLeft = siblings.indexOf(this) < siblings.indexOf(event.getGestureSource());
			if (isDragLeft) {
				getStyleClass().add("drag-n-drop-before");
			} else {
				getStyleClass().add("drag-n-drop-after");
			}
		} else if (dragBoard.hasContent(MockupChooserItem.DATA_FORMAT)) {
			getStyleClass().add("drag-n-drop-after");
		}

		event.consume();
	}

	@FXML
	private void handleDragExited(final DragEvent event) {
		getStyleClass().remove("drag-n-drop-before");
		getStyleClass().remove("drag-n-drop-after");
		event.consume();
	}

	@FXML
	private void handleDragOver(final DragEvent event) {
		if (equals(event.getGestureSource())) {
			return;
		}

		final Dragboard dragBoard = event.getDragboard();
		if (dragBoard.hasContent(DATA_FORMAT)) {
			event.acceptTransferModes(TransferMode.MOVE);
		} else if (dragBoard.hasContent(MockupChooserItem.DATA_FORMAT)) {
			event.acceptTransferModes(TransferMode.COPY);
		}

		event.consume();
	}

	public Path getMockupPath() {
		return mockupPath;
	}

	public ObservableList<IAction> getActions() {
		return actions;
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}
}
