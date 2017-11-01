package me.negative3.vital.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import me.negative3.vital.game.framework.GameMenu;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.handlers.AudioHandler;
import me.negative3.vital.game.handlers.LevelHandler;
import me.negative3.vital.game.handlers.ObjectHandler;
import me.negative3.vital.game.handlers.ResourceHandler;
import me.negative3.vital.game.items.WeaponItem;
import me.negative3.vital.game.menus.MainMenu;
import me.negative3.vital.game.objects.Player;
import me.negative3.vital.game.rendering.Camera;
import me.negative3.vital.library.framework.GameState;
import me.negative3.vital.library.framework.ObjectId;
import me.negative3.vital.library.framework.WeaponType;

public class Game extends JPanel
		implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, MouseInputListener {
	private static final long serialVersionUID = -5392079078096345962L;

	private Thread thread;
	private int combo = 0, secretCombo = 0;
	private boolean running = false, debug = false, vsync = true, loadLevel = false;
	private Main main;
	private ObjectHandler objectHandler;
	private ResourceHandler resourceHandler;
	private AudioHandler audioHandler;
	private int colorStage = 0;
	private int currentLevel = 0;
	private Rectangle renderingBounds;
	private LevelHandler levelHandler;
	private Camera camera;
	private GameMenu currentMenu;

	private GameState state = GameState.MENU;

	private int blockSize = 156;

	public Game(Main main) {
		main.add(this);
		this.main = main;
		renderingBounds = new Rectangle(0, 0, main.getWidth() + blockSize, main.getHeight() + blockSize);
		resourceHandler = new ResourceHandler();
		audioHandler = new AudioHandler();
		levelHandler = new LevelHandler(this);
		nextLevel();
		objectHandler = new ObjectHandler(this);
		objectHandler
				.add(new Player(main.getWidth() / 2 - 16, main.getHeight() / 2 - 16, 128, 128, ObjectId.PLAYER, this));
		currentMenu = new MainMenu(this);
		camera = new Camera(0, 0, this);
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		System.out.println("Width: " + main.getWidth() + " Height: " + main.getHeight());
	}

	// Starts the game loop
	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	// Stops the game loop
	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int[] player = new int[] { 0, 0 };

	private GameState lastState = null;

	// Tick Method (Runs 60 times per second)
	public void update() {
		if (state != lastState) {
			if (state == GameState.MENU) {
				audioHandler.stop_all();
				audioHandler.play("title_music", true, 0.1f);
			} else if (state == GameState.GAME) {
				audioHandler.stop_all();
				if (currentLevel < 50) {
					
				}
			}
			lastState = state;
		}

		if (state == GameState.GAME) {
			if (loadLevel) {
				loadLevel = false;
				levelHandler.clearLevel();
				levelHandler.spawnLevel();
			}

			objectHandler.update();

			boolean[] direction = new boolean[] { keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP],
					keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT], keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN],
					keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT] };

			renderingBounds.setLocation((int) -camera.getX() - blockSize / 2, (int) -camera.getY() - blockSize / 2);

			for (GameObject obj : objectHandler.objects) {
				if (obj.getObjectId() == ObjectId.PLAYER) {
					Player player = (Player) obj;
					combo = secretCombo + player.getCombo();
					camera.tick(player);
					player.updateAngle(-camera.getX() + mPos[0], -camera.getY() + mPos[1]);
					this.player[0] = (int) (player.getX() + player.getWidth() / 2);
					this.player[1] = (int) (player.getY() + player.getHeight() / 2);

					double speedX = 0;
					double speedY = 0;
					double speed = 10;

					if (direction[0] == direction[2]) {
						speedY = 0;
					} else if (direction[0]) {
						speedY = -speed;
					} else if (direction[2]) {
						speedY = speed;
					}

					if (direction[1] == direction[3]) {
						speedX = 0;
					} else if (direction[1]) {
						speedX = -speed;
					} else if (direction[3]) {
						speedX = speed;
					}

					if (speedX != 0 && speedY != 0) {
						speedX *= .75;
						speedY *= .75;
					}

					player.setVelX(speedX);
					player.setVelY(speedY);

					if (buttons[MouseEvent.BUTTON1]) {
						if (player.getEquiped().getObjectId() == ObjectId.WEAPON) {
							WeaponItem equiped = (WeaponItem) player.getEquiped();
							if (equiped.getWeaponType() == WeaponType.FULL_AUTO) {
								player.onUse(mPos[0], mPos[1]);
							}
						}
					}
					break;
				}
			}
		} else if (state == GameState.MENU) {
			currentMenu.update();
		}
	}

	// Render Method (Runs as fast as it can)
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, main.getWidth(), main.getHeight());
		g.setColor(Color.BLACK);
		if (state == GameState.GAME) {
			g.drawImage(resourceHandler.get(colorStage % 3), 0, 0, main.getWidth(), main.getHeight(), null);
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(camera.getX(), camera.getY());
			g2d.rotate(camera.getR(), -camera.getX() + (main.getWidth() / 2), -camera.getY() + (main.getHeight() / 2));

			if (debug) {
				g.setColor(Color.GREEN);
				g2d.draw(renderingBounds);
			}

			objectHandler.paintComponent(g);

			g2d.rotate(-camera.getR(), -camera.getX() + (main.getWidth() / 2), -camera.getY() + (main.getHeight() / 2));
			g2d.translate(-camera.getX(), -camera.getY());

			for (int i = 0; i < objectHandler.objects.size(); i++) {
				GameObject obj = objectHandler.objects.get(i);
				if (obj.getObjectId() == ObjectId.PLAYER) {
					Player player = (Player) obj;
					int ii = -1;
					for (int iii = 0; iii < player.getMaxHealth(); iii++) {
						if (iii % 10 == 0) {
							ii++;
						}
						if (iii >= player.getHealth()) {
							g.drawImage(resourceHandler.get("no_heart"), 64 * (iii % 10), 64 * ii, 64, 64, null);
						} else {
							g.drawImage(resourceHandler.get("heart"), 64 * (iii % 10), 64 * ii, 64, 64, null);
						}
					}
					g.drawString(combo + "", main.getWidth() - 30 * (combo + "").length(), 30);
					break;
				}
			}

			g.setColor(Color.BLACK);
			int mapScale = 10;
			g.fillRect(0, main.getHeight() - 16 * (blockSize / mapScale), 16 * (blockSize / mapScale),
					16 * (blockSize / mapScale));
			g.drawImage(levelHandler.getMapImage(player[0], player[1]), 0,
					main.getHeight() - 16 * (blockSize / mapScale), 16 * (blockSize / mapScale),
					16 * (blockSize / mapScale), null);
		} else if (state == GameState.MENU) {
			currentMenu.render(g);
		}
		int reticleSize = 46;
		g.drawImage(resourceHandler.get("cursor"), mPos[0] - reticleSize / 2, mPos[1] - reticleSize / 2, reticleSize,
				reticleSize, null);
		g.dispose();
	}

	// Keyboard and Mouse input

	private boolean[] keys = new boolean[500];
	private boolean[] buttons = new boolean[500];
	private int[] mPos = new int[] { 0, 0 };

	public boolean[] getMouseButtons() {
		return buttons;
	}

	public Point getMousePoint() {
		return new Point(mPos[0], mPos[1]);
	}

	public boolean[] getKeys() {
		return keys;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			for (GameObject obj : objectHandler.objects) {
				if (obj.getObjectId() == ObjectId.PLAYER) {
					Player player = (Player) obj;
					if (player.getEquiped().getObjectId() == ObjectId.WEAPON) {
						WeaponItem equiped = (WeaponItem) player.getEquiped();
						if (equiped.getWeaponType() == WeaponType.SINGLE_SHOT) {
							player.onUse(mPos[0], mPos[1]);
						}
					}
					break;
				}
			}
		}
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mPos[0] = e.getX();
		mPos[1] = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mPos[0] = e.getX();
		mPos[1] = e.getY();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = true;
		if (key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if (key == KeyEvent.VK_F3) {
			if (debug)
				debug = false;
			else
				debug = true;
		} else if (key == KeyEvent.VK_F2) {
			if (vsync)
				vsync = false;
			else
				vsync = true;
		} else if (key == KeyEvent.VK_F1) {
			colorStage++;
		} else if (key == KeyEvent.VK_F4) {
			nextLevel();
		} else if (key == KeyEvent.VK_F5) {
			secretCombo++;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	// Game Loop
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

	public ObjectHandler getObjectHandler() {
		return objectHandler;
	}

	public ResourceHandler getResourceHandler() {
		return resourceHandler;
	}
	
	public AudioHandler getAudioHandler() {
		return audioHandler;
	}

	public LevelHandler getLevelHandler() {
		return levelHandler;
	}

	public boolean isDebug() {
		return debug;
	}

	public Rectangle getRenderingBounds() {
		return renderingBounds;
	}

	public Main getMain() {
		return main;
	}

	public void nextLevel() {
		currentLevel++;
		boolean blank = false;
		if (currentLevel == 1) {
			if (blank) {
				levelHandler.loadLevel(resourceHandler.get("level_blank"));
			} else {
				levelHandler.loadLevel(resourceHandler.get("level_mockup"));
			}
		} else {
			levelHandler.generateLevel(32, 32);
		}

		colorStage++;
		loadLevel = true;
	}

	public void resetLevel() {
		currentLevel = 0;
		nextLevel();
	}

	public int getBlockSize() {
		return blockSize;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setColorStage(int colorStage) {
		this.colorStage = colorStage;
	}

	public int getCombo() {
		return combo;
	}

	public void setState(GameState state) {
		this.state = state;
	}
}
