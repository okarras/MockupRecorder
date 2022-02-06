package application.interaction;

import application.views.controls.MockupPane;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MousePressAction implements IAction {
	private String target;
	private String targetType;
	private double duration = 50;
	private MouseButton mouseButton;

	public MousePressAction() {
		// Needed for json object binding.
	}

	public MousePressAction(final String target, final String targetType, final MouseButton mouseButton) {
		this.target = target;
		this.targetType = targetType;
		this.mouseButton = mouseButton;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node node = mockupPane.lookup(target);
		node.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, mouseButton, 0, false, false, false, false, false, false, false, false, false, false, null));
		mockupPane.setMouseColor(mouseButton == MouseButton.PRIMARY ? Color.BLUE : Color.GREEN);
		mockupPane.setMouseRadius(20);
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
