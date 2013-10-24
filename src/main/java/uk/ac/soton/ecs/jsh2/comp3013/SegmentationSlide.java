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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

public class SegmentationSlide implements Slide, VideoDisplayListener<MBFImage> {
	private final FelzenszwalbHuttenlocherSegmenter<MBFImage> seg = new FelzenszwalbHuttenlocherSegmenter<MBFImage>();
	private VideoCapture capture;
	private VideoDisplay<MBFImage> videoDisplay;
	private JPanel modelPanel;
	private ImageComponent modelFrame;
	private MBFImage segImage;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel window = new JPanel();
		window.setSize(width, height);
		window.setPreferredSize(new Dimension(width, height));

		this.capture = new VideoCapture(320, 240);

		window.setLayout(new GridBagLayout());

		final JPanel vidPanel = new JPanel(new GridBagLayout());
		vidPanel.setBorder(BorderFactory.createTitledBorder("Live Video"));
		this.videoDisplay = VideoDisplay.createVideoDisplay(this.capture, vidPanel);
		this.videoDisplay.addVideoListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		window.add(vidPanel, gbc);

		this.modelPanel = new JPanel(new GridBagLayout());
		this.modelPanel.setBorder(BorderFactory.createTitledBorder("Segmentation"));
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
		this.segImage = new MBFImage(320, 240, ColourSpace.RGB);
		this.modelFrame.setImage(ImageUtilities.createBufferedImageForDisplay(this.segImage));

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
		final List<ConnectedComponent> ccs = seg.segment(frame);

		for (final ConnectedComponent cc : ccs) {
			final Float[] col = RGBColour.randomColour();
			for (final Pixel p : cc.pixels) {
				segImage.setPixel(p.x, p.y, col);
			}
		}

		this.modelFrame.setImage(ImageUtilities.createBufferedImageForDisplay(this.segImage));
	}
}
