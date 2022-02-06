package application.models;

import java.util.List;

import application.interaction.IAction;

public class InteractionModel {
	/*
	 * Relative path from project dir to mockup file.
	 */
	private String mockup;

	/*
	 * List of captured interactions.
	 */
	private List<IAction> actions;

	protected InteractionModel() {
		// Needed for json object binding.
	}

	public InteractionModel(final String mockup, final List<IAction> actions) {
		this.mockup = mockup;
		this.actions = actions;
	}

	public String getMockup() {
		return mockup;
	}

	public List<IAction> getActions() {
		return actions;
	}
}
