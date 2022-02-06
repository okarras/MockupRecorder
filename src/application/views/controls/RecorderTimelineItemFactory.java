package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 
 * @author Lennart Glauer
 * @notice Factory Design Pattern
 *
 */
public interface RecorderTimelineItemFactory {
	/**
	 * Create new recorder timeline item.
	 * 
	 * @param timeline
	 * @param mockupPath
	 * @return
	 * @throws IOException
	 */
	RecorderTimelineItem createItem(final RecorderTimeline timeline, final Path mockupPath) throws IOException;
}
