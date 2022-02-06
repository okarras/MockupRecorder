package application.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import application.interaction.IAction;
import application.interaction.KeyPressAction;
import application.interaction.KeyReleaseAction;
import application.interaction.KeyTypeAction;
import application.interaction.MouseClickAction;
import application.interaction.MouseMoveAction;
import application.views.controls.RecorderTimelineItem;

/**
 * 
 * @author Lennart Glauer
 *
 */
public final class ScenarioToTextConverter {
	private ScenarioToTextConverter() {
		// Utility class.
	}

	/**
	 * Convert given timeline interactions to simple text scenario.
	 * 
	 * @param timelineItems
	 * @return
	 */
	public static String convert(final List<RecorderTimelineItem> timelineItems) {
		final StringBuilder builder = new StringBuilder();

		for (final RecorderTimelineItem item : timelineItems) {
			builder.append(String.format(Locale.GERMANY, "Das System wechselt die Ansicht zu \"%s\".\n\n", item.getMockupPath().getFileName()));
			builder.append(convertActions(item.getActions()));
			builder.append('\n');
		}

		return builder.toString();
	}

	private static String convertActions(final List<IAction> actions) {
		final StringBuilder builder = new StringBuilder();
		final List<IAction> filteredActions = filterClicks(filterKeyType(filterMouseMove(actions)));
		final Iterator<IAction> it = filteredActions.iterator();

		while (it.hasNext()) {
			final IAction action = it.next();
			if (action.getClass() == KeyTypeAction.class) {
				final KeyTypeAction type = (KeyTypeAction) action;
				builder.append(String.format(Locale.GERMANY, "Der Benutzer tippt \"%s\" in %s \"%s\".\n", type.getCharacter(), type.getTargetType(), type.getTarget()));
			} else if (action.getClass() == MouseMoveAction.class) {
				final MouseMoveAction move = (MouseMoveAction) action;
				builder.append(String.format(Locale.GERMANY, "Der Benutzer bewegt den Zeiger zu %s \"%s\".\n", move.getTargetType(), move.getTarget()));
			} else if (action.getClass() == MouseClickAction.class) {
				final MouseClickAction click = (MouseClickAction) action;
				builder.append(String.format(Locale.GERMANY, "Der Benutzer klickt %d mal auf %s \"%s\".\n", click.getClickCount(), click.getTargetType(), click.getTarget()));
			}
		}

		return builder.toString();
	}

	/*
	 * Remove MouseMoveActions with small distance.
	 */
	private static List<IAction> filterMouseMove(final List<IAction> actions) {
		final List<IAction> result = new ArrayList<IAction>();
		final Iterator<IAction> it = actions.iterator();

		String target = "";
		double x = 0;
		double y = 0;

		while (it.hasNext()) {
			final IAction action = it.next();
			if (action.getClass() == MouseMoveAction.class) {
				final MouseMoveAction move = (MouseMoveAction) action;
				if (!target.equals(move.getTarget()) || Math.abs(move.getLocalX() - x) > 10 || Math.abs(move.getLocalY() - y) > 10) {
					result.add(action);
					target = move.getTarget();
					x = move.getLocalX();
					y = move.getLocalY();
				}
			} else {
				result.add(action);
			}
		}

		return result;
	}

	/*
	 * Merge KeyTypeActions.
	 */
	private static List<IAction> filterKeyType(final List<IAction> actions) {
		final List<IAction> result = new ArrayList<IAction>();
		final Iterator<IAction> it = actions.iterator();

		String target = "";
		String targetType = "";
		String characters = "";

		while (it.hasNext()) {
			final IAction action = it.next();
			if (action.getClass() == KeyTypeAction.class) {
				final KeyTypeAction type = (KeyTypeAction) action;
				if (target.equals(type.getTarget()) && !type.getCharacter().equals("\t") && !type.getCharacter().equals("\r")) {
					characters += type.getCharacter();
				} else {
					if (!characters.isEmpty()) {
						result.add(new KeyTypeAction(target, targetType, characters));
					}
					if (type.getCharacter().equals("\t")) {
						result.add(new KeyTypeAction(target, targetType, "TAB"));
						characters = "";
					} else if (type.getCharacter().equals("\r")) {
						result.add(new KeyTypeAction(target, targetType, "RETURN"));
						characters = "";
					} else {
						characters = type.getCharacter();
					}
					target = type.getTarget();
					targetType = type.getTargetType();
				}
			} else {
				if (action.getClass() != KeyPressAction.class && action.getClass() != KeyReleaseAction.class) {
					if (!characters.isEmpty()) {
						result.add(new KeyTypeAction(target, targetType, characters));
					}
					target = "";
					targetType = "";
					characters = "";
				}
				result.add(action);
			}
		}

		if (!characters.isEmpty()) {
			result.add(new KeyTypeAction(target, targetType, characters));
		}

		return result;
	}

	/*
	 * Remove multiple click events in case of double click.
	 */
	private static List<IAction> filterClicks(final List<IAction> actions) {
		final List<IAction> result = new ArrayList<IAction>();

		String target = "";

		for (int i = actions.size() - 1; i >= 0; --i) {
			final IAction action = actions.get(i);
			if (action.getClass() == MouseClickAction.class) {
				if (!target.equals(action.getTarget())) {
					result.add(action);
					target = action.getTarget();
				}
			} else {
				result.add(action);
				target = "";
			}
		}

		Collections.reverse(result);
		return result;
	}
}
