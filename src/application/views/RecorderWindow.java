package application.views;

import java.io.FileInputStream;
import java.io.IOException;

import application.IMediator;
import application.controllers.RecorderWindowController;
import javafx.scene.image.Image;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class RecorderWindow extends AbstractWindow<RecorderWindowController> {
	/*
	 * Window title.
	 */
	private static final String WINDOW_TITLE = "MockupRecorder";

	/*
	 * Window width.
	 */
	private static final int WINDOW_WIDTH = 1024;

	/*
	 * Window height.
	 */
	private static final int WINDOW_HEIGHT = 768;

	/**
	 * Initialize window.
	 * 
	 * @param mediator Application mediator
	 */
	public RecorderWindow(final IMediator mediator) {
		super(mediator);

		try {	// Sets the icon to the  main stage
        	FileInputStream logoInput = new FileInputStream("src/application/images/logo.png");
			this.getIcons().add(new Image(logoInput));
        } catch(IOException e) {
			e.printStackTrace();
		}

		setTitle(WINDOW_TITLE);
		setWidth(WINDOW_WIDTH);
		setHeight(WINDOW_HEIGHT);
		setOnCloseRequest(event -> mediator.onCloseRecorderWindow(event));

		try {
			loadFXML("RecorderWindow.fxml");
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}
}
