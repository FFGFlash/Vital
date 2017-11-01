package me.negative3.vital.game.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.handlers.LevelHandler;
import me.negative3.vital.library.framework.ObjectId;

public class Tile extends GameObject {
	private BufferedImage image = null;
	private LevelHandler levelHandler;
	protected LinkedList<BufferedImage> tiles = new LinkedList<>();
	protected BufferedImage[] animation = null;
	private int enemies;
	private boolean active = false;
	protected int ticks = 0, frames = 0;

	public Tile(double x, double y, int width, int height, ObjectId objectId, Game game) {
		super(x, y, width, height, objectId, game);
		levelHandler = game.getLevelHandler();
		enemies = levelHandler.getEnemies();
		tiles.add(game.getResourceHandler().get("tile_0"));
		tiles.add(game.getResourceHandler().get("tile_1"));
		tiles.add(game.getResourceHandler().get("tile_2"));
		tiles.add(game.getResourceHandler().get("tile_3"));
		zIndex = 0;
		switch (objectId) {
		case CORNER_TILE: // Calculate Rotation and Sprite for Corner
			if (checkForTile(1, 0) && checkForTile(-1, 0) && checkForTile(0, -1) && checkForTile(0, 1)) {
				image = tiles.get(randint(0, tiles.size()));
			} else if (checkForTile(-1, 0) && checkForTile(0, 1) && checkForTile(0, -1)) {
				image = game.getResourceHandler().get("squish_tile");
			} else if (checkForTile(1, 0) && checkForTile(0, 1) && checkForTile(0, -1)) {
				image = game.getResourceHandler().get("squish_tile");
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-image.getWidth(null), 0);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			} else if (checkForTile(1, 0) && checkForTile(-1, 0) && checkForTile(0, -1)) {
				image = game.getResourceHandler().get("squish_tile");
				int angle = 90;
				double rads = Math.toRadians(angle), sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
				int w = image.getWidth();
				int h = image.getHeight();
				int newWidth = (int) Math.floor(w * cos + h * sin);
				int newHeight = (int) Math.floor(h * cos + w * sin);
				AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
				tx.translate((newWidth - w) / 2, (newHeight - h) / 2);
				tx.rotate(rads, w / 2, h / 2);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			} else if (checkForTile(1, 0) && checkForTile(-1, 0) && checkForTile(0, 1)) {
				image = game.getResourceHandler().get("squish_tile");
				int angle = -90;
				double rads = Math.toRadians(angle), sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
				int w = image.getWidth();
				int h = image.getHeight();
				int newWidth = (int) Math.floor(w * cos + h * sin);
				int newHeight = (int) Math.floor(h * cos + w * sin);
				AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
				tx.translate((newWidth - w) / 2, (newHeight - h) / 2);
				tx.rotate(rads, w / 2, h / 2);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			} else if (checkForTile(-1, 0) && checkForTile(0, 1)) {
				image = game.getResourceHandler().get("corner_tile");
			} else if (checkForTile(-1, 0) && checkForTile(0, -1)) {
				image = game.getResourceHandler().get("corner_tile");
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -image.getHeight(null));
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			} else if (checkForTile(1, 0) && checkForTile(0, -1)) {
				image = game.getResourceHandler().get("corner_tile");
				AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
				tx.translate(-image.getWidth(null), -image.getHeight(null));
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			} else if (checkForTile(1, 0) && checkForTile(0, 1)) {
				image = game.getResourceHandler().get("corner_tile");
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-image.getWidth(null), 0);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = op.filter(image, null);
			}
			break;
		case DOOR_TILE:
			image = tiles.get(randint(0, tiles.size()));
			break;
		case NORMAL_TILE:
			image = tiles.get(randint(0, tiles.size()));
			break;
		case FLOOR_TILE:
			image = game.getResourceHandler().get("floor_tile");
			break;
		case LAVA_TILE:
			animation = game.getResourceHandler().getFrames("lava");
			break;
		default:
			image = null;
			break;
		}
	}

	private int randint(double min, double max) {
		if (min > max)
			return 0;
		return (int) Math.floor(min + (Math.random() * (max - min)));
	}

	private boolean checkForTile(int xOffset, int yOffset) {
		if (levelHandler.getPixel((int) (x + width / 2) + xOffset * game.getBlockSize(),
				(int) (y + height / 2) + yOffset * game.getBlockSize()) == new Color(0, 0, 255).getRGB()) {
			return true;
		} else if (levelHandler.getPixel((int) (x + width / 2) + xOffset * game.getBlockSize(),
				(int) (y + height / 2) + yOffset * game.getBlockSize()) == new Color(255, 0, 0).getRGB()) {
			return true;
		}
		return false;
	}

	@Override
	public void update() {
		if (objectId == ObjectId.DOOR_TILE) {
			for (GameObject obj : game.getObjectHandler().objects) {
				if (obj.getObjectId() == ObjectId.PLAYER) {
					Player player = (Player) obj;
					if (player.getKills() == enemies) {
						active = true;
						image = game.getResourceHandler().get("door_tile");
					}
				}
			}
		}

		if (ticks % 10 == 0) {
			frames++;
		}

		ticks++;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (animation != null) {
			image = animation[frames % animation.length];
		}

		if (objectId == ObjectId.BUILDER_TILE) {
			g.setColor(Color.PINK);
			g.fillRect((int) x, (int) y, width, height);
		} else if (image != null) {
			g.drawImage(image, (int) x, (int) y, width, height, null);
		} else {
			g.setColor(new Color(frames));
			g.fillRect((int) x, (int) y, width, height);
		}
	}

	public boolean isActive() {
		return active;
	}

}
