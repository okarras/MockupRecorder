package application.video;

import java.nio.file.Path;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;

/**
 * Record opencv frames grabbed from javafx nodes.
 * 
 * @author Lennart Glauer
 *
 */
public class JavaFXFrameRecorder extends FFmpegFrameRecorder {
	/**
	 * Initialize javafx frame recorder.
	 * 
	 * @param video Video output file
	 * @param imageWidth Frame width
	 * @param imageHeight Frame height
	 */
	public JavaFXFrameRecorder(final Path video, final int imageWidth, final int imageHeight) {
		super(video.toFile(), imageWidth, imageHeight);

		setVideoCodec(avcodec.AV_CODEC_ID_H264);
		setFormat("mp4");

		// see: https://trac.ffmpeg.org/wiki/StreamingGuide
		setVideoOption("tune", "zerolatency");
		// see: https://trac.ffmpeg.org/wiki/Encode/H.264
		setVideoOption("preset", "ultrafast");
		setVideoOption("crf", "22");
	}
}
