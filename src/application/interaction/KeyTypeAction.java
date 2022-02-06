package application.interaction;

import application.views.controls.MockupPane;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyTypeAction implements IAction {
	private String target;
	private String targetType;
	private double duration;
	private String character;

	public KeyTypeAction() {
		// Needed for json object binding.
	}

	public KeyTypeAction(final String target, final String targetType, final String character) {
		this.target = target;
		this.targetType = targetType;
		this.character = character;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node input = mockupPane.lookup(target);
		input.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, character, null, KeyCode.UNDEFINED, false, false, false, false));
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public String getTargetType() {
		return targetType;
	}

	public void setDuration(final double duration) {
		this.duration = duration;
	}

	public String getCharacter() {
		return character;
	}
}
