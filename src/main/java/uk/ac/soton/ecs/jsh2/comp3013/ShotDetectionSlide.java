package uk.ac.soton.ecs.jsh2.comp3013;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.video.processing.shotdetector.CombiShotDetector;
import org.openimaj.video.processing.shotdetector.HistogramVideoShotDetector;
import org.openimaj.video.processing.shotdetector.LocalHistogramVideoShotDetector;
import org.openimaj.video.processing.shotdetector.ShotBoundary;
import org.openimaj.video.processing.shotdetector.ShotDetectedListener;
import org.openimaj.video.processing.shotdetector.VideoKeyframe;
import org.openimaj.video.timecode.VideoTimecode;
import org.openimaj.video.xuggle.XuggleVideo;

public class ShotDetectionSlide implements Slide, ShotDetectedListener<MBFImage> {
	private ImageComponent shotDetectorFrame;
	private ImageComponent videoDisplay;
	private JPanel vidPanel;
	private MBFImage videoImage;
	private JPanel shotDetectorPanel;
	private MBFImage shotDetectorImage;
	private ArrayList<VideoKeyframe<MBFImage>> keyframes;
	private ResizeProcessor rp;
	private MBFImageRenderer renderer;
	private CombiShotDetector vsd;
	private double threshold;
	private static final int th = 64;
	private static final int tw = 114;
	private XuggleVideo video;
	private JPanel keyframesPanel;
	private ImageComponent keyframesFrame;
	private MBFImage keyframesImage;
	private BufferedImage keyframesBImg;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel window = new JPanel();
		window.setSize(width, height);
		window.setPreferredSize(new Dimension(width, height));
		window.setLayout(new GridBagLayout());

		this.vidPanel = new JPanel(new GridBagLayout());
		vidPanel.setBorder(BorderFactory.createTitledBorder("Video"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		window.add(vidPanel, gbc);

		this.videoDisplay = new ImageComponent(true, false);
		this.videoDisplay.setShowPixelColours(false);
		this.videoDisplay.setShowXYPosition(false);
		this.videoDisplay.removeMouseListener(videoDisplay);
		this.videoDisplay.removeMouseMotionListener(videoDisplay);
		this.vidPanel.add(this.videoDisplay);
		this.videoImage = new MBFImage(320, 240, ColourSpace.RGB);
		this.videoDisplay.setImage(this.videoBImg = ImageUtilities.createBufferedImageForDisplay(this.videoImage,
				this.videoBImg));

		this.shotDetectorPanel = new JPanel(new GridBagLayout());
		this.shotDetectorPanel.setBorder(BorderFactory.createTitledBorder("Shot Detector"));
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridy = 1;
		window.add(this.shotDetectorPanel, gbc);

		this.shotDetectorFrame = new ImageComponent(true, false);
		this.shotDetectorFrame.setShowPixelColours(false);
		this.shotDetectorFrame.setShowXYPosition(false);
		this.shotDetectorFrame.removeMouseListener(shotDetectorFrame);
		this.shotDetectorFrame.removeMouseMotionListener(shotDetectorFrame);
		this.shotDetectorPanel.add(this.shotDetectorFrame);
		this.shotDetectorImage = new MBFImage(width - 20, 200, ColourSpace.RGB);
		this.shotDetectorFrame.setImage(this.shotDetBImg =
				ImageUtilities.createBufferedImageForDisplay(this.shotDetectorImage,
						this.shotDetBImg));

		this.keyframesPanel = new JPanel(new GridBagLayout());
		this.keyframesPanel.setBorder(BorderFactory.createTitledBorder("Keyframes"));
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.gridy = 2;
		window.add(this.keyframesPanel, gbc);

		this.keyframesFrame = new ImageComponent(true, false);
		this.keyframesFrame.setShowPixelColours(false);
		this.keyframesFrame.setShowXYPosition(false);
		this.keyframesFrame.removeMouseListener(keyframesFrame);
		this.keyframesFrame.removeMouseMotionListener(keyframesFrame);
		this.keyframesPanel.add(this.keyframesFrame);
		this.keyframesImage = new MBFImage(width - 20, th, ColourSpace.RGB);
		this.keyframesFrame.setImage(this.keyframesBImg =
				ImageUtilities.createBufferedImageForDisplay(this.keyframesImage,
						this.keyframesBImg));

		this.keyframes = new ArrayList<VideoKeyframe<MBFImage>>();
		this.renderer = this.shotDetectorImage.createRenderer();
		this.rp = new ResizeProcessor(tw, th, true);

		try {
			this.video = new XuggleVideo(
					"/Users/jsh2/Work/openimaj/trunk/demos/demos/src/main/resources/org/openimaj/demos/video/guy_goma.mp4");
		} catch (final Exception e) {
			this.video = new XuggleVideo(
					"http://sourceforge.net/p/openimaj/code/HEAD/tree/trunk/demos/demos/src/main/resources/org/openimaj/demos/video/guy_goma.mp4?format=raw");
		}
		vsd = new CombiShotDetector(video);
		vsd.addVideoShotDetector(new HistogramVideoShotDetector(video), 1);
		vsd.addVideoShotDetector(new LocalHistogramVideoShotDetector(video,
				20), 1);
		this.threshold = vsd.getThreshold();
		vsd.setStoreAllDifferentials(true);
		vsd.setFindKeyframes(true);
		vsd.addShotDetectedListener(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				vsd.process();
			}
		}).start();

		return window;
	}

	@Override
	public void close() {
		if (video != null) {
			video.close();
			video = null;
		}
	}

	private double lastMax = 10000;
	private BufferedImage shotDetBImg;
	private BufferedImage videoBImg;

	@Override
	public void shotDetected(final ShotBoundary<MBFImage> sb, final VideoKeyframe<MBFImage> vk)
	{
		// Store the keyframe
		if (vk != null) {
			keyframes.add(vk.clone());

			int i = -1;
			for (final VideoKeyframe<MBFImage> kf : keyframes) {
				this.keyframesImage.drawImage(kf.getImage().process(rp), tw * i, 0);
				i++;
			}
			this.keyframesFrame.setImage(this.keyframesBImg =
					ImageUtilities.createBufferedImageForDisplay(this.keyframesImage,
							this.keyframesBImg));
		}

		// Reset the image
		this.shotDetectorImage.zero();

		// Calculate the various variables required to draw the
		// visualisation.
		final DoubleFV dfv = vsd.getDifferentials();
		double max = Double.MIN_VALUE;
		for (int x = 0; x < dfv.length(); x++)
			max = Math.max(max, dfv.get(x));
		if (max > 50)
			this.lastMax = max;

		// Draw all the keyframes found onto the image
		for (final VideoKeyframe<MBFImage> kf : keyframes)
		{
			final long fn = kf.getTimecode().getFrameNumber();
			final int x = (int) (fn * this.shotDetectorImage.getWidth() / dfv.length());

			// We draw the keyframes along the top of the visualisation.
			// So we draw a line to the frame to match it up to the
			// differential
			renderer.drawLine(x, this.shotDetectorImage.getHeight(), x, 0, new Float[] { 0.3f, 0.3f, 0.3f });
			renderer.drawImage(kf.getImage().process(rp), x + 1, 0);
		}

		// This is the threshold line drawn onto the image.
		renderer.drawLine(0, (int) (this.shotDetectorImage.getHeight() - this.shotDetectorImage.getHeight() / max
				* threshold), this.shotDetectorImage.getWidth(),
				(int) (this.shotDetectorImage.getHeight() - this.shotDetectorImage.getHeight() / max * threshold),
				RGBColour.RED);

		// Now draw all the differentials
		int x = 0;
		for (int z = 0; z < dfv.length(); z++)
		{
			x = z * this.shotDetectorImage.getWidth() / dfv.length();
			renderer.drawLine(x, this.shotDetectorImage.getHeight(), x,
					(int) (this.shotDetectorImage.getHeight() - this.shotDetectorImage.getHeight() / max * dfv.get(z)),
					RGBColour.WHITE);
		}
	}

	@Override
	public void differentialCalculated(final VideoTimecode vt, final double d, final MBFImage frame)
	{
		this.shotDetected(null, null);

		renderer.drawShapeFilled(new Rectangle(this.shotDetectorImage.getWidth() + tw / 2 - 5, th, 10,
				this.shotDetectorImage.getHeight() - th), RGBColour.BLACK);
		renderer.drawLine(this.shotDetectorImage.getWidth() + tw / 2, this.shotDetectorImage.getHeight(),
				this.shotDetectorImage.getWidth() + tw / 2,
				(int) (this.shotDetectorImage.getHeight() - this.shotDetectorImage.getHeight() / this.lastMax * d), 10,
				RGBColour.RED);
		renderer.drawImage(frame.process(rp), this.shotDetectorImage.getWidth(), 0);

		// Display the visualisation
		this.shotDetectorFrame.setImage(shotDetBImg = ImageUtilities.createBufferedImageForDisplay(shotDetectorImage,
				shotDetBImg));

		this.videoDisplay.setImage(videoBImg = ImageUtilities.createBufferedImageForDisplay(frame, videoBImg));
	}
}
