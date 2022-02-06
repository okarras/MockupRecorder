package application.views.controls;

import java.io.IOException;
import java.nio.file.Path;

import application.utils.SimpleAlert;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class MockupPane extends BorderPane {
	@FXML
	private StackPane mockupStackPane;
	@FXML
	private BorderPane mockupBorderPane;
	@FXML
	private Canvas canvas;

	private final ObjectProperty<Path> mockupPath = new SimpleObjectProperty<Path>();
	private final DoubleProperty mouseX = new SimpleDoubleProperty();
	private final DoubleProperty mouseY = new SimpleDoubleProperty();
	private final DoubleProperty mouseRadius = new SimpleDoubleProperty(10);
	private final ObjectProperty<Color> mouseColor = new SimpleObjectProperty<Color>(Color.RED);

	/**
	 * Create mockup pane.
	 */
	public MockupPane() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("MockupPane.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e); // NOPMD
		}
	}

	/**
	 * Reset controls to predefined state.
	 */
	public void reset() {
		setMockupPath(null);
		setMouseX(0);
		setMouseY(0);
		setMouseRadius(10);
		setMouseColor(Color.RED);
		setViewportWidth(-1);
		setViewportHeight(-1);
	}

	/**
	 * Reload current mockup.
	 */
	public void reload() {
		final Path mockup = getMockupPath();
		if (mockup != null) {
			setMockupPath(null);
			setMockupPath(mockup);
		}
	}

	@FXML
	private void initialize() {
		mockupBorderPane.minWidthProperty().bind(mockupBorderPane.maxWidthProperty());
		mockupBorderPane.minHeightProperty().bind(mockupBorderPane.maxHeightProperty());

		mockupStackPane.minWidthProperty().bind(mockupBorderPane.minWidthProperty());
		mockupStackPane.minHeightProperty().bind(mockupBorderPane.minHeightProperty());
		mockupStackPane.maxWidthProperty().bind(mockupBorderPane.maxWidthProperty());
		mockupStackPane.maxHeightProperty().bind(mockupBorderPane.maxHeightProperty());

		canvas.widthProperty().bind(mockupBorderPane.widthProperty());
		canvas.heightProperty().bind(mockupBorderPane.heightProperty());

		// Load fxml when mockup property changes.
		mockupPath.addListener((obsVal, oldVal, newVal) -> {
			if (newVal == null) {
				mockupBorderPane.setCenter(null);
				return;
			}

			try {
				final FXMLLoader loader = new FXMLLoader(newVal.toUri().toURL());
				mockupBorderPane.setCenter(loader.load());
			} catch (IOException e) {
				SimpleAlert.exception(e);
			}
		});

		// Draw mouse pointer on canvas.
		mouseX.addListener((obsVal, oldVal, newVal) -> {
			final GraphicsContext graphics = canvas.getGraphicsContext2D();
			final double radius = getMouseRadius();
			graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graphics.setFill(getMouseColor());
			graphics.fillOval(getMouseX() - radius, getMouseY() - radius, 2 * radius, 2 * radius);
		});
		mouseY.addListener((obsVal, oldVal, newVal) -> {
			final GraphicsContext graphics = canvas.getGraphicsContext2D();
			final double radius = getMouseRadius();
			graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graphics.setFill(getMouseColor());
			graphics.fillOval(getMouseX() - radius, getMouseY() - radius, 2 * radius, 2 * radius);
		});
		mouseRadius.addListener((obsVal, oldVal, newVal) -> {
			final GraphicsContext graphics = canvas.getGraphicsContext2D();
			final double radius = getMouseRadius();
			graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graphics.setFill(getMouseColor());
			graphics.fillOval(getMouseX() - radius, getMouseY() - radius, 2 * radius, 2 * radius);
		});
	}

	public StackPane getStackPane() {
		return mockupStackPane;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public double getViewportWidth() {
		return mockupBorderPane.getMaxWidth();
	}

	public void setViewportWidth(final double maxWidth) {
		mockupBorderPane.setMaxWidth(maxWidth);
	}

	public DoubleProperty viewportWidthProperty() {
		return mockupBorderPane.maxWidthProperty();
	}

	public double getViewportHeight() {
		return mockupBorderPane.getMaxHeight();
	}

	public void setViewportHeight(final double maxHeight) {
		mockupBorderPane.setMaxHeight(maxHeight);
	}

	public DoubleProperty viewportHeightProperty() {
		return mockupBorderPane.maxHeightProperty();
	}

	public double getMouseX() {
		return mouseX.get();
	}

	public void setMouseX(final double mouseX) {
		this.mouseX.set(mouseX);
	}

	public DoubleProperty mouseXProperty() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY.get();
	}

	public void setMouseY(final double mouseY) {
		this.mouseY.set(mouseY);
	}

	public DoubleProperty mouseYProperty() {
		return mouseY;
	}

	public double getMouseRadius() {
		return mouseRadius.get();
	}

	public void setMouseRadius(final double radius) {
		mouseRadius.set(radius);
	}

	public DoubleProperty mouseRadiusProperty() {
		return mouseRadius;
	}

	public Color getMouseColor() {
		return mouseColor.get();
	}

	public void setMouseColor(final Color color) {
		mouseColor.set(color);
	}

	public ObjectProperty<Color> mouseColorProperty() {
		return mouseColor;
	}

	public Path getMockupPath() {
		return mockupPath.get();
	}

	public void setMockupPath(final Path path) {
		mockupPath.set(path);
	}

	public ObjectProperty<Path> mockupPathProperty() {
		return mockupPath;
	}
}
