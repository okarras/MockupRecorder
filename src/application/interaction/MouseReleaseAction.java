package application.interaction;

import application.views.controls.MockupPane;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MouseReleaseAction implements IAction {
	private String target;
	private String targetType;
	private double duration;
	private MouseButton mouseButton;

	public MouseReleaseAction() {
		// Needed for json object binding.
	}

	public MouseReleaseAction(final String target, final String targetType, final MouseButton mouseButton) {
		this.target = target;
		this.targetType = targetType;
		this.mouseButton = mouseButton;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node node = mockupPane.lookup(target);
		node.fireEvent(new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, mouseButton, 0, false, false, false, false, false, false, false, false, false, false, null));
		mockupPane.setMouseColor(Color.RED);
		mockupPane.setMouseRadius(10);
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
}
