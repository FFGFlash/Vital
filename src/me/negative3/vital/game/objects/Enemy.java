package me.negative3.vital.game.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

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

public class Enemy extends GameObject {
	private int ticks = 0, frames = 0, state = 0;
	private int[] movement = new int[] { 0, 0 };
	private int movementUpdate = 1;
	private int health = 50;
	protected boolean seePlayer = false;
	protected int[] playerPos = new int[] { 0, 0 };
	
	protected ArrayList<BufferedImage[]> animationsRight = new ArrayList<>();
	protected ArrayList<BufferedImage[]> animationsLeft = new ArrayList<>();
	protected BufferedImage image;
	protected Facing facing = Facing.RIGHT;

	protected double angle = 0;
	
	protected GameItem equiped;

	public Enemy(double x, double y, int width, int height, ObjectId objectId, Game game) {
		super(x, y, width, height, objectId, game);
		zIndex = 99;
		equiped = new WeaponItem(game, 48, game.getResourceHandler().get("smg"), Weapon.AUTO_RIFLE, WeaponType.FULL_AUTO, BulletType.ENEMY);
		updateAngle(game.getMain().getWidth() / 2, game.getMain().getHeight() / 2);
		
		addAnimation(game.getResourceHandler().getFrames("player_idle"));
		addAnimation(game.getResourceHandler().getFrames("player_run"));

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

	public void updateAngle(double playerX, double playerY) {
		angle = Math.atan2(Math.toDegrees(playerY - (y + height / 2)),
				Math.toDegrees(playerX - (x + width / 2) - Math.PI / 2));
	}

	@Override
	public void update() {
		Random rand = new Random();
		
		if (ticks % 4 == 0 && state == 0 || ticks % 5 == 0 && state == 1) {
			frames++;
		}
		
		equiped.update();

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
					updateAngle(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
					if (randInt(0, 100) >= 99) {
						onUse(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
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

		if (ticks % movementUpdate == 0) {
			movementUpdate = rand.nextInt(120) + 1;
			movement = new int[] { rand.nextInt(3), rand.nextInt(3) };
		}

		if (movement[0] == 0) {
			velX = 0;
		} else if (movement[0] == 1) {
			velX = 6;
		} else if (movement[0] == 2) {
			velX = -6;
		}

		if (movement[1] == 0) {
			velY = 0;
		} else if (movement[1] == 1) {
			velY = 6;
		} else if (movement[1] == 2) {
			velY = -6;
		}

		for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
			GameObject obj = game.getObjectHandler().objects.get(i);
			if (obj.getObjectId() == ObjectId.NORMAL_TILE || obj.getObjectId() == ObjectId.DOOR_TILE) {
				if (checkCollision(Collision.RIGHT, obj, Collision.LEFT)) {
					x = obj.getColliderPosition()[0] - getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.LEFT, obj, Collision.RIGHT)) {
					x = obj.getColliderPosition()[0] + obj.getColliderSize().width - (getColliderPosition()[0] - x);
				} else if (checkCollision(Collision.BOTTOM, obj, Collision.TOP)) {
					y = obj.getColliderPosition()[1] - getColliderSize().height - (getColliderPosition()[1] - y);
				} else if (checkCollision(Collision.TOP, obj, Collision.BOTTOM)) {
					y = obj.getColliderPosition()[1] + obj.getColliderSize().height - (getColliderPosition()[1] - y);
				}
			}
		}

		ticks++;
	}
	
	@Override
	public int[] getColliderPosition() {
		return new int[] { (int) x + 25, (int) y + 12 };
	}

	@Override
	public Dimension getColliderSize() {
		return new Dimension(width - 50, height - 27);
	}

	public void onUse(double mouseX, double mouseY) {
		equiped.onUse(this, mouseX, mouseY, angle);
	}

	private int randInt(int min, int max) {
		if (min > max)
			return 0;
		return (int) Math.floor(min + (Math.random() * (max - min)));
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
		
		if (angle > 1.5 && angle <= 3.5 || angle < -1.5 && angle >= -3.5) {
			g.drawImage(equipedSprite[1], (int) (x + width / 2), (int) (y + height / 2) - equiped.getHeight() / 2, equiped.getWidth(), equiped.getHeight(),
					null);
		} else {
			g.drawImage(equipedSprite[0], (int) (x + width / 2), (int) (y + height / 2) - equiped.getHeight() / 2, equiped.getWidth(), equiped.getHeight(),
					null);
		}

		g2d.setTransform(transform);
	}

	public void damage(int mindamage, int maxdamage) {
		int damage = (int) (Math.floor(Math.random() * maxdamage) + mindamage);
		health -= damage;
	}

}
