package me.negative3.vital.game.objects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.ObjectId;
import me.negative3.vital.library.framework.Weapon;
import me.negative3.vital.library.framework.WeaponType;

public class Player extends GameObject {

	protected double gunAngle = 0, inaccuracy = 0;
	protected int weaponCooldown = 0, cooldown, bulletCount, bulletSpeed, minDamage, maxDamage, totalKills, kills,
			health, maxHealth;
	protected WeaponType weaponType;
	protected Weapon weapon = Weapon.SHOTGUN;
	protected BufferedImage[] gun = new BufferedImage[] { null, null };
	protected int combo = 0, comboTimer = 0, resetTime = 240;

	public Player(double x, double y, int width, int height, ObjectId objectId, Game game) {
		super(x, y, width, height, objectId, game);
		maxHealth = 4;
		health = maxHealth;
		zIndex = 100;
	}

	@Override
	public void update() {
		
		if (health <= 0) {
			dead = true;
		} else if (health > maxHealth) {
			health = maxHealth;
		}
		
		game.setColorStage(totalKills + kills % 3);
		x += velX;
		y += velY;
		if (cooldown > 0) {
			cooldown--;
		}

		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (obj.getObjectId() == ObjectId.NORMAL_TILE) {
				if (checkCollision(Collision.RIGHT, obj, Collision.LEFT)) {
					x = obj.getX() - width;
				} else if (checkCollision(Collision.LEFT, obj, Collision.RIGHT)) {
					x = obj.getX() + obj.getWidth();
				} else if (checkCollision(Collision.BOTTOM, obj, Collision.TOP)) {
					y = obj.getY() - height;
				} else if (checkCollision(Collision.TOP, obj, Collision.BOTTOM)) {
					y = obj.getY() + obj.getHeight();
				}
			} else if (obj.getObjectId() == ObjectId.DOOR_TILE) {
				Tile tile = (Tile) obj;
				if (tile.isActive()) {
					if (checkCollision(Collision.BOX, obj, Collision.BOX)) {
						totalKills += kills;
						kills = 0;
						game.nextLevel();
					}
				}
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
		if (combo > 0) {
			comboTimer++;
			if (comboTimer % resetTime == 0) {
				combo = 0;
				comboTimer = 0;
			}
		} else if (comboTimer > 0) {
			comboTimer = 0;
		}
		
	}

	public void updateGun(double mouseX, double mouseY) {
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
			bulletSpeed = 10;
			minDamage = 10;
			maxDamage = 30;
			gun[0] = null;
			break;
		case SHOTGUN:
			inaccuracy = 2;
			weaponCooldown = 20;
			weaponType = WeaponType.SINGLE_SHOT;
			bulletCount = 5;
			bulletSpeed = 20;
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
		gunAngle = Math.atan2(Math.toDegrees(mouseY - (y + height / 2)),
				Math.toDegrees(mouseX - (x + width / 2) - Math.PI / 2));
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

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(game.getResourceHandler().get("player"), (int) x, (int) y, width, height, null);
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

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void addKill() {
		kills++;
	}

	public void addCombo() {
		combo++;
		comboTimer = 0;
	}

	public int getCombo() {
		return combo;
	}

	public void damage() {
		combo = 0;
		comboTimer = 0;
		health--;
	}

	public int getKills() {
		return kills;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public void reset() {
		health = maxHealth;
		kills = 0;
		totalKills = 0;
		combo = 0;
	}

}
