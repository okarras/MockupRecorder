package application.interaction;

import application.views.controls.MockupPane;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyReleaseAction implements IAction {
	private String target;
	private String targetType;
	private double duration;
	private String text;
	private KeyCode keyCode;

	public KeyReleaseAction() {
		// Needed for json object binding.
	}

	public KeyReleaseAction(final String target, final String targetType, final String text, final KeyCode keyCode) {
		this.target = target;
		this.targetType = targetType;
		this.text = text;
		this.keyCode = keyCode;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node input = mockupPane.lookup(target);
		input.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, null, text, keyCode, false, false, false, false));
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

	public String getText() {
		return text;
	}

	public KeyCode getKeyCode() {
		return keyCode;
	}
}
