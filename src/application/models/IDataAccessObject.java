package application.models;

import java.io.IOException;

public interface IDataAccessObject<T> {
	/**
	 * Load data model.
	 * 
	 * @throws IOException
	 */
	T load() throws IOException;

	/**
	 * Store data model.
	 * 
	 * @throws IOException
	 */
	void store(T model) throws IOException;
}
