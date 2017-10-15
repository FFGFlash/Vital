package me.negative3.vital.game.framework;

import java.awt.Graphics;

import me.negative3.vital.game.Game;

public abstract class GameMenu {
	protected Game game;

	public GameMenu(Game game) {
		this.game = game;
	}

	public abstract void update();

	public abstract void render(Graphics g);
}
