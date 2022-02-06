package application.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import application.views.controls.RecorderTimelineItem;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;

/**
 * 
 * @author Lennart Glauer
 *
 */
public final class ScenarioToImageConverter {
	private ScenarioToImageConverter() {
		// Utility class.
	}

	/**
	 * Convert given timeline items to .png images and store them in exportFolder.
	 * 
	 * @param timelineItems
	 * @param exportFolder
	 * @throws IOException
	 */
	public static void convert(final List<RecorderTimelineItem> timelineItems, final Path exportFolder) throws IOException {
		int i = 1;

		for (final RecorderTimelineItem item : timelineItems) {
			final FXMLLoader previewLoader = new FXMLLoader(item.getMockupPath().toUri().toURL());
			final Scene scene = new Scene(previewLoader.load());
			final WritableImage mockupImage = scene.getRoot().snapshot(null, null);

			final Path outPath = exportFolder.resolve(String.format(Locale.GERMANY, "%02d-%s.png", i, item.getMockupPath().getFileName()));
			ImageIO.write(SwingFXUtils.fromFXImage(mockupImage, null), "png", outPath.toFile());

			++i;
		}
	}
}
