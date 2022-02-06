package application.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class ConfigDAO implements IDataAccessObject<ConfigModel> {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".mockup-recorder");
	private static final Path CONFIG_PATH = CONFIG_DIR.resolve("config.json");

	/**
	 * Read config file.
	 * 
	 * @throws IOException
	 */
	public ConfigModel load() throws IOException {
		try {
			return MAPPER.readerFor(ConfigModel.class)
					.readValue(Files.newInputStream(CONFIG_PATH));
		} catch (NoSuchFileException e) {
			return new ConfigModel();
		}
	}

	/**
	 * Write config file.
	 * 
	 * @throws IOException
	 */
	public void store(final ConfigModel model) throws IOException {
		if (!Files.isDirectory(CONFIG_DIR)) {
			Files.createDirectory(CONFIG_DIR);
		}

		MAPPER.writerFor(ConfigModel.class)
				.withDefaultPrettyPrinter()
				.writeValue(Files.newOutputStream(CONFIG_PATH), model);
	}
}
