package uk.ac.soton.ecs.jsh2.comp3013;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JPanel;

import org.openimaj.audio.AudioPlayer;
import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.video.xuggle.XuggleAudio;

public class MusicSlide extends PictureSlide {
	private XuggleAudio audio;
	private AudioPlayer player;

	public MusicSlide() throws IOException {
		super(MIAR.class.getResource("slides/Slide19.png"));
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel panel = new JPanel();

		panel.setOpaque(false);
		panel.setLayout(null);// new GridLayout(1, 1));
		final Component bg = super.getComponent(width, height);
		panel.add(bg);

		audio = new XuggleAudio(MusicSlide.class.getResource("LB2-sample.mp3"));

		player = new AudioPlayer(audio);
		new Thread(player).start();

		return panel;
	}

	@Override
	public void close() {
		if (player != null) {
			player.stop();
			player = null;
			// audio.close();
			audio = null;
		}
	}
}
