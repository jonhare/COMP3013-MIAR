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

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import org.openimaj.audio.AudioFormat;
import org.openimaj.audio.AudioGrabberListener;
import org.openimaj.audio.JavaSoundAudioGrabber;
import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.analysis.EffectiveSoundPressure;
import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.vis.audio.AudioWaveform;

public class DigitalAudioRepresentation extends PictureSlide implements AudioGrabberListener {
	private static final Float[] MIC_COL = new Float[] { 192f / 255f, 80f / 255f, 77f / 255f };
	private AudioWaveform aw;
	private JavaSoundAudioGrabber grabber;
	private EffectiveSoundPressure esp;
	private ImageComponent espic;
	private MBFImage espImg;
	private BufferedImage espBImg;

	public DigitalAudioRepresentation() throws IOException {
		super(ImageUtilities.readMBF(DigitalAudioRepresentation.class.getResource("slides/Slide10.png")));
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel panel = new JPanel();

		panel.setOpaque(false);
		panel.setLayout(null);// new GridLayout(1, 1));
		final Component bg = super.getComponent(width, height);
		panel.add(bg);

		aw = new AudioWaveform(450, 506);
		aw.setColour(new Float[] { 233f / 255f, 236f / 255f, 236f / 255f });
		aw.setMaximum(2);
		aw.setBounds(52, 180, 450, 506);
		panel.add(aw);

		grabber = new JavaSoundAudioGrabber(new AudioFormat(16, 44.1, 1));
		grabber.addAudioGrabberListener(this);
		grabber.setMaxBufferSize(4096);
		new Thread(grabber).start();

		esp = new EffectiveSoundPressure();
		espic = new DisplayUtilities.ImageComponent(false);
		espic.setBackground(Color.WHITE);
		espic.setShowPixelColours(false);
		espic.setShowXYPosition(false);
		espic.removeMouseListener(espic);
		espic.removeMouseMotionListener(espic);
		panel.add(espic);
		espic.setBounds(630, 525, 46, 122);
		espImg = new MBFImage(46, 122, ColourSpace.RGB);
		espImg.fill(RGBColour.WHITE);
		espImg.drawLine(espImg.getWidth() / 2 - 1, 0, espImg.getWidth() / 2 - 1, espImg.getHeight(), 1, MIC_COL);
		espic.setImage(ImageUtilities.createBufferedImageForDisplay(espImg, espBImg));

		panel.setComponentZOrder(bg, 2);

		while (grabber.isStopped()) {
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
			}
		}

		return panel;
	}

	@Override
	public void close() {
		super.close();

		if (grabber != null) {
			grabber.stop();
			grabber = null;
		}
	}

	@Override
	public void samplesAvailable(SampleChunk s) {
		// update waveform
		aw.setData(s.getSampleBuffer());
		aw.update();

		// update microphone
		try {
			esp.process(s);
			espImg.fill(RGBColour.WHITE);

			final double press = esp.getEffectiveSoundPressure();

			int px = espImg.getWidth() / 2 - 1;
			for (int i = 2; i <= espImg.getHeight(); i += 2) {
				final double prop = Math.sin(Math.PI * i / espImg.getHeight());
				final double amp = (espImg.getWidth() / 2) * prop * press / 10000;

				final int nx = (int) ((espImg.getWidth() / 2) - amp);

				espImg.drawLine(px, i - 2, nx, i, 2, MIC_COL);

				px = nx;
			}

			espic.setImage(ImageUtilities.createBufferedImageForDisplay(espImg, espBImg));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
