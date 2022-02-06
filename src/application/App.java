package application;

import java.io.IOException;

import application.models.ConfigDAO;
import application.models.ConfigModel;
import application.models.IDataAccessObject;
import application.utils.SimpleAlert;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application class.
 * 
 * @author Lennart Glauer
 *
 */
public class App extends Application {
	/*
	 * Application config.
	 */
	private final IDataAccessObject<ConfigModel> config = new ConfigDAO();

	/*
	 * Application mediator.
	 */
	private IMediator mediator; // NOPMD

	/*
	 * Javafx primary stage.
	 */
	private Stage primaryStage;

	/**
	 * Launch javafx application.
	 * 
	 * @param args CLI arguments
	 */
	public static void main(final String... args) {
		launch(args);
	}

	/**
	 * Initialize javafx application and show recorder window.
	 */
	@Override
	public void start(final Stage primaryStage) {
		this.primaryStage = primaryStage;

		try {
			config.load();
		} catch (IOException e) {
			SimpleAlert.exception(e);
		}

		mediator = new Mediator(this);
		mediator.showRecorderWindow();
	}

	public IDataAccessObject<ConfigModel> getConfig() {
		return config;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
