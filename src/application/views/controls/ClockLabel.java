package application.views.controls;

import java.io.IOException;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class ClockLabel extends Label {
	private final LongProperty startTime = new SimpleLongProperty();
	private final Timeline timeline = new Timeline(
			new KeyFrame(Duration.seconds(0), event -> {
				final long diff = getElapsedMillis() / 1000;
				final long hours = diff / (60*60);
				final long minutes = (diff / 60) % 60;
				final long seconds = diff % 60;
				setText(String.format(Locale.GERMANY, "%02d:%02d:%02d", hours, minutes, seconds));
			}),
			new KeyFrame(Duration.seconds(1)));

	/**
	 * Create clock label.
	 */
	public ClockLabel() {
		super();

		timeline.setCycleCount(Animation.INDEFINITE);

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("ClockLabel.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	/**
	 * Start clock animation.
	 */
	public void start() {
		if (isStarted()) {
			throw new IllegalStateException("Already started.");
		}

		setStartTime(System.currentTimeMillis());
		timeline.play();
	}

	/**
	 * Stop clock animation.
	 */
	public void stop() {
		timeline.stop();
	}

	/**
	 * Return true if the clock is started.
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return timeline.getStatus() == Status.RUNNING;
	}

	/**
	 * Get elapsed milliseconds.
	 * 
	 * @return
	 */
	public long getElapsedMillis() {
		return isStarted() ? (System.currentTimeMillis() - getStartTime()) : 0;
	}

	/**
	 * Reset control.
	 */
	public void reset() {
		setStartTime(0);
		setText("00:00:00");
	}

	public long getStartTime() {
		return startTime.get();
	}

	protected void setStartTime(final long time) {
		startTime.set(time);
	}

	public ReadOnlyLongProperty startTimeProperty() {
		return startTime;
	}
}
