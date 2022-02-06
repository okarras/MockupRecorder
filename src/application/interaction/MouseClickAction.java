package application.interaction;

import application.views.controls.MockupPane;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseClickAction implements IAction {
	private String target;
	private String targetType;
	private double duration;
	private MouseButton mouseButton;
	private int clickCount;

	public MouseClickAction() {
		// Needed for json object binding.
	}

	public MouseClickAction(final String target, final String targetType, final MouseButton mouseButton, final int clickCount) {
		this.target = target;
		this.targetType = targetType;
		this.mouseButton = mouseButton;
		this.clickCount = clickCount;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node node = mockupPane.lookup(target);
		node.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, mouseButton, clickCount, false, false, false, false, false, false, false, false, false, false, null));
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

	public MouseButton getMouseButton() {
		return mouseButton;
	}

	public int getClickCount() {
		return clickCount;
	}
}
