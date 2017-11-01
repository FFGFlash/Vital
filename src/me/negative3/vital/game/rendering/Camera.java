package me.negative3.vital.game.rendering;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;

public class Camera {
	private float x, y, r;
	private boolean v = false, useLimit = true;
	private Game game;

	public Camera(float x, float y, Game game) {
		this.x = x;
		this.y = y;
		this.game = game;
	}

	public void tick(GameObject player) {
		x = (float) (-player.getX() + game.getMain().getWidth() / 2);
		y = (float) (-player.getY() + game.getMain().getHeight() / 2);

		if (r >= .01) {
			v = false;
		} else if (r <= -.01) {
			v = true;
		}

		if (useLimit) {
			if (v) {
				r += .0002 * Math.min(game.getCombo(), 10);
			} else {
				r -= .0002 * Math.min(game.getCombo(), 10);
			}
		} else {
			if (v) {
				r += .0002 * game.getCombo();
			} else {
				r -= .0002 * game.getCombo();
			}
		}

		if (r < 0) {
			r += .0002;
		} else if (r > 0) {
			r -= .0002;
		}
		
		if (r < .01 && r > -.01 && game.getCombo() == 0) {
			r = 0;
		}
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

	public float getR() {
		return r;
	}
}
