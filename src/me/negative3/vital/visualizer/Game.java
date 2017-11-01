package me.negative3.vital.visualizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JPanel;

public class Game extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = -8505533060376678213L;
	private Main main;
	private Thread thread;
	private boolean running = false, vsync = true;
	private int moves, pixels[], builder[] = { 5, 5 }, spawnCoords[] = { 0, 0 }, stage = 0;
	private BufferedImage level;

	private int width = 64, height = 64;

	public Game(Main main) {
		main.add(this);
		this.main = main;
		addKeyListener(this);
		setFocusable(true);
		level = new BufferedImage(width + 4, height + 4, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) level.getRaster().getDataBuffer()).getData();

		moves = randInt(400, 800);
		if (width * height / 2 > 800) {
			moves = randInt(400, width * height / 2);
		}

		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				pixels[x + y * level.getWidth()] = new Color(0, 0, 0).getRGB();
			}
		}
	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double amountOfTicks = 60.0, ns = 1000000000 / amountOfTicks, delta = 0;
		int updates = 0, frames = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			boolean render = false;

			while (delta >= 1) {
				update();
				updates++;
				delta--;
				render = true;
			}
			if (vsync && render || !vsync) {
				repaint();
				frames++;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + ", TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	private int randInt(int min, int max) {
		max++;
		return (int) Math.floor((Math.random() * (max - min)) + min);
	}

	/*
	 * Stage 0: Place Walls Stage 1: Place Wall Corners *Done separately to prevent
	 * problems Stage 2: Place Player Spawn
	 */

	private void update() {
		if (stage == -1) {
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					pixels[x + y * level.getWidth()] = new Color(0, 0, 0).getRGB();
				}
			}
			moves = (int) Math.floor((Math.random() * (800 - 400)) + 400);
			builder = new int[] { 3, 3 };
			spawnCoords = new int[] { 0, 0 };
			stage++;
		} else if (moves > 0 && stage == 0) {
			int axis = randInt(0, 1), dir = randInt(0, 1), choices[] = { -1, 1 };

			if (axis == 0) {
				System.out.println("Moved " + choices[dir] + " on the X-Axis.");
			} else if (axis == 1) {
				System.out.println("Moved " + choices[dir] + " on the Y-Axis.");
			}

			builder[axis] += choices[dir]; // Moves randomly on either the x or y axis and then positively or
			// negatively

			// Check if x is out of bounds
			if (builder[0] > level.getWidth() - 1 - 2) {
				builder[0] = level.getWidth() - 1 - 2;
			} else if (builder[0] < 2) {
				builder[0] = 2;
			}

			// Check if y is out of bounds
			if (builder[1] > level.getHeight() - 1 - 2) {
				builder[1] = level.getHeight() - 1 - 2;
			} else if (builder[1] < 2) {
				builder[1] = 2;
			}

			// Place floor tile color codes at builder x and builder y
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (y == builder[1] && x == builder[0]) {
						pixels[x + y * level.getWidth()] = new Color(128, 0, 128).getRGB();
					}
				}
			}
			moves--;
		} else if (stage == 0) {
			stage++;
		} else if (stage == 1) {
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x != level.getWidth() - 1 && x != 0) {
						if (pixels[x + y * level.getWidth()] == new Color(0, 0, 0).getRGB()) {
							if (pixels[(x + 1) + y * level.getWidth()] == new Color(128, 0, 128).getRGB()
									|| pixels[(x - 1) + y * level.getWidth()] == new Color(128, 0, 128).getRGB()) {
								builder[0] = x;
								builder[1] = y;
							}
						}
					}
				}
			}

			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x == builder[0] && y == builder[1]) {
						pixels[x + y * level.getWidth()] = new Color(0, 0, 255).getRGB();
					}
				}
			}

			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (y != level.getHeight() - 1 && y != 0) {
						if (pixels[x + y * level.getWidth()] == new Color(0, 0, 0).getRGB()) {
							if (pixels[x + (y + 1) * level.getWidth()] == new Color(128, 0, 128).getRGB()
									|| pixels[x + (y - 1) * level.getWidth()] == new Color(128, 0, 128).getRGB()) {
								builder[0] = x;
								builder[1] = y;
							}
						}
					}
				}
			}

			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x == builder[0] && y == builder[1]) {
						pixels[x + y * level.getWidth()] = new Color(0, 0, 255).getRGB();
					}
				}
			}

			boolean found = false;
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (pixels[x + y * level.getWidth()] == new Color(128, 0, 128).getRGB()) {
						if (pixels[x + (y - 1) * level.getWidth()] == new Color(0, 0, 255).getRGB()
								&& pixels[(x - 1) + y * level.getWidth()] == new Color(0, 0, 255).getRGB()) {
							stage++;
						}
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
		} else if (stage == 2) {
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (y != level.getHeight() - 1 && y != 0 && x != level.getWidth() - 1 && x != 0) {
						if (pixels[x + y * level.getWidth()] == new Color(0, 0, 0).getRGB()) {
							if (pixels[(x + 1) + (y + 1) * level.getWidth()] == new Color(128, 0, 128).getRGB()
									|| pixels[(x + 1) + (y - 1) * level.getWidth()] == new Color(128, 0, 128).getRGB()
									|| pixels[(x - 1) + (y - 1) * level.getWidth()] == new Color(128, 0, 128).getRGB()
									|| pixels[(x - 1) + (y + 1) * level.getWidth()] == new Color(128, 0, 128)
											.getRGB()) {
								builder[0] = x;
								builder[1] = y;
							}
						}
					}
				}
			}

			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x == builder[0] && y == builder[1]) {
						pixels[x + y * level.getWidth()] = new Color(1, 1, 255).getRGB();
					}
				}
			}

			boolean found = false;
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (pixels[x + y * level.getWidth()] == new Color(128, 0, 128).getRGB()) {
						if (pixels[(x - 1) + (y - 1) * level.getWidth()] == new Color(1, 1, 255).getRGB()) {
							stage++;
						}
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
		} else if (stage == 3) {
			// Get spawn coordinates
			boolean found = false;
			if (spawnCoords[0] == 0 && spawnCoords[1] == 0) {
				for (int x = 0; x < level.getWidth(); x++) {
					for (int y = 0; y < level.getHeight(); y++) {
						if (pixels[x + y * level.getWidth()] == new Color(128, 0, 128).getRGB()) {
							pixels[x + y * level.getWidth()] = new Color(255, 255, 255).getRGB();
							spawnCoords = new int[] { x, y };
							found = true;
							stage++;
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}
		} else if (stage == 4) {
			boolean next = false;
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x == level.getWidth()-1 && y == level.getHeight()-1) {
						next = true;
					}
					if (pixels[x + y * level.getWidth()] == new Color(128, 0, 128).getRGB()
							&& ((x >= spawnCoords[0] + 5 || x <= spawnCoords[0] - 5)
									|| (y >= spawnCoords[1] + 5 || y <= spawnCoords[1] - 5))) {
						if (randInt(0, 100) >= 98) {
							pixels[x + y * level.getWidth()] = new Color(255, 1, 1).getRGB();
						}
					}
				}
			}
			if (next) {
				stage++;
			}
		} else if (stage == 5) {
			int[] doorCoords = new int[] { 0, 0 };
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (pixels[x + y * level.getWidth()] == new Color(0, 0, 255).getRGB()) {
						doorCoords = new int[] { x, y };
					}
				}
			}

			// Place exit door
			for (int x = 0; x < level.getWidth(); x++) {
				for (int y = 0; y < level.getHeight(); y++) {
					if (x == doorCoords[0] && y == doorCoords[1]) {
						pixels[x + y * level.getWidth()] = new Color(255, 0, 0).getRGB();
						stage++;
						break;
					}
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, main.getWidth(), main.getHeight());

		g.drawImage(level, (main.getWidth() / 2) - (level.getWidth() / 2 * 16),
				(main.getHeight() / 2) - (level.getHeight() / 2 * 16), level.getWidth() * 16, level.getHeight() * 16,
				null);
		g.setColor(Color.WHITE);
		g.drawRect((main.getWidth() / 2) - (level.getWidth() / 2 * 16),
				(main.getHeight() / 2) - (level.getHeight() / 2 * 16), level.getWidth() * 16, level.getHeight() * 16);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if (key == KeyEvent.VK_R) {
			stage = -1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
