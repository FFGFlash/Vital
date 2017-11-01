package me.negative3.vital.game.framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import me.negative3.vital.game.Game;
import me.negative3.vital.library.framework.Collision;
import me.negative3.vital.library.framework.ObjectId;

public abstract class GameObject {
	protected double x, y, velX = 0, velY = 0;
	protected boolean dead = false, visible = true, fatMode = false;
	protected ObjectId objectId;
	protected int width, height, zIndex;
	protected Game game;

	public GameObject(double x, double y, int width, int height, ObjectId objectId, Game game) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.objectId = objectId;
		this.game = game;
	}

	public abstract void update();

	public abstract void paintComponent(Graphics g);

	public void paintComponentCollision(Graphics g) {
		g.setColor(Color.GREEN);
		Graphics2D g2d = (Graphics2D) g;
		g2d.draw(getCollider(Collision.TOP));
		g2d.draw(getCollider(Collision.LEFT));
		g2d.draw(getCollider(Collision.BOTTOM));
		g2d.draw(getCollider(Collision.RIGHT));
		g2d.draw(getCollider(Collision.BOX));
	}

	public boolean checkCollision(Rectangle r) {
		if (r.intersects(getCollider(Collision.BOX))) {
			return true;
		}
		return false;
	}

	public boolean checkCollision(Rectangle r1, Rectangle r2) {
		if (r1.intersects(r2)) {
			return true;
		}
		return false;
	}

	public boolean checkCollision(Collision c1, Collision c2) {
		for (GameObject obj : game.getObjectHandler().objects) {
			if (getCollider(c1).intersects(obj.getCollider(c2))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkCollision(Collision c1, GameObject obj, Collision c2) {
		if (getCollider(c1).intersects(obj.getCollider(c2))) {
			return true;
		}
		return false;
	}
	
	public Dimension getColliderSize() {
		return new Dimension(width, height);
	}
	
	public int[] getColliderPosition() {
		return new int[] { (int) x, (int) y };
	}

	public Rectangle getCollider(Collision c) {
		
		int width = getColliderSize().width;
		int height = getColliderSize().height;
		int x = getColliderPosition()[0];
		int y = getColliderPosition()[1];
		
		switch (c) {
		case BOTTOM:
			return new Rectangle((int) (x + 5), (int) (y + height / 2), width - 10, height / 2);
		case BOX:
			return new Rectangle((int) x, (int) y, width, height);
		case LEFT:
			return new Rectangle((int) x, (int) (y + 5), width / 2, height - 10);
		case RIGHT:
			return new Rectangle((int) (x + width / 2), (int) (y + 5), width / 2, height - 10);
		case TOP:
			return new Rectangle((int) (x + 5), (int) y, width - 10, height / 2);
		}
		return null;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		setPos(x, y);
	}

	public void setY(double y) {
		setPos(x, y);
	}

	public void setPos(double x, double y) {
		setPos(x, y, (width * 2 + width * 2), (height * 2 + height * 2));
	}

	public void setPos(double x, double y, int width, int height) {
		this.x = x + (width / 2 - this.width / 2);
		this.y = y + (height / 2 - this.height / 2);
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setVelX(double velX) {
		this.velX = velX;
	}

	public void setVelY(double velY) {
		this.velY = velY;
	}

	public double getVelX() {
		return velX;
	}

	public double getVelY() {
		return velY;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getZIndex() {
		return zIndex;
	}
}
