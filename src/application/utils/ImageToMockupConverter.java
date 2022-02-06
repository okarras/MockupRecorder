package application.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javafx.scene.image.Image;

/**
 * 
 * @author Lennart Glauer
 *
 */
public final class ImageToMockupConverter {
	private final static String MOCKUP_TEMPLATE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<?import javafx.scene.image.Image?>\n"
			+ "<?import javafx.scene.image.ImageView?>\n"
			+ "<?import javafx.scene.layout.Pane?>\n"
			+ "<?import javafx.scene.layout.StackPane?>\n"
			+ "<StackPane xmlns=\"http://javafx.com/javafx/8\" xmlns:fx=\"http://javafx.com/fxml/1\">\n"
			    + "<children>\n"
				    + "<ImageView fitHeight=\"%.2f\" fitWidth=\"%.2f\">\n"
					    + "<image>\n"
					    	+ "<Image url=\"@%s\" />\n"
					    + "</image>\n"
				    + "</ImageView>\n"
					+ "<Pane />\n"
			    + "</children>\n"
		    + "</StackPane>\n";

	private ImageToMockupConverter() {
		// Utility class.
	}

	/**
	 * Convert given image to fxml mockup using a default template.
	 * 
	 * @param imagePath
	 * @return
	 * @throws IOException
	 */
	public static Path convert(final Path imagePath) throws IOException {
		final Image image = new Image(imagePath.toUri().toString(), false);
		final Path mockupPath = imagePath.resolveSibling(imagePath.getFileName() + ".fxml");
		final String content = String.format(Locale.US, MOCKUP_TEMPLATE, image.getHeight(), image.getWidth(), imagePath.getFileName());
		Files.write(mockupPath, content.getBytes());
		return mockupPath;
	}
}
