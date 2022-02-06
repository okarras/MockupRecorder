package application.interaction;

import application.views.controls.MockupPane;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;

public class MouseMoveAction implements IAction {
	private String target;
	private String targetType;
	private double duration = 1000;
	private double localX;
	private double localY;

	public MouseMoveAction() {
		// Needed for json object binding.
	}

	public MouseMoveAction(final String target, final String targetType, final double localX, final double localY) {
		this.target = target;
		this.targetType = targetType;
		this.localX = localX;
		this.localY = localY;
	}

	@Override
	public void execute(final MockupPane mockupPane) {
		final Node targetNode = mockupPane.lookup(target);
		final Point2D canvasPosition = mockupPane.getCanvas().sceneToLocal(targetNode.localToScene(localX, localY));

		final double distance = canvasPosition.distance(mockupPane.getMouseX(), mockupPane.getMouseY());
		if (distance < 3) {
			return;
		}

		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0.85 * getDuration()),
				new KeyValue(mockupPane.mouseXProperty(), canvasPosition.getX(), Interpolator.EASE_BOTH),
				new KeyValue(mockupPane.mouseYProperty(), canvasPosition.getY(), Interpolator.EASE_BOTH)));
		timeline.play();
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

	public double getLocalX() {
		return localX;
	}

	public double getLocalY() {
		return localY;
	}
}
