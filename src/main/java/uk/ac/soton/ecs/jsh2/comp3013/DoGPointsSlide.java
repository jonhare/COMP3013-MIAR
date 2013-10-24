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
import java.io.IOException;

import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.feature.local.keypoints.KeypointVisualizer;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

public class DoGPointsSlide implements Slide, VideoDisplayListener<MBFImage> {
	private DoGSIFTEngine engine = new DoGSIFTEngine();
	private VideoCapture capture;
	private VideoDisplay<MBFImage> videoDisplay;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel window = new JPanel();
		window.setLayout(null);
		window.setSize(width, height);
		window.setPreferredSize(new Dimension(width, height));
		window.setBounds(0, 0, width, height);

		engine.getOptions().setDoubleInitialImage(false);

		this.capture = new VideoCapture(width, height);

		this.videoDisplay = VideoDisplay.createVideoDisplay(this.capture, window);
		this.videoDisplay.addVideoListener(this);

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
	public void afterUpdate(VideoDisplay<MBFImage> display) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(MBFImage frame) {
		final FImage fimg = ResizeProcessor.resample(frame.flatten(), 320, 240);

		final LocalFeatureList<Keypoint> kpts = engine.findFeatures(fimg);

		for (final Keypoint kp : kpts) {
			kp.x *= 4;
			kp.y *= 4;
			kp.scale *= 4;
		}

		KeypointVisualizer.drawPatchesInplace(frame, kpts, null, RGBColour.GREEN);
	}
}
