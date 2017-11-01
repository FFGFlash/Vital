package me.negative3.vital.game.framework;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import me.negative3.vital.game.Game;
import me.negative3.vital.library.framework.ObjectId;

public abstract class GameItem {
	protected ObjectId objectId;
	protected int width, height;
	protected BufferedImage[] sprite = new BufferedImage[] { null, null };
	protected Game game;
	
	public GameItem(Game game, int size, BufferedImage sprite, ObjectId objectId) {
		this.game = game;
		this.objectId = objectId;
		this.width = size;
		this.height = (int) Math.round(size * (1.0 * sprite.getHeight() / sprite.getWidth()));
		this.sprite[0] = sprite;
		
		if (this.sprite[0] != null) {
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -this.sprite[0].getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			this.sprite[1] = op.filter(this.sprite[0], null);
		} else {
			this.sprite[1] = null;
		}
	}
	
	public abstract void update();
	
	public abstract void onUse(GameObject usedBy, double mouseX, double mouseY, double angle);
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public BufferedImage[] getSprite() {
		return sprite;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
