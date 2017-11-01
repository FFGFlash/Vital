package me.negative3.vital.game.handlers;

import java.awt.Graphics;
import java.util.LinkedList;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.objects.Player;
import me.negative3.vital.library.framework.GameState;
import me.negative3.vital.library.framework.ObjectId;

public class ObjectHandler {
	public LinkedList<GameObject> objects = new LinkedList<>();
	private int[] order = new int[objects.size()];
	private boolean escape = false;
	private Game game;

	public ObjectHandler(Game game) {
		this.game = game;
	}

	public void sort() {
		try {
			int[] k = new int[objects.size()];
			order = new int[objects.size()];
			for (int i = objects.size() - 1; i >= 0; i--) {
				if (i < objects.size() || i >= 0) {
					k[i] = objects.get(i).getZIndex();
					order[i] = i;
				}
			}
			int temp1;
			int temp2;
			for (int a = 0; a < k.length - 1; a++) {
				for (int b = 0; b < k.length - 1; b++) {
					if (k[b] < k[b + 1]) {
						temp1 = k[b];
						temp2 = order[b];
						order[b] = order[b + 1];
						k[b] = k[b + 1];
						order[b + 1] = temp2;
						k[b + 1] = temp1;
					}
				}
			}
		} catch (Exception e) {
			
		}
	}

	public void update() {
		for (int i = objects.size() - 1; i >= 0; i--) {
			if (i < objects.size() || i >= 0) {
				if (escape)
					break;
				GameObject obj = objects.get(i);
				obj.update();
				if (obj.checkCollision(game.getRenderingBounds())) {
					obj.setVisible(true);
				} else {
					obj.setVisible(false);
				}
				if (obj.isDead() && obj.getObjectId() != ObjectId.PLAYER) {
					remove(i);
				} else if (obj.isDead() && obj.getObjectId() == ObjectId.PLAYER) {
					Player player = (Player) obj;
					game.resetLevel();
					player.reset();
					player.setDead(false);
					game.setState(GameState.MENU);
				}
			}
		}
		if (escape)
			escape = false;
		sort();
	}

	public void paintComponent(Graphics g) {
		for (int i = order.length - 1; i >= 0; i--) {
			if (i < order.length || order[i] < objects.size() || order[i] >= 0 || i >= 0) {
				if (escape)
					break;
				GameObject obj = objects.get(order[i]);
				if (obj.isVisible()) {
					obj.paintComponent(g);
					if (game.isDebug()) {
						obj.paintComponentCollision(g);
					}
				}
			}
		}
	}

	public void add(GameObject obj) {
		objects.add(obj);
	}

	public void remove(GameObject obj) {
		objects.remove(obj);
	}

	public void remove(int id) {
		objects.remove(id);
	}

	public void escape() {
		this.escape = true;
	}

	public boolean isEscape() {
		return escape;
	}
}
