package me.negative3.vital.editor.handlers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.library.rendering.BufferedImageLoader;

public class ResourceHandler {
	private BufferedImageLoader imageLoader;
	public LinkedList<BufferedImage> images = new LinkedList<>();
	public LinkedList<Color> colors = new LinkedList<>();
	public LinkedList<String> ids = new LinkedList<>();

	public ResourceHandler() {
		imageLoader = new BufferedImageLoader();
		add("tile", "art/tile_1", new Color(0, 0, 255));
		add("door_tile", "art/door", new Color(255, 0, 0));
		add("corner_tile", "art/tile_corner", new Color(1, 1, 255));
		add("floor_tile", "art/floor", new Color(128, 0, 128));
		add("enemy_spawn_tile", "art/editor/enemy_spawn", new Color(255, 1, 1));
		add("player_spawn_tile", "art/editor/player_spawn", new Color(255, 255, 255));
		add("lava_tile", "art/lava/lava_anim_0", new Color(255, 2, 2));
	}

	public void add(String id, String link, Color color) {
		id = id.toLowerCase();
		if (ids.contains(id)) {
			System.out.println("\"" + id + "\" is a taken image id");
			return;
		}
		ids.add(id);
		colors.add(color);
		images.add(imageLoader.loadImage("/" + link + ".png"));
	}

	public BufferedImage get(int index) {
		return images.get(index);
	}

	public BufferedImage get(Color color) {
		if (color != new Color(0, 0, 0, 0)) {
			if (!colors.contains(color)) {
				// System.out.println("\"" + color.getRGB() + "\" is not an image color");
				return null;
			}
			for (int i = 0; i < ids.size(); i++) {
				if (colors.get(i).getRGB() == color.getRGB()) {
					return images.get(i);
				}
			}
		}
		return null;
	}

	public Color getColor(int index) {
		return colors.get(index);
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

	public Color getColor(String id) {
		id = id.toLowerCase();
		if (!ids.contains(id)) {
			System.out.println("\"" + id + "\" is not an image id");
			return null;
		}
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				return colors.get(i);
			}
		}
		return null;
	}
}
