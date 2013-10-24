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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

public class MIAR {
	public static void main(String[] args) throws IOException {
		final BufferedImage background = ImageUtilities.createBufferedImageForDisplay(new FImage(1024, 768));

		final List<Slide> slides = new ArrayList<Slide>();

		// slides.add(new ShotDetectionSlide());

		for (int i = 1; i <= 9; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide0" + i + ".png")));

		slides.add(new DigitalAudioRepresentation());

		for (int i = 10; i <= 18; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide" + i + ".png")));

		// slides.add(new MusicSlide());

		for (int i = 20; i <= 26; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide" + i + ".png")));

		slides.add(new HistogramSlide(Mode.RGB_HISTOGRAM));

		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide28.png")));

		slides.add(new SegmentationSlide());

		for (int i = 30; i <= 31; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide" + i + ".png")));

		slides.add(new DoGPointsSlide());

		for (int i = 33; i <= 34; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide" + i + ".png")));

		slides.add(new SIFTTrackerSlide());

		for (int i = 36; i <= 38; i++)
			slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide" + i + ".png")));

		slides.add(new HistogramSlide(Mode.SIFT));

		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide40.png")));

		slides.add(new ShotDetectionSlide());

		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide42.png")));

		slides.add(new SpectrogramSlide());

		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide44.png")));

		new SlideshowApplication(slides, background.getWidth(), background.getHeight(), background);
	}
}
