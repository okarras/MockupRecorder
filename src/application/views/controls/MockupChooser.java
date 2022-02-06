package application.views.controls;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import application.utils.SimpleAlert;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class MockupChooser extends ScrollPane {
	@FXML
	private VBox mockupVBox;

	private MockupChooserItemFactory itemFactory = (chooser, path) -> new MockupChooserItem(chooser, path);
	private final ObjectProperty<Path> rootDir = new SimpleObjectProperty<Path>();
	private final ObservableList<MockupChooserItem> items = FXCollections.observableArrayList();
	private final ObjectProperty<MockupChooserItem> activeItem = new SimpleObjectProperty<MockupChooserItem>();

	/**
	 * Create mockup chooser.
	 */
	public MockupChooser() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("MockupChooser.fxml"));
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
		setRootDir(null);
		setActiveItem(null);
		items.clear();
	}

	/**
	 * Refresh list of mockups.
	 */
	public void refresh() {
		try {
			buildList(getRootDir());
		} catch (IOException e) {
			SimpleAlert.exception(e);
		}
	}

	/**
	 * Set item factory.
	 * 
	 * @param itemFactory
	 */
	public void setItemFactory(final MockupChooserItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	@FXML
	private void initialize() {
		Bindings.bindContent(mockupVBox.getChildren(), items);

		// Update items when the root dir changes.
		rootDir.addListener((obsVal, oldVal, newVal) -> {
			setActiveItem(null);

			if (newVal == null) {
				items.clear();
				return;
			}

			try {
				buildList(newVal);
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		});

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

	/*
	 * Build list of mockups from given directory.
	 */
	private void buildList(final Path rootDir) throws IOException {
		if (rootDir == null) {
			return;
		}

		items.setAll(buildChildren(rootDir));

		// Select first item.
		if (items.size() > 0) {
			setActiveItem(items.get(0));
		}
	}

	private List<MockupChooserItem> buildChildren(final Path rootDir) throws IOException {
		final List<MockupChooserItem> list = new ArrayList<MockupChooserItem>();

		Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attr) {
				if (file.toString().endsWith(".fxml")) {
					try {
						list.add(itemFactory.createItem(MockupChooser.this, file));
					} catch (IOException e) { // NOPMD
						// Ignore.
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return list;
	}

	public Path getRootDir() {
		return rootDir.get();
	}

	public void setRootDir(final Path root) {
		rootDir.set(root);
	}

	public ObjectProperty<Path> rootDirProperty() {
		return rootDir;
	}

	public MockupChooserItem getActiveItem() {
		return activeItem.get();
	}

	public void setActiveItem(final MockupChooserItem item) {
		activeItem.set(item);
	}

	public ObjectProperty<MockupChooserItem> activeItemProperty() {
		return activeItem;
	}
}
