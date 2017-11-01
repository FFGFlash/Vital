package me.negative3.vital.game.items;

import java.awt.image.BufferedImage;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameItem;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.objects.Bullet;
import me.negative3.vital.library.framework.BulletType;
import me.negative3.vital.library.framework.ObjectId;
import me.negative3.vital.library.framework.Weapon;
import me.negative3.vital.library.framework.WeaponType;

public class WeaponItem extends GameItem {
	protected Weapon weapon;
	protected WeaponType weaponType;
	protected int weaponCooldown = 0, cooldown = 0, bulletCount, bulletSpeed, minDamage, maxDamage, bulletSize;
	protected double inaccuracy;
	
	public WeaponItem(Game game, int size, BufferedImage sprite, Weapon weapon, WeaponType weaponType, BulletType bulletType) {
		super(game, size, sprite, ObjectId.WEAPON);
		this.weapon = weapon;
		this.weaponType = weaponType;
		
		switch (bulletType) {
		case ENEMY:
			bulletSize = 32;
			break;
		case PLAYER:
			bulletSize = 16;
			break;
		default:
			break;
		}
		
		switch (weapon) {
		case MELEE:
			inaccuracy = 0;
			weaponCooldown = 5;
			bulletCount = 0;
			bulletSpeed = 0;
			minDamage = 5;
			maxDamage = 10;
			break;
		case PISTOL:
			inaccuracy = .03;
			weaponCooldown = 15;
			bulletCount = 1;
			bulletSpeed = 20;
			minDamage = 5;
			maxDamage = 10;
			break;
		case AUTO_RIFLE:
			inaccuracy = .05;
			weaponCooldown = 10;
			bulletCount = 1;
			bulletSpeed = 20;
			minDamage = 10;
			maxDamage = 30;
			break;
		case SHOTGUN:
			inaccuracy = 2;
			weaponCooldown = 20;
			bulletCount = 5;
			bulletSpeed = 25;
			minDamage = 5;
			maxDamage = 25;
			break;
		default:
			break;
		}
	}

	@Override
	public void update() {
		if (cooldown > 0) {
			cooldown--;
		}
	}

	@Override
	public void onUse(GameObject usedBy, double mouseX, double mouseY, double angle) {
		double delimiter = 0;
		if (usedBy.getObjectId() == ObjectId.ENEMY) {
			delimiter = 10;
		}
		
		if (cooldown == 0) {
			if (weapon == Weapon.MELEE) {

			} else {
				for (int i = 0; i < bulletCount; i++) {
					Bullet bullet = new Bullet((usedBy.getX() + usedBy.getWidth() / 2) - bulletSize / 2, (usedBy.getY() + usedBy.getWidth() / 2) - bulletSize / 2, bulletSize, bulletSize, ObjectId.BULLET, game,
							getBulletVelocity(mouseX, mouseY, angle, delimiter), minDamage, maxDamage, usedBy.getObjectId());
					game.getObjectHandler().add(bullet);
					game.getLevelHandler().add(bullet);
				}
			}
			cooldown = weaponCooldown;
		}
	}
	
	private double[] getBulletVelocity(double mouseX, double mouseY, double angle, double delimiter) {
		return new double[] { (double) (Math.cos(angle) * (bulletSpeed - delimiter)) + randDouble(-inaccuracy, inaccuracy),
				(double) (Math.sin(angle) * (bulletSpeed - delimiter)) + randDouble(-inaccuracy, inaccuracy) };
	}

	private double randDouble(double min, double max) {
		if (min > max)
			return 0;
		return Math.floor(min + (Math.random() * (max - min)));
	}
	
	public WeaponType getWeaponType() {
		return weaponType;
	}
}
