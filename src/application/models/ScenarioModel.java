package application.models;

import java.util.ArrayList;
import java.util.List;

public class ScenarioModel {
	/*
	 * Mockup Pane viewport width.
	 */
	private double viewportWidth;

	/*
	 * Mockup Pane viewport height.
	 */
	private double viewportHeight;

	/*
	 * Mouse speed factor. 
	 */
	private double mouseSpeed;

	/*
	 * Timeline items.
	 */
	private final List<InteractionModel> interactions = new ArrayList<InteractionModel>();

	public ScenarioModel() { // NOPMD
		// Needed for json object binding.
	}

	public List<InteractionModel> getInteractions() {
		return interactions;
	}

	public double getMouseSpeed() {
		return mouseSpeed;
	}

	public void setMouseSpeed(final double mouseSpeed) {
		this.mouseSpeed = mouseSpeed;
	}

	public double getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportWidth(final double viewportWidth) {
		this.viewportWidth = viewportWidth;
	}

	public double getViewportHeight() {
		return viewportHeight;
	}

	public void setViewportHeight(final double viewportHeight) {
		this.viewportHeight = viewportHeight;
	}
}
