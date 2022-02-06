package application.video;

import java.nio.file.Path;

import org.bytedeco.javacpp.avutil;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class JavaFXCapture implements IVideoCaptureFacade {
	/*
	 * Frames per second.
	 */
	private static final int FPS = 30;

	/*
	 * Time in milliseconds between frames.
	 */
	private static final int PERIOD = 1000 / FPS;

	/*
	 * Frame timer.
	 */
	private final Timeline frameTimer = new Timeline();

	/*
	 * Frame grabber.
	 */
	private final JavaFXFrameGrabber grabber;

	/*
	 * Frame recorder.
	 */
	private final JavaFXFrameRecorder recorder;

	/**
	 * Initialize video capturing.
	 * 
	 * @param root
	 * @param video
	 */
	public JavaFXCapture(final Node root, final Path video) {
		grabber = new JavaFXFrameGrabber(root);
		recorder = new JavaFXFrameRecorder(video, grabber.getImageWidth(), grabber.getImageHeight());
		recorder.setFrameRate(FPS);
		frameTimer.setCycleCount(Animation.INDEFINITE);
	}

	/**
	 * Start video capturing.
	 * 
	 * @throws org.bytedeco.javacv.FrameRecorder.Exception
	 */
	@Override
	public void start() throws org.bytedeco.javacv.FrameRecorder.Exception {
		recorder.start();
		startFrameTimer();
	}

	/**
	 * Stop video capturing.
	 * 
	 * @throws org.bytedeco.javacv.FrameRecorder.Exception
	 */
	@Override
	public void stop() throws org.bytedeco.javacv.FrameRecorder.Exception {
		stopFrameTimer();
		recorder.stop();
	}

	/*
	 * Start frame timer.
	 */
	private void startFrameTimer() {
		frameTimer.getKeyFrames().setAll(new KeyFrame(Duration.millis(0), event -> {
					try {
						if (frameTimer.getStatus() == Status.RUNNING) {
							recorder.record(grabber.grab(), avutil.AV_PIX_FMT_ARGB);
						}
					} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
						// Ignore.
						e.printStackTrace();
					}
				}),
				new KeyFrame(Duration.millis(PERIOD)));
		frameTimer.play();
	}

	/*
	 * Stop frame timer.
	 */
	private void stopFrameTimer() {
		if (frameTimer != null) {
			frameTimer.stop();
		}
	}
}
