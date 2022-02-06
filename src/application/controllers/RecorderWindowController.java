package application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import application.interaction.ActionEventFilter;
import application.interaction.DefaultActionTimelineBuilder;
import application.interaction.IActionTimelineBuilderStrategy;
import application.models.IDataAccessObject;
import application.models.InteractionModel;
import application.models.ScenarioDAO;
import application.models.ScenarioModel;
import application.utils.FileBrowser;
import application.utils.ImageToMockupConverter;
import application.utils.ScenarioToImageConverter;
import application.utils.ScenarioToTextConverter;
import application.utils.SimpleAlert;
import application.video.IVideoCaptureFacade;
import application.video.JavaFXCapture;
import application.views.controls.AboutDialog;
import application.views.controls.MockupChooser;
import application.views.controls.MockupChooserItem;
import application.views.controls.MockupPane;
import application.views.controls.RecorderTimeline;
import application.views.controls.RecorderTimelineItem;
import application.views.controls.RecorderToolBar;
import application.views.controls.ScenarioTextDialog;
import application.views.controls.SizeDialog;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class RecorderWindowController extends AbstractController {
	@FXML
	private MenuItem saveProjectMenuItem;
	@FXML
	private MenuItem saveAsProjectMenuItem;
	@FXML
	private MenuItem openProjectDirectoryMenuItem;
	@FXML
	private Menu projectScenariosMenu;
	@FXML
	private MenuItem togglePlayingMenuItem;
	@FXML
	private MenuItem toggleRecordingMenuItem;
	@FXML
	private MenuItem nextItemMenuItem;
	@FXML
	private MenuItem previousItemMenuItem;
	@FXML
	private MenuItem toBeginMenuItem;
	@FXML
	private MenuItem toEndMenuItem;
	@FXML
	private MenuItem moveRightItemMenuItem;
	@FXML
	private MenuItem moveLeftItemMenuItem;
	@FXML
	private MenuItem exportTextMenuItem;
	@FXML
	private MenuItem exportImageMenuItem;
	@FXML
	private MenuItem exportVideoMenuItem;
	@FXML
	private MockupChooser mockupChooser;
	@FXML
	private MockupPane mockupPane;
	@FXML
	private RecorderToolBar recorderToolBar;
	@FXML
	private ProgressBar recorderProgressBar;
	@FXML
	private RecorderTimeline recorderTimeline;

	private final ObjectProperty<Path> scenarioPath = new SimpleObjectProperty<Path>();
	private final BooleanProperty isDirty = new SimpleBooleanProperty();
	private final ActionEventFilter actionEventFilter = new ActionEventFilter();
	private final Timeline actionTimeline = new Timeline();
	private IActionTimelineBuilderStrategy actionTimelineBuilder;
	private IVideoCaptureFacade videoFacade;

	/**
	 * Reset controls to predefined state.
	 */
	public void reset() {
		setScenarioPath(null);
		actionTimeline.getKeyFrames().clear();
		recorderToolBar.reset();
		recorderProgressBar.setProgress(0);
		recorderTimeline.reset();
		mockupPane.reset();
		mockupChooser.reset();
		setDirty(false);
	}

	/**
	 * Create new empty scenario.
	 */
	@FXML
	public void handleNewScenario() {
		handleAskForSave();
		reset();

		final File projectFile = mediator.showScenarioSaveDialog("Neues Szenario erstellen");
		if (projectFile != null) {
			try {
				storeScenario(projectFile.toPath());
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Load scenario file.
	 */
	@FXML
	public void handleLoadScenario() {
		handleAskForSave();

		final File projectFile = mediator.showScenarioOpenDialog("Szenario aus Datei öffnen");
		if (projectFile != null) {
			try {
				loadScenario(projectFile.toPath());
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Save current scenario.
	 */
	@FXML
	public void handleSaveScenario() {
		final Path projectPath = getScenarioPath();
		if (projectPath != null) {
			try {
				storeScenario(projectPath);
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Ask the user to save the scenario if there are any changes.
	 */
	@FXML
	public void handleAskForSave() {
		if (isDirty()) {
			final ButtonType result = SimpleAlert.confirm("Möchten Sie die Änderungen am Szenario speichern?");
			if (result == ButtonType.OK) {
				handleSaveScenario();
			}
		}
	}

	/**
	 * Save current scenario file as...
	 */
	@FXML
	public void handleSaveAsScenario() {
		final File projectFile = mediator.showScenarioSaveDialog("Szenario speichern unter...");
		if (projectFile != null) {
			try {
				storeScenario(projectFile.toPath());
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Open project directory in default file browser.
	 */
	@FXML
	public void handleOpenProjectDirectory() {
		final Path scenarioPath = getScenarioPath();
		if (scenarioPath != null) {
			final Path projectDir = scenarioPath.getParent();
			try {
				FileBrowser.open(projectDir);
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Exit application.
	 */
	@FXML
	public void handleExit() {
		Platform.exit();
	}

	/**
	 * Open Scenebuilder for mockup creation.
	 */
	@FXML
	public void handleOpenSceneBuilder() {
		try {
			mediator.openSceneBuilder(null);
		} catch (IOException e) {
			SimpleAlert.exception(e);
		}
	}

	/**
	 * Reload current mockup in mockup pane.
	 */
	@FXML
	public void handleReloadMockup() {
		mockupPane.reload();
		recorderTimeline.refresh();
		mockupChooser.refresh();
	}

	/**
	 * Create mockup with background image.
	 */
	@FXML
	public void handleCreateMockupWithImage() {
		final File imageFile = mediator.showImageOpenDialog("Bild für Mockup auswählen");
		if (imageFile != null) {
			try {
				final Path mockupPath = ImageToMockupConverter.convert(imageFile.toPath());
				mediator.openSceneBuilder(mockupPath.toString());
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Toggle recording state.
	 */
	@FXML
	public void handleToggleRecording() {
		recorderToolBar.toggleRecording();
	}

	/**
	 * Toggle recording state.
	 */
	@FXML
	public void handleTogglePlaying() {
		recorderToolBar.togglePlaying();
	}

	/**
	 * Select next timeline item.
	 */
	@FXML
	public void handleNextItem() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.selectNextItem();
	}

	/**
	 * Select previous timeline item.
	 */
	@FXML
	public void handlePreviousItem() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.selectPreviousItem();
	}

	/**
	 * Jump to begin of the timeline.
	 */
	@FXML
	public void handleToBegin() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.scrollToBegin();
		recorderTimeline.selectFirstItem();
	}

	/**
	 * Jump to end of the timeline.
	 */
	@FXML
	public void handleToEnd() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.scrollToEnd();
		recorderTimeline.selectLastItem();
	}

	/**
	 * Resize mockup pane viewport.
	 */
	@FXML
	public void handleResizeViewport() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		final SizeDialog dialog = new SizeDialog();
		final Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
		result.ifPresent(pair -> {
			mockupPane.setViewportWidth(pair.getKey());
			mockupPane.setViewportHeight(pair.getValue());
			setDirty(true);
		});
	}

	/**
	 * Move active recorder timeline item one index to the right.
	 */
	@FXML
	public void handleMoveRightItem() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.moveRight(recorderTimeline.getActiveItem());
	}

	/**
	 * Move active recorder timeline item one index to the left.
	 */
	@FXML
	public void handleMoveLeftItem() {
		if (recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		recorderTimeline.moveLeft(recorderTimeline.getActiveItem());
	}

	/**
	 * Export scenario as text.
	 */
	@FXML
	public void handleTextExport() {
		if (recorderToolBar.isRecording() || recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		if (!recorderTimeline.getItems().isEmpty()) {
			final ScenarioTextDialog dialog = new ScenarioTextDialog();
			dialog.setResizable(true);
			dialog.setText(ScenarioToTextConverter.convert(recorderTimeline.getItems()));
			dialog.showAndWait();
		}
	}

	/**
	 * Export scenario as mockup images.
	 */
	@FXML
	public void handleImageExport() {
		if (recorderToolBar.isRecording() || recorderToolBar.isPlaying()) {
			throw new IllegalStateException("Pre-condition failed");
		}
		final Path scenarioPath = getScenarioPath();
		if (scenarioPath != null) {
			final Path exportDir = scenarioPath.getParent().resolve("mockup-export");
			try {
				if (!Files.exists(exportDir)) {
					Files.createDirectories(exportDir);
				}
				ScenarioToImageConverter.convert(recorderTimeline.getItems(), exportDir);
				FileBrowser.open(exportDir);
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}
	}

	/**
	 * Start video export.
	 */
	@FXML
	public void handleVideoExport() {
		recorderToolBar.startExporting();
	}

	/**
	 * Show about dialog.
	 */
	@FXML
	public void handleAboutDialog() {
		final AboutDialog dialog = new AboutDialog();
		dialog.showAndWait();
	}

	/*
	 * Load scenario from file and unserialize it.
	 */
	private void loadScenario(final Path file) throws IOException {
		// Unserialize project file.
		final IDataAccessObject<ScenarioModel> dao = new ScenarioDAO(file);
		final ScenarioModel scenarioModel = dao.load();

		// Reset controls.
		reset();

		// Restore viewport.
		mockupPane.setViewportWidth(scenarioModel.getViewportWidth());
		mockupPane.setViewportHeight(scenarioModel.getViewportHeight());

		// Restore mouse speed.
		recorderToolBar.setMouseSpeed(scenarioModel.getMouseSpeed());

		// Restore timeline.
		recorderTimeline.getItems().clear();
		for (final InteractionModel item : scenarioModel.getInteractions()) {
			final RecorderTimelineItem recorderItem = recorderTimeline.addItem(file.resolveSibling(item.getMockup()));
			recorderItem.getActions().setAll(item.getActions());
		}
		recorderTimeline.selectFirstItem();

		setScenarioPath(file);
		setDirty(false);
	}

	/*
	 * Serialize scenario and store it in given file.
	 */
	private void storeScenario(final Path file) throws IOException {
		final IDataAccessObject<ScenarioModel> dao = new ScenarioDAO(file);
		final ScenarioModel scenarioModel = new ScenarioModel();

		scenarioModel.setViewportWidth(mockupPane.getViewportWidth());
		scenarioModel.setViewportHeight(mockupPane.getViewportHeight());
		scenarioModel.setMouseSpeed(recorderToolBar.getMouseSpeed());

		for (final RecorderTimelineItem item : recorderTimeline.getItems()) {
			final String mockup = file.getParent().relativize(item.getMockupPath()).toString();
			scenarioModel.getInteractions().add(new InteractionModel(mockup, item.getActions()));
		}

		dao.store(scenarioModel);
		setScenarioPath(file);
		setDirty(false);
	}

	@FXML
	private void initialize() {
		// Load mockups from project dir.
		scenarioPath.addListener((obsVal, oldVal, newVal) -> {
			if (newVal == null) {
				mockupChooser.setRootDir(null);
			} else {
				mockupChooser.setRootDir(newVal.getParent());
				updateProjectScenariosMenu();
			}
		});

		initInteraction();
		initMenuBar();
		initMockupChooser();
		initMockupPane();
		initRecorderTimeline();
		initRecorderToolBar();
	}

	private void initInteraction() {
		actionTimelineBuilder = new DefaultActionTimelineBuilder(mockupPane, recorderTimeline, recorderProgressBar.progressProperty(), recorderToolBar.mouseSpeedProperty());
		actionEventFilter.activeTimelineItemProperty().bind(recorderTimeline.activeItemProperty());
		actionTimeline.setOnFinished(event -> recorderToolBar.stopPlaying());
	}

	private void initMenuBar() {
		openProjectDirectoryMenuItem.disableProperty().bind(Bindings.isNull(scenarioPath));
		projectScenariosMenu.disableProperty().bind(Bindings.isNull(scenarioPath));
		saveProjectMenuItem.disableProperty().bind(Bindings.isNull(scenarioPath));
		saveAsProjectMenuItem.disableProperty().bind(Bindings.isNull(scenarioPath));
		projectScenariosMenu.setOnShowing(event -> updateProjectScenariosMenu());

		togglePlayingMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isRecordingProperty()));
		toggleRecordingMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isPlayingProperty()));
		moveRightItemMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isPlayingProperty()));
		moveLeftItemMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isPlayingProperty()));
		nextItemMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isPlayingProperty()));
		previousItemMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.isPlayingProperty()));
		toBeginMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.getToBeginButton().disableProperty()));
		toEndMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.getToEndButton().disableProperty()));
		exportTextMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.getExportButton().disableProperty()));
		exportImageMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.getExportButton().disableProperty()));
		exportVideoMenuItem.disableProperty().bind(Bindings.or(recorderToolBar.disableProperty(), recorderToolBar.getExportButton().disableProperty()));
	}

	private void initMockupChooser() {
		mockupChooser.getContent().mouseTransparentProperty().bind(recorderToolBar.isPlayingProperty());

		final ContextMenu menu = new ContextMenu();
		final MenuItem refresh = new MenuItem("Mockups neu laden");
		refresh.setOnAction(event -> {
			mockupPane.reload();
			mockupChooser.refresh();
			recorderTimeline.refresh();
		});
		menu.getItems().add(refresh);
		final MenuItem edit = new MenuItem("Mockup mit SceneBuilder bearbeiten");
		edit.setOnAction(event -> {
			final MockupChooserItem item = mockupChooser.getActiveItem();
			if (item != null) {
				try {
					mediator.openSceneBuilder(item.getMockupPath().toString());
				} catch (IOException e) {
					SimpleAlert.exception(e);
				}
			}
		});
		menu.getItems().add(edit);
		mockupChooser.setContextMenu(menu);

		mockupChooser.setItemFactory((chooser, mockup) -> {
			final MockupChooserItem mockupItem = new MockupChooserItem(chooser, mockup);
			mockupItem.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
					// Display chosen mockup and reset active recorder timline item.
					recorderTimeline.setActiveItem(null);
					mockupPane.setMockupPath(mockupItem.getMockupPath());
				}
			});
			return mockupItem;
		});
	}

	private void initMockupPane() {
		mockupPane.getCanvas().visibleProperty().bind(recorderToolBar.isPlayingProperty());
	}

	private void initRecorderTimeline() {
		recorderTimeline.getContent().mouseTransparentProperty().bind(recorderToolBar.isPlayingProperty());

		// Always display the active item.
		recorderTimeline.activeItemProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal != null) {
				mockupPane.setMockupPath(newVal.getMockupPath());
			}
		});

		// Mark scenario as dirty if the list of items changed.
		recorderTimeline.getItems().addListener((Observable obsVal) -> setDirty(true));

		recorderTimeline.setItemFactory((timeline, mockup) -> {
			// Context menu.
			final RecorderTimelineItem timelineItem = new RecorderTimelineItem(timeline, mockup);
			timelineItem.getActions().addListener((Observable obsVal) -> setDirty(true));
			final MenuItem clear = new MenuItem("Clear Actions");
			clear.setOnAction(event -> timelineItem.getActions().clear());
			timelineItem.getContextMenu().getItems().add(clear);
			final MenuItem remove = new MenuItem("Remove Item");
			remove.setOnAction(event -> {
				recorderTimeline.getItems().remove(timelineItem);
				if (timelineItem.equals(recorderTimeline.getActiveItem())) {
					// We remove the active item.
					recorderTimeline.setActiveItem(null);
				}
			});
			timelineItem.getContextMenu().getItems().add(remove);
			return timelineItem;
		});

		// Add active mockup from chooser to timeline.
		recorderTimeline.getEndPane().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				final MockupChooserItem mockupItem = mockupChooser.getActiveItem();
				if (mockupItem != null && event.getButton() == MouseButton.PRIMARY) {
					recorderTimeline.setActiveItem(recorderTimeline.addItem(mockupItem.getMockupPath()));
				}
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		});

		// Tooltip.
		final Tooltip timelineEndPaneTooltip = new Tooltip("Ziehen Sie einen Mockup hier her, um ihn der Timeline hinzuzufügen.\n"
				+ "Alternativ kann der ausgewählte Mockup mit einem Klick hinzugefügt werden.");
		recorderTimeline.getEndPane().addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
			timelineEndPaneTooltip.show(recorderTimeline.getEndPane(), event.getScreenX() + 10, event.getScreenY() + 10));
		recorderTimeline.getEndPane().addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
			timelineEndPaneTooltip.setX(event.getScreenX() + 10);
			timelineEndPaneTooltip.setY(event.getScreenY() + 10);
		});
		recorderTimeline.getEndPane().addEventHandler(MouseEvent.MOUSE_EXITED, event -> timelineEndPaneTooltip.hide());
	}

	private void initRecorderToolBar() {
		recorderToolBar.disableProperty().bind(Bindings.isNull(recorderTimeline.activeItemProperty()));
		recorderToolBar.mouseSpeedProperty().addListener((obsVal, oldVal, newVal) -> setDirty(true));

		// Toggle recording.
		recorderToolBar.isRecordingProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal) {
				mockupPane.addEventFilter(InputEvent.ANY, actionEventFilter);
			} else {
				mockupPane.removeEventFilter(InputEvent.ANY, actionEventFilter);
			}
		});

		// Toggle playback.
		recorderToolBar.isPlayingProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal) {
				mockupPane.setMouseX(0);
				mockupPane.setMouseY(0);
				mockupPane.setMouseRadius(10);
				mockupPane.setMouseColor(Color.RED);
				mockupPane.reload();
				recorderProgressBar.setProgress(0);
				actionTimeline.getKeyFrames().setAll(actionTimelineBuilder.build());
				actionTimeline.play();
			} else {
				actionTimeline.stop();
			}
		});

		// Toggle video export.
		recorderToolBar.isExportingProperty().addListener((obsVal, oldVal, newVal) -> {
			try {
				if (newVal) {
					final String scenarioFileName = getScenarioPath().getFileName().toString();
					final int lastDotPosition = scenarioFileName.lastIndexOf('.');
					final String scenarioName = lastDotPosition == -1 ? scenarioFileName : scenarioFileName.substring(0, lastDotPosition);
					final String dateTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.GERMANY).format(new Date());
					final Path videoPath = getScenarioPath().resolveSibling(scenarioName + "_" + dateTime + ".mp4");
					videoFacade = new JavaFXCapture(mockupPane.getStackPane(), videoPath);
					videoFacade.start();
				} else if (videoFacade != null) {
					videoFacade.stop();
				}
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				SimpleAlert.exception(e);
			}
		});

		recorderToolBar.getToBeginButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleToBegin());
		recorderToolBar.getToEndButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleToEnd());
		recorderToolBar.getResizeButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleResizeViewport());
	}

	private void updateProjectScenariosMenu() {
		projectScenariosMenu.getItems().clear();

		final Path scenario = scenarioPath.get();
		if (scenario != null) {
			final Path projectDir = scenario.getParent();
			try (final DirectoryStream<Path> stream = Files.newDirectoryStream(projectDir)) {
				for (final Path file : stream) {
					if (file.toString().endsWith(".szenario")) {
						final MenuItem item = new MenuItem(file.getFileName().toString());
						item.setOnAction(event -> {
							handleAskForSave();

							try {
								loadScenario(file);
							} catch (IOException e) {
								SimpleAlert.exception(e);
							}
						});
						projectScenariosMenu.getItems().add(item);
					}
				}
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		}

		if (projectScenariosMenu.getItems().isEmpty()) {
			final MenuItem item = new MenuItem("Keine Szenarios gefunden");
			item.setDisable(true);
			projectScenariosMenu.getItems().add(item);
		}
	}

	public Path getScenarioPath() {
		return scenarioPath.get();
	}

	public void setScenarioPath(final Path path) {
		scenarioPath.set(path);
	}

	public ObjectProperty<Path> scenarioPathProperty() {
		return scenarioPath;
	}

	public boolean isDirty() {
		return isDirty.get();
	}

	public void setDirty(final boolean dirty) {
		isDirty.set(dirty);
	}

	public BooleanProperty isDirtyProperty() {
		return isDirty;
	}
}
