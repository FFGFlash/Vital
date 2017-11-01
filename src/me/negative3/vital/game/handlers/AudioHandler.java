package me.negative3.vital.game.handlers;

import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import me.negative3.vital.library.audio.AudioLoader;

public class AudioHandler {
	private AudioLoader audioLoader;
	public LinkedList<AudioInputStream> audio = new LinkedList<>();
	public LinkedList<String> ids = new LinkedList<>();
	public LinkedList<String> links = new LinkedList<>();
	
	public LinkedList<Clip> nowPlaying = new LinkedList<>();
	public LinkedList<String> nowPlayingIds = new LinkedList<>();

	public AudioHandler() {
		this.audioLoader = new AudioLoader();
		add("title_music", "audio/porcelain_king");
		add("shotgun_shoot", "audio/shotgun");
		add("shotgun_reload", "audio/shotgunreload");
		add("footstep_0", "audio/footstep0");
		add("footstep_1", "audio/footstep1");
	}

	public void add(String id, String link) {
		id = id.toLowerCase();
		if (ids.contains(id)) {
			System.out.println("\"" + id + "\" is a taken image id");
			return;
		}
		ids.add(id);
		links.add(link);
		audio.add(audioLoader.loadAudio("/" + link + ".wav", false));
	}

	public void resetAudioInputStream(String id) {
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				audio.set(i, audioLoader.loadAudio("/" + links.get(i) + ".wav", true));
				break;
			}
		}
	}

	public AudioInputStream get(String id) {
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				return audio.get(i);
			}
		}
		return null;
	}
	
	public AudioInputStream get(int id) {
		return audio.get(id);
	}

	public void play(String id, boolean loop, float volume) {
		resetAudioInputStream(id);
		try {
			Clip clip = AudioSystem.getClip();
			clip.setFramePosition(0);
			System.out.println(id);
			clip.open(get(id));
			if (loop) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				clip.setLoopPoints(0, -1);
			}
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float v = volume;
			if (v > 6f)
				v = 6f;
			gainControl.setValue(v);
			clip.start();
			nowPlaying.add(clip);
			nowPlayingIds.add(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop(String id) {
		for(int i = 0; i < nowPlayingIds.size(); i++) {
			if (nowPlayingIds.get(i) == id) {
				nowPlaying.get(i).stop();
				nowPlayingIds.remove(i);
				nowPlaying.remove(i);
				break;
			}
		}
	}
	
	public void stop_all() {
		for(int i = nowPlayingIds.size()-1; i >= 0; i--) {
			nowPlaying.get(i).stop();
			nowPlayingIds.remove(i);
			nowPlaying.remove(i);
		}
	}

	public void play(int id, boolean loop, float volume) {
		play(ids.get(id), loop, volume);
	}
}