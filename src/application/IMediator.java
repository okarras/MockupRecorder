package application;

import java.io.File;
import java.io.IOException;

import javafx.stage.WindowEvent;

public interface IMediator {
	/**
	 * Show recorder window.
	 */
	void showRecorderWindow();

	/**
	 * Called when there is an external request to close this Window.
	 * The installed event handler can prevent window closing by consuming the received event.
	 * 
	 * @param event
	 */
	void onCloseRecorderWindow(WindowEvent event);

	/**
	 * Show scenario open dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	File showScenarioOpenDialog(String title);

	/**
	 * Show scenario save dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	File showScenarioSaveDialog(String title);

	/**
	 * Show image open dialog.
	 * 
	 * @param title Window title
	 * @return
	 */
	File showImageOpenDialog(String title);

	/**
	 * Open SceneBuilder.
	 * 
	 * @param args
	 * @throws IOException 
	 */
	void openSceneBuilder(String args) throws IOException;
}
