package application.interaction;

import application.views.controls.RecorderTimelineItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class ActionEventFilter implements EventHandler<InputEvent> {
	private final ObjectProperty<RecorderTimelineItem> activeTimelineItem = new SimpleObjectProperty<RecorderTimelineItem>();

	/**
	 * Handle javafx input event.
	 */
	@Override
	public void handle(final InputEvent event) {
		final RecorderTimelineItem item = getActiveTimelineItem();
		if (item != null) {
			final Node target = getTarget(event);
			if (target != null && target.getId() != null) {
				dispatch(event, item, target);
			}
		}
	}

	/*
	 * Find first ancestor of event target (including itself) with fx:id.
	 */
	private Node getTarget(final InputEvent event) {
		Node target = (Node) event.getTarget();
		do {
			if (target.getId() != null) {
				break;
			}
		} while ((target = target.getParent()) != null);
		return target;
	}

	private void dispatch(final InputEvent event, final RecorderTimelineItem item, final Node target) {
		final String selector = "#" + target.getId();
		final String type = target.getTypeSelector();

		if (event.getEventType() == KeyEvent.KEY_PRESSED) {
			final KeyEvent keyEvent = (KeyEvent) event;
			item.getActions().add(new KeyPressAction(selector, type, keyEvent.getText(), keyEvent.getCode()));

		} else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
			final KeyEvent keyEvent = (KeyEvent) event;
			item.getActions().add(new KeyReleaseAction(selector, type, keyEvent.getText(), keyEvent.getCode()));

		} else if (event.getEventType() == KeyEvent.KEY_TYPED) {
			final KeyEvent keyEvent = (KeyEvent) event;
			item.getActions().add(new KeyTypeAction(selector, type, keyEvent.getCharacter()));

		} else if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			final MouseEvent mouseEvent = (MouseEvent) event;
			final Point2D pointInTarget = target.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
			item.getActions().add(new MouseMoveAction(selector, type, pointInTarget.getX(), pointInTarget.getY()));
			item.getActions().add(new MousePressAction(selector, type, mouseEvent.getButton()));

		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			final MouseEvent mouseEvent = (MouseEvent) event;
			final Point2D pointInTarget = target.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
			item.getActions().add(new MouseMoveAction(selector, type, pointInTarget.getX(), pointInTarget.getY()));
			item.getActions().add(new MouseReleaseAction(selector, type, mouseEvent.getButton()));

		} else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			final MouseEvent mouseEvent = (MouseEvent) event;
			item.getActions().add(new MouseClickAction(selector, type, mouseEvent.getButton(), mouseEvent.getClickCount()));
		}
	}

	protected RecorderTimelineItem getActiveTimelineItem() {
		return activeTimelineItem.get();
	}

	protected void setActiveTimelineItem(final RecorderTimelineItem item) {
		activeTimelineItem.set(item);
	}

	public ObjectProperty<RecorderTimelineItem> activeTimelineItemProperty() {
		return activeTimelineItem;
	}
}
