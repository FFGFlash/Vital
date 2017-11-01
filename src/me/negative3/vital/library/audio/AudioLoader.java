package me.negative3.vital.library.audio;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioLoader {
	private AudioInputStream audio;

	public AudioInputStream loadAudio(String path, boolean reset) {
		try {
			audio = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!reset)
			System.out.println(path + " - " + audio.getFormat());
		return audio;
	}
}
