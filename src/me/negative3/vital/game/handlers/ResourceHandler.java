package me.negative3.vital.game.handlers;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.library.rendering.BufferedImageLoader;

public class ResourceHandler {
	private BufferedImageLoader imageLoader;
	public LinkedList<BufferedImage> images = new LinkedList<>();
	public LinkedList<String> ids = new LinkedList<>();

	public ResourceHandler() {
		imageLoader = new BufferedImageLoader();
		add("background_0", "art/background0");
		add("background_1", "art/background1");
		add("background_2", "art/background2");
		add("player", "art/player");
		add("shotgun", "art/gun");
		add("player_bullet", "art/bullet_ally");
		add("enemy_bullet", "art/bullet_enemy");
		add("tile_0", "art/tile_1");
		add("tile_1", "art/tile_2");
		add("tile_2", "art/tile_3");
		add("tile_3", "art/tile_4");
		add("door_tile", "art/door");
		add("corner_tile", "art/tile_corner");
		add("squish_tile", "art/tile_squish");
		add("floor_tile", "art/floor");
		add("enemy", "art/enemy");
		add("level_mockup", "levels/mockup");
		add("heart", "art/heart");
		add("no_heart", "art/noheart");
		add("cursor", "art/reticle");
		add("level_test", "levels/level_test");
	}

	public void add(String id, String link) {
		id = id.toLowerCase();
		if (ids.contains(id)) {
			System.out.println("\"" + id + "\" is a taken image id");
			return;
		}
		ids.add(id);
		images.add(imageLoader.loadImage("/" + link + ".png"));
	}

	public BufferedImage get(int index) {
		return images.get(index);
	}

	public BufferedImage get(String id) {
		id = id.toLowerCase();
		if (!ids.contains(id)) {
			System.out.println("\"" + id + "\" is not an image id");
			return null;
		}
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				return images.get(i);
			}
		}
		return null;
	}
}
