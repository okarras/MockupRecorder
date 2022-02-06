package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import application.models.ConfigModel;
import application.views.RecorderWindow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.WindowEvent;

/**
 * This class serves as a mediator between the javafx application, stages and controllers.
 * 
 * @author Lennart Glauer
 * @notice Mediator Design Pattern
 * 
 */
public class Mediator implements IMediator {
	private final App app;
	private final RecorderWindow recorderWindow;

	/**
	 * Initialize mediator / colleagues.
	 * 
	 * @param app
	 */
	public Mediator(final App app) {
		this.app = app;

		recorderWindow = new RecorderWindow(this);
		recorderWindow.initOwner(app.getPrimaryStage());
	}

	//
	// --------------- Recorder Window ---------------
	//

	/**
	 * Show recorder window.
	 */
	public void showRecorderWindow() {
		recorderWindow.getController().reset();
		recorderWindow.show();
	}

	/**
	 * Called when there is an external request to close this Window.
	 * The installed event handler can prevent window closing by consuming the received event.
	 * 
	 * @param event
	 */
	public void onCloseRecorderWindow(final WindowEvent event) {
		recorderWindow.getController().handleAskForSave();
	}

	/**
	 * Show scenario open dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	public File showScenarioOpenDialog(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("*.szenario", "*.szenario"),
				new ExtensionFilter("*.*", "*.*"));
		return fileChooser.showOpenDialog(recorderWindow);
	}

	/**
	 * Show scenario save dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	public File showScenarioSaveDialog(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("*.szenario", "*.szenario"),
				new ExtensionFilter("*.*", "*.*"));
		return fileChooser.showSaveDialog(recorderWindow);
	}

	/**
	 * Show image open dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	public File showImageOpenDialog(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("*.png, *.jpeg, *.gif, *.bmp", "*.png", "*.jpeg", "*.gif", "*.bmp"));
		return fileChooser.showOpenDialog(recorderWindow);
	}

	//
	// --------------- Other ---------------
	//

	/**
	 * Open SceneBuilder.
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public void openSceneBuilder(final String args) throws IOException {
		final ConfigModel configModel = app.getConfig().load();
		Path sceneBuilderPath = configModel.getSceneBuilderPath();
		if (sceneBuilderPath == null) {
			// Lazy initialization.
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("SceneBuilder auswählen...");
			fileChooser.getExtensionFilters().setAll(
					new ExtensionFilter("SceneBuilder.exe", "*.exe"),
					new ExtensionFilter("SceneBuilder.jar", "*.jar"));
			final File selectedFile = fileChooser.showOpenDialog(null);
			if (selectedFile == null) {
				// Abort.
				return;
			}
			sceneBuilderPath = selectedFile.toPath();
			configModel.setSceneBuilderPath(sceneBuilderPath);
			app.getConfig().store(configModel);
		}

		final String path = sceneBuilderPath.toString();
		final String argsString = (args == null) ? "" : args;
		if (path.endsWith(".jar")) {
			final ProcessBuilder builder = new ProcessBuilder("java", "-jar", path, argsString);
			builder.start();
		} else {
			final ProcessBuilder builder = new ProcessBuilder(path, argsString);
			builder.start();
		}
	}
}
