package application.interaction;

import java.util.List;

import javafx.animation.KeyFrame;

/**
 * 
 * @author Lennart Glauer
 * @notice Strategy Design Pattern
 *
 */
public interface IActionTimelineBuilderStrategy {
	/**
	 * Build action timeline keyframes.
	 * 
	 * @return
	 */
	List<KeyFrame> build();
}
