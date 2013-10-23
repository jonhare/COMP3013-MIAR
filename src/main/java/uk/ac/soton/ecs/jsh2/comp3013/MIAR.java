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

		slides.add(new DigitalAudioRepresentation());

		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide01.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide02.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide03.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide04.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide05.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide06.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide07.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide08.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide09.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide10.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide11.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide12.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide13.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide14.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide15.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide16.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide17.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide18.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide19.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide20.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide21.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide22.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide23.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide24.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide25.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide26.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide27.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide28.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide29.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide30.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide31.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide32.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide33.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide34.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide35.png")));
		slides.add(new PictureSlide(MIAR.class.getResource("slides/Slide36.png")));

		new SlideshowApplication(slides, background.getWidth(), background.getHeight(), background);
	}
}
