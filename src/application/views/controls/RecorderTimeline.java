package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import application.utils.SimpleAlert;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class RecorderTimeline extends ScrollPane {
	@FXML
	private AnchorPane endPane;
	@FXML
	private HBox itemHBox;

	private RecorderTimelineItemFactory itemFactory = (timeline, path) -> new RecorderTimelineItem(timeline, path);
	private final ObservableList<RecorderTimelineItem> items = FXCollections.observableArrayList();
	private final ObjectProperty<RecorderTimelineItem> activeItem = new SimpleObjectProperty<RecorderTimelineItem>();

	/**
	 * Create recorder timeline.
	 */
	public RecorderTimeline() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("RecorderTimeline.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	/**
	 * Reset controls to predefined state.
	 */
	public void reset() {
		setActiveItem(null);
		items.clear();
		scrollToBegin();
	}

	/**
	 * Refresh list of mockups.
	 */
	public void refresh() {
		for (final RecorderTimelineItem item : items) {
			try {
				item.loadPreview();
			} catch (IOException e) { // NOPMD
				// ignore
			}
		}
	}

	/**
	 * Set item factory.
	 * 
	 * @param itemFactory
	 */
	public void setItemFactory(final RecorderTimelineItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	/**
	 * Get items.
	 * 
	 * @return
	 */
	public ObservableList<RecorderTimelineItem> getItems() {
		return items;
	}

	/**
	 * Append timeline item.
	 * 
	 * @param mockupPath
	 * @return
	 * @throws IOException
	 */
	public RecorderTimelineItem addItem(final Path mockupPath) throws IOException {
		final RecorderTimelineItem item = itemFactory.createItem(this, mockupPath);
		items.add(item);
		return item;
	}

	/**
	 * Insert mockup item after target.
	 * 
	 * @param target
	 * @param mockup
	 * @return
	 * @throws IOException
	 */
	public RecorderTimelineItem insertMockupAfter(final RecorderTimelineItem target, final Path mockup) throws IOException {
		final int index = items.indexOf(target);
		if (index == -1) {
			throw new IOException("Target item not found.");
		}
		final RecorderTimelineItem item = itemFactory.createItem(this, mockup);
		items.add(index + 1, item);
		return item;
	}

	/**
	 * Move item to target position.
	 * 
	 * @param target
	 * @param item
	 * @return
	 */
	public boolean moveItemTo(final RecorderTimelineItem target, final RecorderTimelineItem item) {
		final int index = items.indexOf(target);
		if (index != -1) {
			items.remove(item);
			items.add(index, item);
			return true;
		}
		return false;
	}

	/**
	 * Move item one one index to the right.
	 * 
	 * @param item
	 * @return
	 */
	public boolean moveRight(final RecorderTimelineItem item) {
		final int index = items.indexOf(item);
		if (index >= 0 && index < items.size() - 1) {
			items.remove(item);
			items.add(index + 1, item);
			return true;
		}
		return false;
	}

	/**
	 * Move item one one index to the left.
	 * 
	 * @param item
	 * @return
	 */
	public boolean moveLeft(final RecorderTimelineItem item) {
		final int index = items.indexOf(item);
		if (index > 0 && index < items.size()) {
			items.remove(item);
			items.add(index - 1, item);
			return true;
		}
		return false;
	}

	/**
	 * Scroll to begin.
	 */
	public void scrollToBegin() {
		setHvalue(getHmin());
	}

	/**
	 * Scroll to end.
	 */
	public void scrollToEnd() {
		setHvalue(getHmax());
	}

	/**
	 * Select first item.
	 */
	public void selectFirstItem() {
		// Select first recorder timeline item.
		if (items.size() > 0) {
			setActiveItem(items.get(0));
		}
	}

	/**
	 * Select last item.
	 */
	public void selectLastItem() {
		// Select last recorder timeline item.
		final int size = items.size();
		if (size > 0) {
			setActiveItem(items.get(size - 1));
		}
	}

	/**
	 * Select next item.
	 */
	public void selectNextItem() {
		final RecorderTimelineItem item = getActiveItem();
		if (item != null) {
			final int index = items.indexOf(item) + 1;
			if (index >= 1 && index < items.size()) {
				setActiveItem(items.get(index));
			}
		}
	}

	/**
	 * Select previous item.
	 */
	public void selectPreviousItem() {
		final RecorderTimelineItem item = getActiveItem();
		if (item != null) {
			final int index = items.indexOf(item) - 1;
			if (index >= 0 && index < items.size()) {
				setActiveItem(items.get(index));
			}
		}
	}

	@FXML
	private void initialize() {
		Bindings.bindContent(itemHBox.getChildren(), items);

		// Toggle active style class.
		activeItem.addListener((obsVal, oldVal, newVal) -> {
			if (oldVal != null) {
				oldVal.getStyleClass().remove("active");
			}
			if (newVal != null) {
				newVal.getStyleClass().add("active");
			}
		});
	}

	@FXML
	private void handleDragDropped(final DragEvent event) {
		boolean success = false;
		try {
			final Dragboard dragBoard = event.getDragboard();
			if (dragBoard.hasContent(MockupChooserItem.DATA_FORMAT)) {
				final Path mockupPath = Paths.get((String) dragBoard.getContent(MockupChooserItem.DATA_FORMAT));
				setActiveItem(addItem(mockupPath));
				success = true;
			}
		} catch (IOException e) {
			SimpleAlert.exception(e);
		} finally {
			event.setDropCompleted(success);
			event.consume();
		}
	}

	@FXML
	private void handleDragOver(final DragEvent event) {
		final Dragboard dragBoard = event.getDragboard();
		if (dragBoard.hasContent(MockupChooserItem.DATA_FORMAT)) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
	}

	public AnchorPane getEndPane() {
		return endPane;
	}

	public RecorderTimelineItem getActiveItem() {
		return activeItem.get();
	}

	public void setActiveItem(final RecorderTimelineItem item) {
		activeItem.set(item);
	}

	public ObjectProperty<RecorderTimelineItem> activeItemProperty() {
		return activeItem;
	}
}
