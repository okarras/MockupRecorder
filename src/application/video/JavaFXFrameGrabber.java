package application.video;

import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;

/**
 * Grab javacv frames from a javafx node.
 * 
 * @author Lennart Glauer
 *
 */
public class JavaFXFrameGrabber extends FrameGrabber {
	/*
	 * JavaFX root node.
	 */
	private final Node root;

	/*
	 * Reusable javafx image.
	 */
	private WritableImage fxImage;

	/*
	 * Reusable buffered image.
	 */
	private BufferedImage bufImage;

	/*
	 * Convert swing BufferedImage to javacv frame.
	 */
	private final Java2DFrameConverter converter = new Java2DFrameConverter();

	/**
	 * Initialize javafx frame grabber.
	 * 
	 * @param root JavaFX root node
	 */
	public JavaFXFrameGrabber(final Node root) {
		super();

		this.root = root;

		imageWidth = (int) root.getBoundsInLocal().getWidth();
		imageHeight = (int) root.getBoundsInLocal().getHeight();

		// Ensure bounds are even integers.
		if (imageWidth % 2 != 0) {
			--imageWidth;
		}
		if (imageHeight % 2 != 0) {
			--imageHeight;
		}

		// Preallocate image buffers for best performance.
		fxImage = new WritableImage(imageWidth, imageHeight);
		bufImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Grab next frame.
	 * 
	 * @notice Must be called from FX application thread.
	 */
	@Override
	public Frame grab() {
		fxImage = root.snapshot(null, fxImage);
		bufImage = SwingFXUtils.fromFXImage(fxImage, bufImage);
		return converter.convert(bufImage);
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void start() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void stop() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void release() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void trigger() {
		throw new UnsupportedOperationException();
	}
}
