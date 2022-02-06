package application.utils;

import java.util.function.Predicate;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class BooleanPropertyWithPredicate extends SimpleBooleanProperty {
	private final Predicate<Boolean> predicate;

	public BooleanPropertyWithPredicate(final Predicate<Boolean> predicate) {
		super();

		this.predicate = predicate;
	}

	public BooleanPropertyWithPredicate(final boolean initialValue, final Predicate<Boolean> predicate) {
		super(initialValue);

		this.predicate = predicate;
	}

	/**
	 * Set value if the predicate returns true.
	 */
	@Override
	public void set(final boolean value) {
		if (!predicate.test(value)) {
			throw new IllegalStateException("Pre-condition failed");
		}
		super.set(value);
	}
}
