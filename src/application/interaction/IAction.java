package application.interaction;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import application.views.controls.MockupPane;

/**
 * 
 * @author Lennart Glauer
 * @notice Command Design Pattern
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface IAction {
	/**
	 * Get css selector for target node.
	 * 
	 * @return
	 */
	String getTarget();

	/**
	 * Get target node type.
	 * 
	 * @return
	 */
	String getTargetType();

	/**
	 * Get duration in milliseconds.
	 * 
	 * @return
	 */
	double getDuration();

	/**
	 * Set duration in milliseconds.
	 * 
	 * @return
	 */
	void setDuration(double duration);

	/**
	 * Execute action command.
	 * 
	 * @param mockupPane
	 */
	void execute(final MockupPane mockupPane);
}
