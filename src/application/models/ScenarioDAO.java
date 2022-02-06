package application.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class ScenarioDAO implements IDataAccessObject<ScenarioModel> {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Path scenarioPath;

	public ScenarioDAO(final Path scenarioPath) {
		this.scenarioPath = scenarioPath;
	}

	/**
	 * Read config file.
	 * 
	 * @throws IOException
	 */
	public ScenarioModel load() throws IOException {
		try {
			return MAPPER.readerFor(ScenarioModel.class)
					.readValue(Files.newInputStream(scenarioPath));
		} catch (NoSuchFileException e) {
			return new ScenarioModel();
		}
	}

	/**
	 * Write config file.
	 * 
	 * @throws IOException
	 */
	public void store(final ScenarioModel model) throws IOException {
		MAPPER.writerFor(ScenarioModel.class)
				.withDefaultPrettyPrinter()
				.writeValue(Files.newOutputStream(scenarioPath), model);
	}
}
