package me.negative3.vital.editor.rendering;

import me.negative3.vital.editor.Game;

public class Camera {
	private float x, y;
	private Game game;

	public Camera(float x, float y, Game game) {
		this.x = x;
		this.y = y;
		this.game = game;
	}

	public void update(double xx, double yy) {
		x = (float) (xx + game.getMain().getWidth() / 2);
		y = (float) (yy + game.getMain().getHeight() / 2);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
}
