package uk.ac.soton.ecs.jsh2.comp3013;

import java.awt.Component;
import java.io.IOException;

import org.openimaj.audio.AudioFormat;
import org.openimaj.audio.AudioGrabberListener;
import org.openimaj.audio.JavaSoundAudioGrabber;
import org.openimaj.audio.SampleChunk;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.vis.audio.AudioSpectrogram;

public class SpectrogramSlide implements Slide, AudioGrabberListener {
	private AudioSpectrogram spectrogram;
	private JavaSoundAudioGrabber grabber;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		spectrogram = new AudioSpectrogram(width, height);

		grabber = new JavaSoundAudioGrabber(new AudioFormat(16, 44.1, 1));
		grabber.addAudioGrabberListener(this);
		grabber.setMaxBufferSize(256);
		new Thread(grabber).start();

		while (grabber.isStopped()) {
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
			}
		}

		return spectrogram;
	}

	@Override
	public void close() {
		if (grabber != null) {
			grabber.stop();
			grabber = null;
		}
	}

	@Override
	public void samplesAvailable(SampleChunk s) {
		spectrogram.setData(s);
		spectrogram.update();
	}
}
