package application.video;

/**
 * 
 * @author Lennart Glauer
 * @notice Facade Design Pattern
 *
 */
public interface IVideoCaptureFacade {
	/**
	 * Start video capturing.
	 * 
	 * @throws org.bytedeco.javacv.FrameRecorder.Exception
	 */
	void start() throws org.bytedeco.javacv.FrameRecorder.Exception;

	/**
	 * Stop video capturing.
	 * 
	 * @throws org.bytedeco.javacv.FrameRecorder.Exception
	 */
	void stop() throws org.bytedeco.javacv.FrameRecorder.Exception;
}
