package application.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import application.IMediator;
import application.controllers.AbstractController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Abstract window.
 * 
 * @author Lennart Glauer
 *
 */
public abstract class AbstractWindow<T extends AbstractController> extends Stage {
	/*
	 * Application mediator.
	 */
	protected final IMediator mediator;

	/*
	 * Window controller.
	 */
	protected T controller;

	/**
	 * Initialize window.
	 * 
	 * @param mediator Application mediator
	 */
	public AbstractWindow(final IMediator mediator) {
		super();

		this.mediator = mediator;
	}

	/**
	 * Get fxml controller.
	 * 
	 * @return 
	 */
	public T getController() {
		return controller;
	}

	/*
	 * Load scene from fxml.
	 * 
	 * @param path Path to mockup fxml file
	 * @throws IOException 
	 */
	protected void loadFXML(final String fxml) throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

		// Register a controller factory to pass the mediator instance to all (nested) controllers.
		loader.setControllerFactory(controllerClass -> {
			try {
				final AbstractController controller = (AbstractController) controllerClass.getDeclaredConstructor().newInstance();
				controller.setMediator(mediator);
				return controller;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e); // NOPMD
			}
		});

		// Load fxml and controller.
		final Parent root = loader.load();
		controller = loader.getController();

		// Create scene
		setScene(new Scene(root));
	}
}
