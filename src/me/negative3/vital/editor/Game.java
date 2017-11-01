package me.negative3.vital.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import me.negative3.vital.editor.rendering.Camera;
import me.negative3.vital.editor.handlers.ResourceHandler;

public class Game extends JPanel
		implements Runnable, KeyListener, MouseInputListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = -4464038964496035738L;
	private Main main;
	private Thread thread;
	private Camera camera;
	private ResourceHandler rh;
	private boolean running = false, vsync = true, keys[] = new boolean[5000], buttons[] = new boolean[5000];
	private int mousePosition[] = { 0, 0 }, blockSize = 64, pixels[], selected = 0, levelSize[] = { 32, 32 }, ticks = 0;
	private double position[] = { 0, 0 };
	private BufferedImage level, selectedTile;
	private Color selectedColor;

	public Game(Main main) {
		main.add(this);
		this.main = main;
		camera = new Camera(0, 0, this);
		rh = new ResourceHandler();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setFocusable(true);
		level = new BufferedImage(levelSize[0], levelSize[1], BufferedImage.TYPE_INT_ARGB);
		pixels = new int[level.getWidth() * level.getHeight()];
		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				pixels[x + y * level.getWidth()] = level.getRGB(x, y);
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

	public synchronized void stop() {
		if (!running)
			return;
		try {
			running = false;
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double amountOfTicks = 60.0, ns = 1000000000 / amountOfTicks, delta = 0;
		int updates = 0, frames = 0;
		boolean limit = true;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			boolean render = false;

			if (limit) {
				while (delta >= 1) {
					update();
					updates++;
					delta--;
					render = true;
				}
			} else {
				update();
				updates++;
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

	public void update() {
		if (selectedTile != rh.get(selected % rh.ids.size())) {
			selectedTile = rh.get(selected % rh.ids.size());
			selectedColor = rh.getColor(selected % rh.ids.size());
			int[] pixels = new int[selectedTile.getWidth() * selectedTile.getHeight()];
			for (int x = 0; x < selectedTile.getWidth(); x++) {
				for (int y = 0; y < selectedTile.getHeight(); y++) {
					Color color = new Color(selectedTile.getRGB(x, y));
					if (color.getAlpha() > 128) {
						pixels[x + y * selectedTile.getWidth()] = new Color(color.getRed(), color.getGreen(),
								color.getBlue(), 100).getRGB();
					} else {
						pixels[x + y * selectedTile.getWidth()] = new Color(color.getRed(), color.getGreen(),
								color.getBlue(), color.getAlpha()).getRGB();
					}
				}
			}
			selectedTile.setRGB(0, 0, selectedTile.getWidth(), selectedTile.getHeight(), pixels, 0,
					selectedTile.getWidth());
		}

		camera.update(position[0], position[1]);

		boolean[] scale = { keys[KeyEvent.VK_UP], keys[KeyEvent.VK_LEFT], keys[KeyEvent.VK_DOWN],
				keys[KeyEvent.VK_RIGHT] };

		if (ticks % 5 == 0 && (scale[0] || scale[1] || scale[2] || scale[3])) {

			int newLevelSize[] = { levelSize[0], levelSize[1] };

			if (scale[0]) {
				newLevelSize[1] -= 1;
			} else if (scale[2]) {
				newLevelSize[1] += 1;
			}

			if (scale[1]) {
				newLevelSize[0] -= 1;
			} else if (scale[3]) {
				newLevelSize[0] += 1;
			}

			if (newLevelSize[0] != levelSize[0] || newLevelSize[1] != levelSize[1]) {
				if (newLevelSize[0] <= 0) {
					newLevelSize[0] = 1;
				}
				if (newLevelSize[1] <= 0) {
					newLevelSize[1] = 1;
				}
				BufferedImage newLevel = new BufferedImage(newLevelSize[0], newLevelSize[1],
						BufferedImage.TYPE_INT_ARGB);
				int newPixels[] = new int[newLevel.getWidth() * newLevel.getHeight()];
				for (int x = 0; x < newLevel.getWidth(); x++) {
					for (int y = 0; y < newLevel.getHeight(); y++) {
						int spacingWidth = newLevel.getWidth() - level.getWidth();
						int spacingHeight = newLevel.getHeight() - level.getHeight();
						if (x < newLevel.getWidth() - spacingWidth && y < newLevel.getHeight() - spacingHeight) {
							newPixels[x + y * newLevel.getWidth()] = pixels[x + y * level.getWidth()];
						} else {
							newPixels[x + y * newLevel.getWidth()] = newLevel.getRGB(x, y);
						}
					}
				}
				newLevel.setRGB(0, 0, newLevel.getWidth(), newLevel.getHeight(), newPixels, 0, newLevel.getWidth());
				level = newLevel;
				pixels = newPixels;
				levelSize = newLevelSize;
			}
		}

		if (scale[0] || scale[1] || scale[2] || scale[3]) {
			ticks++;
		} else {
			ticks = 0;
		}

		boolean[] move = { keys[KeyEvent.VK_W], keys[KeyEvent.VK_A], keys[KeyEvent.VK_S], keys[KeyEvent.VK_D] };

		if (move[0]) {
			position[1] += 10;
		} else if (move[2]) {
			position[1] -= 10;
		}

		if (move[1]) {
			position[0] += 10;
		} else if (move[3]) {
			position[0] -= 10;
		}

		if (getDrawPosition()[0] / blockSize >= 0 && getDrawPosition()[1] / blockSize >= 0
				&& getDrawPosition()[0] / blockSize < level.getWidth()
				&& getDrawPosition()[1] / blockSize < level.getHeight()) {
			if (buttons[MouseEvent.BUTTON1]) {
				pixels[(getDrawPosition()[0] / blockSize)
						+ (getDrawPosition()[1] / blockSize) * level.getWidth()] = selectedColor.getRGB();
			} else if (buttons[MouseEvent.BUTTON3]) {
				pixels[(getDrawPosition()[0] / blockSize)
						+ (getDrawPosition()[1] / blockSize) * level.getWidth()] = new Color(0, 0, 0, 0).getRGB();
			}
		}

		if (-position[0] < 0) {
			position[0] = 0;
		} else if (-position[0] > level.getWidth() * blockSize) {
			position[0] = -(level.getWidth() * blockSize);
		}

		if (-position[1] < 0) {
			position[1] = 0;
		} else if (-position[1] > level.getHeight() * blockSize) {
			position[1] = -(level.getHeight() * blockSize);
		}

		int[] newPixels = new int[level.getWidth() * level.getHeight()];
		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				if (x == getDrawPosition()[0] / blockSize && y == getDrawPosition()[1] / blockSize) {
					newPixels[x + y * level.getWidth()] = Color.GREEN.getRGB();
				} else {
					newPixels[x + y * level.getWidth()] = pixels[x + y * level.getWidth()];
				}
			}
		}
		level.setRGB(0, 0, level.getWidth(), level.getHeight(), newPixels, 0, level.getWidth());
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, main.getWidth(), main.getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(camera.getX(), camera.getY()); // Start of Camera Affected Objects

		// Draw Zone
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, level.getWidth() * blockSize, level.getHeight() * blockSize);

		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				g.drawImage(rh.get(new Color(pixels[x + y * level.getWidth()])), x * blockSize, y * blockSize,
						blockSize, blockSize, null);
			}
		}

		// Grid Lines
		g.setColor(Color.BLACK);
		for (int y = 0; y < level.getHeight(); y++) {
			g.drawLine(0, y * blockSize, level.getWidth() * blockSize, y * blockSize);
		}
		for (int x = 0; x < level.getWidth(); x++) {
			g.drawLine(x * blockSize, 0, x * blockSize, level.getHeight() * blockSize);
		}

		// Drawing Cursor
		g.drawImage(selectedTile, getDrawPosition()[0], getDrawPosition()[1], blockSize, blockSize, null);
		g.setColor(Color.WHITE);
		g.drawRect(getDrawPosition()[0], getDrawPosition()[1], blockSize, blockSize);

		g2d.translate(-camera.getX(), -camera.getY()); // End of Camera Affected Objects

		// Map
		int mapSize = 128;
		g.drawImage(level, 0, main.getHeight() - mapSize - 1, mapSize, mapSize, null);

		g.setColor(Color.WHITE);
		g.drawRect(0, main.getHeight() - mapSize - 1, mapSize, mapSize);

		// Controls
		g.setFont(new Font("font", 0, 30));
		String[] controls = { "Controls", "F1 - Export ", "ESC - Exit", "WASD - Move Camera",
				"Mouse Wheel - Change Selected Tile", "Arrow Keys - Change Map Size" };

		int x = 5;
		int y = 30;

		for (int i = 0; i < controls.length; i++) {
			g.drawString(controls[i], x, y + 30 * i);
		}

		g.drawString("[" + levelSize[0] + ", " + levelSize[1] + "]", mapSize + 5, main.getHeight() - 45);
		g.drawString("[" + (getDrawPosition()[0] / blockSize + 1) + ", " + (getDrawPosition()[1] / blockSize + 1) + "]",
				mapSize + 5, main.getHeight() - 15);

		g.dispose();
	}

	public int[] getDrawPosition() {
		int[] pos = { 0, 0 };
		pos[0] = (int) (Math.floor(1.0 * (-camera.getX() + mousePosition[0]) / blockSize) * blockSize);
		pos[1] = (int) (Math.floor(1.0 * (-camera.getY() + mousePosition[1]) / blockSize) * blockSize);
		return pos;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		selected -= e.getWheelRotation();
		if (selected <= -1) {
			selected = rh.ids.size() - 1;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = true;

		if (key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if (key == KeyEvent.VK_F1) {
			int spacing = 4;
			BufferedImage exportedLevel = new BufferedImage(level.getWidth() + spacing, level.getHeight() + spacing,
					BufferedImage.TYPE_INT_ARGB);
			int[] newPixels = new int[exportedLevel.getWidth() * exportedLevel.getHeight()];
			for (int x = 0; x < exportedLevel.getWidth(); x++) {
				for (int y = 0; y < exportedLevel.getHeight(); y++) {
					if (x >= spacing / 2 && y >= spacing / 2 && x < exportedLevel.getWidth() - spacing / 2
							&& y < exportedLevel.getHeight() - spacing / 2) {
						if (pixels[(x - spacing / 2) + (y - spacing / 2) * level.getWidth()] == new Color(0, 0, 0, 0)
								.getRGB()) {
							newPixels[x + y * exportedLevel.getWidth()] = Color.BLACK.getRGB();
						} else {
							newPixels[x + y * exportedLevel.getWidth()] = pixels[(x - spacing / 2)
									+ (y - spacing / 2) * level.getWidth()];
						}
					} else {
						newPixels[x + y * exportedLevel.getWidth()] = Color.BLACK.getRGB();
					}
				}
			}
			exportedLevel.setRGB(0, 0, exportedLevel.getWidth(), exportedLevel.getHeight(), newPixels, 0,
					exportedLevel.getWidth());
			try {
				ImageIO.write(exportedLevel, "png", new File("level.png"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if (key == KeyEvent.VK_F2) {
			if (vsync) {
				vsync = false;
			} else {
				vsync = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public Main getMain() {
		return main;
	}

	public BufferedImage getLevel() {
		return level;
	}

	public int getBlockSize() {
		return blockSize;
	}
}
