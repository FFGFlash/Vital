package me.negative3.vital.game.objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.ObjectId;

public class Chest extends GameObject {
	protected LinkedList<BufferedImage[]> animations = new LinkedList<>();
	protected BufferedImage image;
	protected int type, frames = 0, ticks = 0;
	protected boolean open = false, opening = false;

	public Chest(double x, double y, int width, int height, ObjectId objectId, Game game, int type) {
		super(x, y, width, height, objectId, game);
		this.type = type;

		zIndex = 1;

		animations.add(game.getResourceHandler().getFrames("health_chest"));
		animations.add(game.getResourceHandler().getFrames("ammo_chest"));
		animations.add(game.getResourceHandler().getFrames("weapon_chest"));

		image = animations.get(type)[0];
	}

	@Override
	public void update() {
		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (obj.getObjectId() == ObjectId.PLAYER) {
				if (checkCollision(Collision.BOX, obj, Collision.BOX)) {
					opening = true;
				}
			}
		}

		if (ticks % 5 == 0 && opening && !open) {
			frames++;
		}

		if (frames == animations.get(type).length - 1 && opening) {
			open = true;
			opening = false;
		}

		ticks++;
	}

	@Override
	public void paintComponent(Graphics g) {
		image = animations.get(type)[frames];

		g.drawImage(image, (int) x, (int) y, width, height, null);
	}

}
