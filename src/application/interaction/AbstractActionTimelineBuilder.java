package application.interaction;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * 
 * @author Lennart Glauer
 * @notice Builder Design Pattern
 *
 */
public abstract class AbstractActionTimelineBuilder implements IActionTimelineBuilderStrategy {
	private final List<KeyFrame> frames = new ArrayList<KeyFrame>();
	private Duration duration = Duration.ZERO;

	/**
	 * Build action timeline keyframes.
	 */
	@Override
	public abstract List<KeyFrame> build();

	/**
	 * Reset state.
	 * 
	 * @return
	 */
	public AbstractActionTimelineBuilder reset() {
		frames.clear();
		duration = Duration.ZERO;
		return this;
	}

	/**
	 * Add callback frame.
	 * 
	 * @param delay
	 * @param handler
	 * @return
	 */
	public AbstractActionTimelineBuilder addFrame(final double delay, final EventHandler<ActionEvent> handler) {
		frames.add(new KeyFrame(duration, handler));
		addDelay(delay);
		return this;
	}

	/**
	 * Add frame for value interpolation.
	 * 
	 * @param delay
	 * @param values
	 * @return
	 */
	public AbstractActionTimelineBuilder addFrame(final double delay, final KeyValue... values) {
		frames.add(new KeyFrame(duration, values));
		addDelay(delay);
		return this;
	}

	/**
	 * Add delay in milliseconds.
	 * 
	 * @param delay
	 * @return
	 */
	public AbstractActionTimelineBuilder addDelay(final double delay) {
		duration = duration.add(Duration.millis(delay));
		frames.add(new KeyFrame(duration));
		return this;
	}

	/**
	 * Get result.
	 * 
	 * @return
	 */
	public List<KeyFrame> getFrames() {
		return frames;
	}
}
