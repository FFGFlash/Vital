package me.negative3.vital.game.handlers;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.library.rendering.BufferedImageLoader;

public class ResourceHandler {
	private BufferedImageLoader imageLoader;
	public LinkedList<BufferedImage> images = new LinkedList<>();
	public LinkedList<String> ids = new LinkedList<>();
	
	public LinkedList<BufferedImage[]> anims = new LinkedList<>();
	public LinkedList<String> animIds = new LinkedList<>();

	public ResourceHandler() {
		imageLoader = new BufferedImageLoader();
		add("background_0", "art/backgrounds/bg_red");
		add("background_1", "art/backgrounds/bg_green");
		add("background_2", "art/backgrounds/bg_blue");
		add("shotgun", "art/weapons/shotgun");
		add("blade", "art/weapons/blade");
		add("pistol", "art/weapons/handgun");
		add("rifle", "art/weapons/rifle");
		add("slugger", "art/weapons/slugger");
		add("smg", "art/weapons/smg");
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
		add("level_blank", "levels/blank");
		add("heart", "art/heart");
		add("no_heart", "art/noheart");
		add("cursor", "art/reticle");
		
		addFrames("lava", "art/lava/lava_anim", 7);
		
		addFrames("player_idle", "art/player/player_idle", 4);
		addFrames("player_run", "art/player/player_run", 5);
		addFrames("player_switchweapon", "art/player/player_switchweapon", 9);
		addFrames("player_pickup", "art/player/pickup_pickup", 10);
		
		addFrames("ammo_chest", "art/chests/ammo/anim_ammo", 7);
		addFrames("health_chest", "art/chests/stimpak/anim_stimpak", 7);
		addFrames("weapon_chest", "art/chests/weapon/anim_weapon", 7);
		
		addFrames("title_buttons", "art/title/text", 10);
		addFrames("title_graphics", "art/title/graphic", 4);
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

	public void addFrames(String id, String link, int frames) {
		BufferedImage[] f = new BufferedImage[frames];
		id = id.toLowerCase();
		if (animIds.contains(id)) {
			System.out.println("\"" + id + "\" is a taken anim id");
			return;
		}
		for (int i = 0; i < frames; i++) {
			f[i] = imageLoader.loadImage("/" + link + "_" + i + ".png");
		}
		animIds.add(id);
		anims.add(f);
	}
	
	public BufferedImage[] getFrames(int index) {
		return anims.get(index);
	}
	
	public BufferedImage[] getFrames(String id) {
		id = id.toLowerCase();
		if (!animIds.contains(id)) {
			System.out.println("\"" + id + "\" is not an image id");
			return null;
		}
		for (int i = 0; i < animIds.size(); i++) {
			if (animIds.get(i) == id) {
				return anims.get(i);
			}
		}
		return null;
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
