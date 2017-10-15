package me.negative3.vital.game.objects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.ObjectId;
import me.negative3.vital.library.framework.Weapon;
import me.negative3.vital.library.framework.WeaponType;

public class Enemy extends GameObject {
	private int ticks = 0;
	private int[] movement = new int[] { 0, 0 };
	private int movementUpdate = 1;
	private int health = 50;
	protected boolean seePlayer = false;
	protected int[] playerPos = new int[] { 0, 0 };

	protected double gunAngle = 0, inaccuracy = 0;
	protected int weaponCooldown = 0, cooldown, bulletCount, bulletSpeed, minDamage, maxDamage, kills;
	protected WeaponType weaponType;
	protected Weapon weapon = Weapon.AUTO_RIFLE;
	protected BufferedImage[] gun = new BufferedImage[] { null, null };

	public Enemy(double x, double y, int width, int height, ObjectId objectId, Game game) {
		super(x, y, width, height, objectId, game);
		zIndex = 99;
		updateGun(game.getMain().getWidth() / 2, game.getMain().getHeight() / 2);
	}

	public void updateGun(double playerX, double playerY) {
		switch (weapon) {
		case MELEE:
			inaccuracy = 0;
			weaponCooldown = 5;
			weaponType = WeaponType.SINGLE_SHOT;
			bulletCount = 0;
			bulletSpeed = 0;
			minDamage = 5;
			maxDamage = 10;
			gun[0] = null;
			break;
		case PISTOL:
			inaccuracy = .03;
			weaponCooldown = 15;
			weaponType = WeaponType.SINGLE_SHOT;
			bulletCount = 1;
			bulletSpeed = 10;
			minDamage = 5;
			maxDamage = 10;
			gun[0] = null;
			break;
		case AUTO_RIFLE:
			inaccuracy = .05;
			weaponCooldown = 10;
			weaponType = WeaponType.FULL_AUTO;
			bulletCount = 1;
			bulletSpeed = 5;
			minDamage = 10;
			maxDamage = 30;
			gun[0] = null;
			break;
		case SHOTGUN:
			inaccuracy = 2;
			weaponCooldown = 20;
			weaponType = WeaponType.SINGLE_SHOT;
			bulletCount = 5;
			bulletSpeed = 7;
			minDamage = 5;
			maxDamage = 25;
			gun[0] = game.getResourceHandler().get("shotgun");
			break;
		default:
			break;
		}
		if (gun[0] != null) {
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -gun[0].getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			gun[1] = op.filter(gun[0], null);
		} else {
			gun[1] = null;
		}
		gunAngle = Math.atan2(Math.toDegrees(playerY - (y + height / 2)),
				Math.toDegrees(playerX - (x + width / 2) - Math.PI / 2));
	}

	@Override
	public void update() {
		Random rand = new Random();

		for (int ii = game.getObjectHandler().objects.size() - 1; ii >= 0; ii--) {
			GameObject o = game.getObjectHandler().objects.get(ii);
			if (o.getObjectId() == ObjectId.PLAYER) {
				Player player = (Player) o;
				seePlayer = true;
				for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
					GameObject obj = game.getObjectHandler().objects.get(i);
					if (obj.getObjectId() != ObjectId.PLAYER && obj.getObjectId() != ObjectId.ENEMY
							&& obj.getObjectId() != ObjectId.FLOOR_TILE && obj.getObjectId() != ObjectId.BULLET) {
						if (obj.getCollider(Collision.BOX).intersectsLine(x + width / 2, y + height / 2,
								player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2)) {
							seePlayer = false;
						}
					}
				}
				if (seePlayer) {
					updateGun(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
					if (randInt(0, 100) >= 99) {
						attack(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
					}
				}
				if (health <= 0) {
					player.addKill();
					dead = true;
				}
				break;
			}
		}

		x += velX;
		y += velY;

		if (cooldown > 0) {
			cooldown--;
		}

		if (ticks % movementUpdate == 0) {
			movementUpdate = rand.nextInt(120) + 1;
			movement = new int[] { rand.nextInt(3), rand.nextInt(3) };
		}

		if (movement[0] == 0) {
			velX = 0;
		} else if (movement[0] == 1) {
			velX = 3;
		} else if (movement[0] == 2) {
			velX = -3;
		}

		if (movement[1] == 0) {
			velY = 0;
		} else if (movement[1] == 1) {
			velY = 3;
		} else if (movement[1] == 2) {
			velY = -3;
		}

		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (obj.getObjectId() == ObjectId.NORMAL_TILE || obj.getObjectId() == ObjectId.DOOR_TILE) {
				if (checkCollision(Collision.RIGHT, obj, Collision.LEFT)) {
					x = obj.getX() - width;
				} else if (checkCollision(Collision.LEFT, obj, Collision.RIGHT)) {
					x = obj.getX() + obj.getWidth();
				} else if (checkCollision(Collision.BOTTOM, obj, Collision.TOP)) {
					y = obj.getY() - height;
				} else if (checkCollision(Collision.TOP, obj, Collision.BOTTOM)) {
					y = obj.getY() + obj.getHeight();
				}
			}
		}

		ticks++;
	}

	public void attack(double mouseX, double mouseY) {
		if (cooldown == 0) {
			if (weapon == Weapon.MELEE) {

			} else {
				for (int i = 0; i < bulletCount; i++) {
					Bullet bullet = new Bullet((x + width / 2) - 4, (y + height / 2) - 4, 16, 16, ObjectId.BULLET, game,
							getBulletVelocity(mouseX, mouseY), minDamage, maxDamage, objectId);
					game.getObjectHandler().add(bullet);
					game.getLevelHandler().add(bullet);
				}
			}
			cooldown = weaponCooldown;
		}
	}

	private double[] getBulletVelocity(double mouseX, double mouseY) {
		return new double[] { (double) (Math.cos(gunAngle) * bulletSpeed) + randDouble(-inaccuracy, inaccuracy),
				(double) (Math.sin(gunAngle) * bulletSpeed) + randDouble(-inaccuracy, inaccuracy) };
	}

	private double randDouble(double min, double max) {
		if (min > max)
			return 0;
		return Math.floor(min + (Math.random() * (max - min)));
	}

	private int randInt(int min, int max) {
		if (min > max)
			return 0;
		return (int) Math.floor(min + (Math.random() * (max - min)));
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(game.getResourceHandler().get("enemy"), (int) x, (int) y, width, height, null);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = g2d.getTransform();

		g2d.rotate(gunAngle, (x + width / 2), (y + height / 2));

		if (gun[1] != null && gun[0] != null) {
			if (gunAngle > 1.5 && gunAngle <= 3.5 || gunAngle < -1.5 && gunAngle >= -3.5) {
				g.drawImage(gun[1], (int) (x + width / 2), (int) (y + height / 2) - 6, 24, 12, null);
			} else {
				g.drawImage(gun[0], (int) (x + width / 2), (int) (y + height / 2) - 6, 24, 12, null);
			}
		}

		g2d.setTransform(transform);
	}

	public void damage(int mindamage, int maxdamage) {
		int damage = (int) (Math.floor(Math.random() * maxdamage) + mindamage);
		health -= damage;
	}

}
