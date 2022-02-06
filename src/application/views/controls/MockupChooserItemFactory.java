package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 
 * @author Lennart Glauer
 * @notice Factory Design Pattern
 *
 */
public interface MockupChooserItemFactory {
	/**
	 * Create new mockup chooser item.
	 * 
	 * @param chooser
	 * @param mockupPath
	 * @return
	 * @throws IOException
	 */
	MockupChooserItem createItem(final MockupChooser chooser, final Path mockupPath) throws IOException;
}
