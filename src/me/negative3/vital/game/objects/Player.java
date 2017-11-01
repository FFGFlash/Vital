package me.negative3.vital.game.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameItem;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.items.WeaponItem;
import me.negative3.vital.library.framework.BulletType;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.Facing;
import me.negative3.vital.library.framework.ObjectId;
import me.negative3.vital.library.framework.Weapon;
import me.negative3.vital.library.framework.WeaponType;

public class Player extends GameObject {

	protected double angle = 0;
	protected int totalKills, kills, health, maxHealth, frames = 0, state = 0, ticks = 0;

	protected int equipedBob = 0;
	protected boolean equipedBobUp = true;

	protected Facing facing = Facing.RIGHT;

	protected LinkedList<GameItem> inventory = new LinkedList<>();

	protected ArrayList<BufferedImage[]> animationsRight = new ArrayList<>();
	protected ArrayList<BufferedImage[]> animationsLeft = new ArrayList<>();

	protected int combo = 0, comboTimer = 0, resetTime = 240, invincibleTimer = 0;
	protected boolean invnicible = false;
	protected BufferedImage image;

	protected GameItem equiped;

	public Player(double x, double y, int width, int height, ObjectId objectId, Game game) {
		super(x, y, width, height, objectId, game);
		maxHealth = 4;
		health = maxHealth;
		zIndex = 100;

		equiped = new WeaponItem(game, 48, game.getResourceHandler().get("shotgun"), Weapon.SHOTGUN,
				WeaponType.SINGLE_SHOT, BulletType.PLAYER);

		addAnimation(game.getResourceHandler().getFrames("player_idle"));
		addAnimation(game.getResourceHandler().getFrames("player_run"));
		addAnimation(game.getResourceHandler().getFrames("player_switchweapon"));
		addAnimation(game.getResourceHandler().getFrames("player_pickup"));

		image = animationsRight.get(state)[frames];
	}

	public void addAnimation(BufferedImage[] frames) {

		BufferedImage[] left = new BufferedImage[frames.length];

		animationsRight.add(frames);

		for (int ii = 0; ii < frames.length; ii++) {
			left[ii] = frames[ii];
			BufferedImage temp = left[ii];
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-temp.getWidth(), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			temp = op.filter(temp, null);
			left[ii] = temp;
		}

		animationsLeft.add(left);
	}
	
	private int randInt(int min, int max) {
		if (min > max)
			return 0;
		return (int) Math.floor(min + (Math.random() * (max - min)));
	}

	@Override
	public void update() {
		height = (int) Math.round(width * (1.0 * image.getHeight() / image.getWidth()));

		equiped.update();

		if (ticks % 6 == 0 && state == 0 || ticks % 5 == 0 && state == 1 || ticks % 5 == 0 && state == 2
				|| ticks % 5 == 0 && state == 3) {
			frames++;
			if (state == 1) {
				game.getAudioHandler().play("footstep_0", false, .01f);
			}
		}

		if (health <= 0) {
			dead = true;
		} else if (health > maxHealth) {
			health = maxHealth;
		}

		game.setColorStage(totalKills + kills % 3);
		x += velX;
		y += velY;

		if (velX != 0 || velY != 0) {
			state = 1;
		} else {
			state = 0;
		}

		if (velX < 0) {
			facing = Facing.LEFT;
		} else if (velX > 0) {
			facing = Facing.RIGHT;
		}

		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (obj.getObjectId() == ObjectId.NORMAL_TILE) {
				if (checkCollision(Collision.RIGHT, obj, Collision.LEFT)) {
					x = obj.getColliderPosition()[0] - getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.LEFT, obj, Collision.RIGHT)) {
					x = obj.getColliderPosition()[0] + obj.getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.BOTTOM, obj, Collision.TOP)) {
					y = obj.getColliderPosition()[1] - getColliderSize().height - (getColliderPosition()[1] - y);
				} else if (checkCollision(Collision.TOP, obj, Collision.BOTTOM)) {
					y = obj.getColliderPosition()[1] + obj.getColliderSize().height - (getColliderPosition()[1] - y);
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
					x = obj.getColliderPosition()[0] - getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.LEFT, obj, Collision.RIGHT)) {
					x = obj.getColliderPosition()[0] + obj.getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.BOTTOM, obj, Collision.TOP)) {
					y = obj.getColliderPosition()[1] - getColliderSize().height - (getColliderPosition()[1] - y);
				} else if (checkCollision(Collision.TOP, obj, Collision.BOTTOM)) {
					y = obj.getColliderPosition()[1] + obj.getColliderSize().height - (getColliderPosition()[1] - y);
				}
			} else if (obj.getObjectId() == ObjectId.LAVA_TILE) {
				if (!invnicible) {
					if (checkCollision(Collision.BOX, obj, Collision.BOX)) {
						invincibleTimer = 20;
						invnicible = true;
						health--;
					}
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

		if (invincibleTimer > 0) {
			invincibleTimer--;
		} else {
			invnicible = false;
		}

		ticks++;
	}

	public void updateAngle(double mouseX, double mouseY) {
		angle = Math.atan2(Math.toDegrees(mouseY - (y + height / 2)),
				Math.toDegrees(mouseX - (x + width / 2) - Math.PI / 2));
	}

	public void onUse(double mouseX, double mouseY) {
		equiped.onUse(this, mouseX, mouseY, angle);
	}

	@Override
	public int[] getColliderPosition() {
		return new int[] { (int) x + 25, (int) y + 12 };
	}

	@Override
	public Dimension getColliderSize() {
		return new Dimension(width - 50, height - 27);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (facing == Facing.RIGHT)
			image = animationsRight.get(state)[frames % animationsRight.get(state).length];
		else if (facing == Facing.LEFT)
			image = animationsLeft.get(state)[frames % animationsLeft.get(state).length];

		g.drawImage(image, (int) x, (int) y, width, height, null);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = g2d.getTransform();

		g2d.rotate(angle, (x + width / 2), (y + height / 2));

		BufferedImage[] equipedSprite = equiped.getSprite();

		equipedBob = (frames % animationsRight.get(state).length) + animationsRight.get(state).length / 2;

		if (angle > 1.5 && angle <= 3.5 || angle < -1.5 && angle >= -3.5) {
			g.drawImage(equipedSprite[1], (int) (x + width / 2),
					(int) (y + height / 2 + equipedBob) - equiped.getHeight() / 2, equiped.getWidth(),
					equiped.getHeight(), null);
		} else {
			g.drawImage(equipedSprite[0], (int) (x + width / 2),
					(int) (y + height / 2 + equipedBob) - equiped.getHeight() / 2, equiped.getWidth(),
					equiped.getHeight(), null);
		}

		g2d.setTransform(transform);
	}

	public GameItem getEquiped() {
		return equiped;
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
		equiped = new WeaponItem(game, 48, game.getResourceHandler().get("shotgun"), Weapon.SHOTGUN,
				WeaponType.SINGLE_SHOT, BulletType.PLAYER);
	}

	public void makeInvincible(int timer) {
		invnicible = true;
		invincibleTimer = timer;
	}

}
