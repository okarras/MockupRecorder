package application.models;

import java.nio.file.Path;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class ConfigModel {
	/*
	 * Path to scene builder.
	 */
	private Path sceneBuilderPath;

	public ConfigModel() { // NOPMD
		// Needed for json object binding.
	}

	public Path getSceneBuilderPath() {
		return sceneBuilderPath;
	}

	public void setSceneBuilderPath(final Path sceneBuilderPath) {
		this.sceneBuilderPath = sceneBuilderPath;
	}
}
