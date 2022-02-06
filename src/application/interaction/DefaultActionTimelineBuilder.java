package application.interaction;

import java.util.List;

import application.views.controls.MockupPane;
import application.views.controls.RecorderTimeline;
import application.views.controls.RecorderTimelineItem;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;

/**
 * 
 * @author Lennart Glauer
 * @notice Strategy Design Pattern
 *
 */
public class DefaultActionTimelineBuilder extends AbstractActionTimelineBuilder {
	private final MockupPane mockupPane;
	private final RecorderTimeline recorderTimeline;
	private final DoubleProperty timelineProgress;
	private final DoubleProperty mouseSpeed;

	public DefaultActionTimelineBuilder(final MockupPane mockupPane, final RecorderTimeline recorderTimeline, final DoubleProperty timelineProgress, final DoubleProperty mouseSpeed) {
		super();

		this.mockupPane = mockupPane;
		this.recorderTimeline = recorderTimeline;
		this.timelineProgress = timelineProgress;
		this.mouseSpeed = mouseSpeed;
	}

	/**
	 * Build action timeline keyframes.
	 */
	@Override
	public List<KeyFrame> build() {
		// Reset keyframes.
		reset();

		// Get start index.
		final ObservableList<RecorderTimelineItem> items = recorderTimeline.getItems();
		final int index = items.indexOf(recorderTimeline.getActiveItem());
		if (index == -1) {
			// Empty list.
			return getFrames();
		}

		// Build action keyframes.
		final int length = recorderTimeline.getItems().size();
		final double speedFactor = mouseSpeed.get();

		for (int i = index; i < length; ++i) {
			final RecorderTimelineItem current = recorderTimeline.getItems().get(i);
			addFrame(1000 / speedFactor, event -> recorderTimeline.setActiveItem(current));

			for (final IAction command : current.getActions()) {
				final double duration = command.getDuration();
				addFrame(duration / speedFactor, event -> {
					command.setDuration(duration / speedFactor);
					command.execute(mockupPane);
					command.setDuration(duration);
				});
			}

			addDelay(300 / speedFactor);
		}

		addFrame(0, new KeyValue(timelineProgress, 1));
		return getFrames();
	}
}
