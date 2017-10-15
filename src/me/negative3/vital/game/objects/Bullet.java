package me.negative3.vital.game.objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.ObjectId;

public class Bullet extends GameObject {
	protected int mindamage, maxdamage;
	protected ObjectId shotBy;
	protected BufferedImage bullet;

	public Bullet(double x, double y, int width, int height, ObjectId objectId, Game game, double[] vel, int mindamage, int maxdamage, ObjectId shotBy) {
		super(x, y, width, height, objectId, game);
		this.velX = vel[0];
		this.velY = vel[1];
		this.maxdamage = maxdamage;
		this.mindamage = mindamage;
		this.shotBy = shotBy;
		if (shotBy == ObjectId.PLAYER) {
			bullet = game.getResourceHandler().get("player_bullet");
		} else {
			bullet = game.getResourceHandler().get("enemy_bullet");
		}
		zIndex = 98;
	}

	@Override
	public void update() {
		x += velX;
		y += velY;
		if (checkCollision(Collision.BOX, Collision.BOX)) {
			dead = true;
		}
	}

	@Override
	public boolean checkCollision(Collision c1, Collision c2) {
		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (getCollider(c1).intersects(obj.getCollider(c2))) {
				if (obj.getObjectId() != shotBy) {
					if (obj.getObjectId() == ObjectId.NORMAL_TILE || obj.getObjectId() == ObjectId.DOOR_TILE) {
						return true;
					} else if (obj.getObjectId() == ObjectId.ENEMY) {
						Enemy enemy = (Enemy) obj;
						enemy.damage(mindamage, maxdamage);
						for (int ii = game.getObjectHandler().objects.size() - 1; ii >= 0; ii--) {
							GameObject o = game.getObjectHandler().objects.get(ii);
							if (o.getObjectId() == ObjectId.PLAYER) {
								Player player = (Player) o;
								player.addCombo();
								break;
							}
						}
						return true;
					} else if (obj.getObjectId() == ObjectId.PLAYER) {
						Player player = (Player) obj;
						player.damage();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bullet, (int) x, (int) y, width, height, null);
	}

}
