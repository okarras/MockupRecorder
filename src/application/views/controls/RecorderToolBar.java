package application.views.controls;

import java.io.IOException;
import java.util.Locale;

import application.utils.BooleanPropertyWithPredicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

/**
 * 
 * @author Lennart Glauer
 *
 */
public class RecorderToolBar extends ToolBar {
	@FXML
	private Button startButton;
	@FXML
	private Button stopButton;
	@FXML
	private Button playButton;
	@FXML
	private Button pauseButton;
	@FXML
	private ClockLabel clockLabel;
	@FXML
	private Button toBeginButton;
	@FXML
	private Button toEndButton;
	@FXML
	private Button resizeButton;
	@FXML
	private Button exportButton;
	@FXML
	private Slider mouseSpeedSlider;

	// State transition constraints.
	private final BooleanPropertyWithPredicate isRecording = new BooleanPropertyWithPredicate(newVal -> {
		if (newVal) {
			return !isRecording() && !isPlaying();
		} else {
			return !isPlaying();
		}
	});
	private final BooleanPropertyWithPredicate isPlaying = new BooleanPropertyWithPredicate(newVal -> {
		if (newVal) {
			return !isRecording() && !isPlaying();
		} else {
			return !isRecording();
		}
	});
	private final BooleanPropertyWithPredicate isExporting = new BooleanPropertyWithPredicate(newVal -> {
		if (newVal) {
			return !isRecording() && !isPlaying();
		} else {
			return !isRecording();
		}
	});

	/**
	 * Create recorder tool bar.
	 */
	public RecorderToolBar() {
		super();

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("RecorderToolBar.fxml"));
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
		if (isRecording()) {
			stopRecording();
		}
		if (isPlaying()) {
			stopPlaying();
		}
		clockLabel.reset();
		setMouseSpeed(1);
	}

	@FXML
	private void initialize() {
		// Constraint: isExporting => isPlaying
		isExporting.addListener((obsVal, oldVal, newVal) -> {
			if (newVal) {
				startPlaying();
			}
		});
		isPlaying.addListener((obsVal, oldVal, newVal) -> {
			if (!newVal) {
				stopExporting();
			}
		});

		disableProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal) {
				stopRecording();
				stopPlaying();
			}
		});

		// Bindings.
		startButton.disableProperty().bind(Bindings.or(isRecording, isPlaying));
		stopButton.disableProperty().bind(isPlaying);
		playButton.disableProperty().bind(Bindings.or(isRecording, isPlaying));
		pauseButton.disableProperty().bind(isRecording);
		toBeginButton.disableProperty().bind(isPlaying);
		toEndButton.disableProperty().bind(isPlaying);
		resizeButton.disableProperty().bind(isPlaying);
		exportButton.disableProperty().bind(Bindings.or(isRecording, isPlaying));
		mouseSpeedSlider.disableProperty().bind(isPlaying);

		// User actions.
		startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startRecording());
		stopButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> stopRecording());
		playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startPlaying());
		pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> stopPlaying());
		exportButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startExporting());

		// Tooltips.
		startButton.setTooltip(new Tooltip("Aufnahme beginnen"));
		stopButton.setTooltip(new Tooltip("Aufnahme beenden"));
		playButton.setTooltip(new Tooltip("Wiedergabe starten"));
		pauseButton.setTooltip(new Tooltip("Wiedergabe pausieren"));
		toBeginButton.setTooltip(new Tooltip("Zum Anfang der Timeline springen"));
		toEndButton.setTooltip(new Tooltip("Zum Ende der Timeline springen"));
		resizeButton.setTooltip(new Tooltip("Viewport ändern"));
		exportButton.setTooltip(new Tooltip("Als Video exportieren"));
		mouseSpeedSlider.setTooltip(new Tooltip("Wiedergabegeschwindigkeit anpassen"));

		// Mouse speed slider label formatter.
		mouseSpeedSlider.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(final Double number) {
				return String.format(Locale.US, "%.1fx", number);
			}
			@Override
			public Double fromString(final String string) {
				throw new UnsupportedOperationException();
			}
		});

		// Toggle clock.
		isPlaying.addListener((obsVal, oldVal, newVal) -> {
			if (newVal) {
				clockLabel.start();
			} else {
				clockLabel.stop();
			}
		});
	}

	public Button getStartButton() {
		return startButton;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public Button getPlayButton() {
		return playButton;
	}

	public Button getPauseButton() {
		return pauseButton;
	}

	public ClockLabel getClockLabel() {
		return clockLabel;
	}

	public Button getToBeginButton() {
		return toBeginButton;
	}

	public Button getToEndButton() {
		return toEndButton;
	}

	public Button getResizeButton() {
		return resizeButton;
	}

	public Button getExportButton() {
		return exportButton;
	}

	public void startRecording() {
		isRecording.set(true);
	}

	public void stopRecording() {
		isRecording.set(false);
	}

	public void toggleRecording() {
		if (isRecording()) {
			stopRecording();
		} else {
			startRecording();
		}
	}

	public boolean isRecording() {
		return isRecording.get();
	}

	public ReadOnlyBooleanProperty isRecordingProperty() {
		return isRecording;
	}

	public void startPlaying() {
		isPlaying.set(true);
	}

	public void stopPlaying() {
		isPlaying.set(false);
	}

	public void togglePlaying() {
		if (isPlaying()) {
			stopPlaying();
		} else {
			startPlaying();
		}
	}

	public boolean isPlaying() {
		return isPlaying.get();
	}

	public ReadOnlyBooleanProperty isPlayingProperty() {
		return isPlaying;
	}

	public void startExporting() {
		isExporting.set(true);
	}

	public void stopExporting() {
		isExporting.set(false);
	}

	public boolean isExporting() {
		return isExporting.get();
	}

	public ReadOnlyBooleanProperty isExportingProperty() {
		return isExporting;
	}

	public double getMouseSpeed() {
		return mouseSpeedSlider.getValue();
	}

	public void setMouseSpeed(final double speed) {
		mouseSpeedSlider.setValue(speed);
	}

	public DoubleProperty mouseSpeedProperty() {
		return mouseSpeedSlider.valueProperty();
	}
}
