package uk.ac.soton.ecs.jsh2.comp3013;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JPanel;

import org.openimaj.audio.AudioFormat;
import org.openimaj.audio.AudioGrabberListener;
import org.openimaj.audio.JavaSoundAudioGrabber;
import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.analysis.EffectiveSoundPressure;
import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.image.DisplayUtilities.ScalingImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.vis.audio.AudioWaveform;

public class DigitalAudioRepresentation extends PictureSlide implements AudioGrabberListener {
	private ScalingImageComponent waveform;
	private AudioWaveform aw;
	private JavaSoundAudioGrabber grabber;
	private EffectiveSoundPressure esp;

	public DigitalAudioRepresentation() throws IOException {
		super(ImageUtilities.readMBF(DigitalAudioRepresentation.class.getResource("slides/Slide12.png")));
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel panel = new JPanel();

		panel.setOpaque(false);
		panel.setLayout(null);// new GridLayout(1, 1));
		panel.add(super.getComponent(width, height));

		aw = new AudioWaveform(450, 506);
		aw.setBounds(52, 180, 450, 506);
		panel.add(aw);

		grabber = new JavaSoundAudioGrabber(new AudioFormat(16, 44.1, 1));
		grabber.addAudioGrabberListener(this);
		new Thread(grabber).start();

		esp = new EffectiveSoundPressure();
		esp.setUnderlyingStream(grabber);

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
		aw.setData(s.getSampleBuffer());
		aw.update();

		System.out.println(esp.getEffectiveSoundPressure());
	}
}
