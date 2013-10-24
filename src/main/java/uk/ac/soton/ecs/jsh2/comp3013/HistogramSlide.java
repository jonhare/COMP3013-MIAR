/**
 * Copyright (c) 2013, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package uk.ac.soton.ecs.jsh2.comp3013;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.io.IOUtils;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.ml.clustering.ByteCentroidsResult;
import org.openimaj.ml.clustering.assignment.hard.ExactByteAssigner;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

public class HistogramSlide implements Slide, VideoDisplayListener<MBFImage> {

	private VideoCapture capture;
	private VideoDisplay<MBFImage> videoDisplay;
	private JPanel modelPanel;
	private Mode mode;
	private ImageComponent modelFrame;
	private MBFImage histogramImage;

	public HistogramSlide(Mode mode) {
		this.mode = mode;
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel window = new JPanel();
		window.setSize(width, height);
		window.setPreferredSize(new Dimension(width, height));

		this.capture = new VideoCapture(640, 480);

		window.setLayout(new GridBagLayout());

		final JPanel vidPanel = new JPanel(new GridBagLayout());
		vidPanel.setBorder(BorderFactory.createTitledBorder("Live Video"));
		this.videoDisplay = VideoDisplay.createVideoDisplay(this.capture, vidPanel);
		this.videoDisplay.addVideoListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		window.add(vidPanel, gbc);

		this.modelPanel = new JPanel(new GridBagLayout());
		this.modelPanel.setBorder(BorderFactory.createTitledBorder("Feature type: " + this.mode.toString()));
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.gridy = 1;
		window.add(this.modelPanel, gbc);

		this.modelFrame = new ImageComponent(true, false);
		this.modelFrame.setShowPixelColours(false);
		this.modelFrame.setShowXYPosition(false);
		this.modelFrame.removeMouseListener(modelFrame);
		this.modelFrame.removeMouseMotionListener(modelFrame);
		this.modelPanel.add(this.modelFrame);
		this.histogramImage = new MBFImage(1000, 200, ColourSpace.RGB);
		this.modelFrame.setImage(ImageUtilities.createBufferedImageForDisplay(this.histogramImage));

		return window;
	}

	@Override
	public void close() {
		try {
			if (this.capture != null) {
				videoDisplay.close();
				capture.stopCapture();
				capture.close();
				capture = null;
			}
		} catch (final Exception e) {

		}
	}

	@Override
	public void afterUpdate(final VideoDisplay<MBFImage> display) {

	}

	@Override
	public synchronized void beforeUpdate(final MBFImage frame) {
		final DoubleFV histogram = this.mode.createFeature(frame);

		this.drawHistogramImage(histogram);
		this.modelFrame.setImage(ImageUtilities.createBufferedImageForDisplay(this.histogramImage));
	}

	private void drawHistogramImage(DoubleFV histogram) {
		histogram = histogram.normaliseFV();

		final int width = this.histogramImage.getWidth();
		final int height = this.histogramImage.getHeight();

		final int bw = width / histogram.length();

		this.histogramImage.zero();
		final MBFImageRenderer renderer = this.histogramImage.createRenderer();
		final Rectangle s = new Rectangle();
		s.width = bw;
		for (int i = 0; i < histogram.values.length; i++) {
			final int rectHeight = (int) (histogram.values[i] * height);
			final int remHeight = height - rectHeight;

			s.x = i * bw;
			s.y = remHeight;
			s.height = rectHeight;
			renderer.drawShapeFilled(s, this.mode.colourForBin(i));
		}
	}
}

enum Mode {
	RGB_HISTOGRAM {
		HistogramModel model = new HistogramModel(4, 4, 4);
		Float[][] binCols = null;

		@Override
		public String toString() {
			return "64-bin RGB Histogram";
		}

		@Override
		public DoubleFV createFeature(final MBFImage image) {
			this.model.estimateModel(image);
			return this.model.histogram;
		}

		void buildBinCols() {
			this.binCols = new Float[4 * 4 * 4][3];
			for (int k = 0; k < 4; k++) {
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < 4; i++) {
						this.binCols[k * 4 * 4 + j * 4 + i][0] = (float) i / 4 + (0.5f / 4);
						this.binCols[k * 4 * 4 + j * 4 + i][1] = (float) j / 4 + (0.5f / 4);
						this.binCols[k * 4 * 4 + j * 4 + i][2] = (float) k / 4 + (0.5f / 4);
					}
				}
			}
		}

		@Override
		public Float[] colourForBin(final int bin) {
			if (this.binCols == null)
				this.buildBinCols();

			return this.binCols[bin];
		}
	},
	HSV_HISTOGRAM {
		HistogramModel model = new HistogramModel(4, 4, 4);
		Float[][] binCols = null;

		@Override
		public String toString() {
			return "64-bin HSV Histogram";
		}

		@Override
		public DoubleFV createFeature(MBFImage image) {
			image = Transforms.RGB_TO_HSV(image);
			this.model.estimateModel(image);
			return this.model.histogram;
		}

		void buildBinCols() {
			this.binCols = new Float[4 * 4 * 4][];
			for (int k = 0; k < 4; k++) {
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < 4; i++) {
						final float h = (float) i / 4 + (0.5f / 4);
						final float s = (float) j / 4 + (0.5f / 4);
						final float v = (float) k / 4 + (0.5f / 4);

						MBFImage img = new MBFImage(1, 1, ColourSpace.HSV);
						img.setPixel(0, 0, new Float[] { h, s, v });

						img = Transforms.HSV_TO_RGB(img);

						this.binCols[k * 4 * 4 + j * 4 + i] = img.getPixel(0, 0);
					}
				}
			}
		}

		@Override
		public Float[] colourForBin(final int bin) {
			if (this.binCols == null)
				this.buildBinCols();

			return this.binCols[bin];
		}
	},
	SIFT {
		ExactByteAssigner rabc = null;
		DoubleFV fv = null;
		DoGSIFTEngine engine = new DoGSIFTEngine();

		@Override
		public String toString() {
			return "100-term SIFT BoVW";
		}

		@Override
		public DoubleFV createFeature(final MBFImage image) {
			if (this.rabc == null) {
				try {
					final ByteCentroidsResult clusterer = IOUtils.read(Mode.class
							.getResourceAsStream("random-100-highfield-codebook.voc"),
							ByteCentroidsResult.class);

					this.rabc = new ExactByteAssigner(clusterer);
					this.fv = new DoubleFV(clusterer.numClusters());
					this.engine.getOptions().setDoubleInitialImage(false);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

			FImage img = Transforms.calculateIntensity(image);
			img = ResizeProcessor.halfSize(img);
			final List<Keypoint> keys = this.engine.findFeatures(img);

			for (final Keypoint keypoint : keys) {
				image.drawPoint(new Point2dImpl(keypoint.x * 2f, keypoint.y * 2f), RGBColour.RED, 3);
			}

			Arrays.fill(this.fv.values, 0);

			for (final Keypoint k : keys) {
				this.fv.values[this.rabc.assign(k.ivec)]++;
			}

			return this.fv;
		}

		@Override
		public Float[] colourForBin(final int bin) {
			return RGBColour.RED;
		}
	};
	public abstract DoubleFV createFeature(MBFImage image);

	public abstract Float[] colourForBin(int bin);
}
