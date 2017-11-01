package me.negative3.vital.game.handlers;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameObject;
import me.negative3.vital.game.objects.Chest;
import me.negative3.vital.game.objects.Enemy;
import me.negative3.vital.game.objects.Tile;
import me.negative3.vital.library.framework.ObjectId;

public class LevelHandler {
	private Game game;
	private LinkedList<GameObject> levelObjects = new LinkedList<>();

	private int[] levelPixel;
	private int[] levelSize;
	private int enemies = 0;

	private int blockSize = 0;

	public LevelHandler(Game game) {
		this.game = game;
		this.blockSize = game.getBlockSize();
	}

	public void generateLevel(int width, int height) {
		int spacing = 4;
		levelSize = new int[] { width + spacing, height + spacing };

		int builderId = new Color(128, 0, 128).getRGB(); // Purple Pixel (Used as passable tiles)
		int blank = new Color(0, 0, 0).getRGB(); // Black pixel
		int[] builder = new int[] { 3, 3 }; // place builder at x: 3, y: 3

		int[] pixels = new int[width * height]; // Create a pixel array the size of width * height
		int[] newPixels = new int[width * height]; // Create a second pixel array the size of width * height

		// create empty board
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixels[x + y * width] = blank;
			}
		}

		int moves = randInt(400, 800);
		if (width * height / 2 > 800) {
			moves = randInt(400, width * height / 2); // find room size and corresponding move count
		}

		int[] choices = new int[] { -1, 1 }; // Used in Builder

		// Builder
		for (int ii = 0; ii < moves; ii++) {
			builder[randInt(0, 2)] += choices[randInt(0, 2)]; // Moves randomly on either the x or y axis and then
																// positively or negatively

			// Check if x is out of bounds
			if (builder[0] > width - 1) {
				builder[0] = width - 1;
			} else if (builder[0] < 0) {
				builder[0] = 0;
			}

			// Check if y is out of bounds
			if (builder[1] > height - 1) {
				builder[1] = height - 1;
			} else if (builder[1] < 0) {
				builder[1] = 0;
			}

			// Place floor tile color codes at builder x and builder y
			newPixels = new int[width * height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (y == builder[1] && x == builder[0]) {
						// System.out.println("Passable tile placed at (" + builder[0] + ", " +
						// builder[1] + ")");
						newPixels[x + y * width] = builderId;
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				}
			}
			pixels = newPixels;
			// System.out.println((ii + 1) + " moves out of " + moves + " done!");
		}

		newPixels = new int[(width + spacing) * (height + spacing)];
		for (int x = 0; x < width + spacing; x++) {
			for (int y = 0; y < height + spacing; y++) {
				if (x <= (spacing / 2) - 1 || x >= width + ((spacing / 2) - 1) || y <= (spacing / 2) - 1
						|| y >= height + ((spacing / 2) - 1))
					newPixels[x + y * (width + spacing)] = blank;
				else
					newPixels[x + y * (width + spacing)] = pixels[(x - (spacing / 2)) + (y - (spacing / 2)) * width];
			}
		}
		pixels = newPixels;

		width += spacing;
		height += spacing;

		// Add bounds to left and right edges
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x != width - 1 && x != 0) {
					if (pixels[x + y * width] == blank && pixels[(x + 1) + y * width] == builderId) {
						newPixels[x + y * width] = new Color(0, 0, 255).getRGB();
					} else if (pixels[x + y * width] == blank && pixels[(x - 1) + y * width] == builderId) {
						newPixels[x + y * width] = new Color(0, 0, 255).getRGB();
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Add bounds to top and bottom edges
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y != height - 1 && y != 0) {
					if (pixels[x + y * width] == blank && pixels[x + (y + 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(0, 0, 255).getRGB();
					} else if (pixels[x + y * width] == blank && pixels[x + (y - 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(0, 0, 255).getRGB();
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Add bounds to corners
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y != height - 1 && y != 0 && x != width - 1 && x != 0) {
					if (pixels[x + y * width] == blank && pixels[(x + 1) + (y + 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(1, 1, 255).getRGB();
					} else if (pixels[x + y * width] == blank && pixels[(x + 1) + (y - 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(1, 1, 255).getRGB();
					} else if (pixels[x + y * width] == blank && pixels[(x - 1) + (y - 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(1, 1, 255).getRGB();
					} else if (pixels[x + y * width] == blank && pixels[(x - 1) + (y + 1) * width] == builderId) {
						newPixels[x + y * width] = new Color(1, 1, 255).getRGB();
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Get spawn coordinates
		int[] spawnCoords = new int[] { 0, 0 };
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == builderId) {
					spawnCoords = new int[] { x, y };
					break;
				}
			}
			if (spawnCoords[0] != 0 || spawnCoords[1] != 0) {
				break;
			}
		}

		// Place spawn
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x == spawnCoords[0] && y == spawnCoords[1]) {
					newPixels[x + y * width] = new Color(255, 255, 255).getRGB();
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Place enemies
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == builderId && (x >= spawnCoords[0] + 5 || x <= spawnCoords[0] - 5)
						&& (y >= spawnCoords[1] + 5 || y <= spawnCoords[1] - 5)) {
					if (randInt(0, 100) >= 85) {
						newPixels[x + y * width] = new Color(255, 1, 1).getRGB();
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Place Health Chest
		boolean spawned = false;
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == builderId && (x >= spawnCoords[0] + 5 || x <= spawnCoords[0] - 5)
						&& (y >= spawnCoords[1] + 5 || y <= spawnCoords[1] - 5)) {
					if (randInt(0, 100) >= 98 && !spawned) {
						newPixels[x + y * width] = new Color(255, 255, 0).getRGB();
						spawned = true;
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Place Ammo Chest
		spawned = false;
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == builderId && (x >= spawnCoords[0] + 5 || x <= spawnCoords[0] - 5)
						&& (y >= spawnCoords[1] + 5 || y <= spawnCoords[1] - 5)) {
					if (randInt(0, 100) >= 98 && !spawned) {
						newPixels[x + y * width] = new Color(255, 255, 1).getRGB();
						spawned = true;
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Place Weapon Chest
		spawned = false;
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == builderId && (x >= spawnCoords[0] + 5 || x <= spawnCoords[0] - 5)
						&& (y >= spawnCoords[1] + 5 || y <= spawnCoords[1] - 5)) {
					if (randInt(0, 100) >= 98 && !spawned) {
						newPixels[x + y * width] = new Color(255, 255, 2).getRGB();
						spawned = true;
					} else {
						newPixels[x + y * width] = pixels[x + y * width];
					}
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		// Get exit door coordinates
		int[] doorCoords = new int[] { 0, 0 };
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x + y * width] == new Color(0, 0, 255).getRGB()) {
					doorCoords = new int[] { x, y };
				}
			}
		}

		// Place exit door
		newPixels = new int[width * height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x == doorCoords[0] && y == doorCoords[1]) {
					newPixels[x + y * width] = new Color(255, 0, 0).getRGB();
				} else {
					newPixels[x + y * width] = pixels[x + y * width];
				}
			}
		}
		pixels = newPixels;

		/*
		 * int[] pixels = new int[width * height]; for (int x = 0; x < width; x++) { for
		 * (int y = 0; y < height; y++) { pixels[x + y * width] = generateRGB(); } }
		 */
		levelPixel = pixels;
	}

	private int randInt(int min, int max) {
		if (min > max)
			return 0;
		return (int) Math.floor(min + (Math.random() * (max - min)));
	}

	public void loadLevel(BufferedImage level) {
		levelSize = new int[] { level.getWidth(), level.getHeight() };
		int[] pixels = new int[level.getWidth() * level.getHeight()];
		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				pixels[x + y * level.getWidth()] = level.getRGB(x, y);
			}
		}
		levelPixel = pixels;
	}

	public void spawnLevel() {
		enemies = 0;
		int[] size = levelSize;
		int[] pixels = levelPixel;

		for (int x = 0; x < size[0]; x++) {
			for (int y = 0; y < size[1]; y++) {
				int pixel = pixels[x + y * size[0]];
				int ax = (game.getMain().getWidth() / 2 - size[0] * blockSize / 2) + x * blockSize;
				int ay = (game.getMain().getHeight() / 2 - size[1] * blockSize / 2) + y * blockSize;

				int chestSize = (int) (blockSize * .6);

				if (checkRGB(pixel, 0, 0, 255)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.NORMAL_TILE, game));
				} else if (checkRGB(pixel, 255, 255, 255)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					for (int i = game.getObjectHandler().objects.size() - 1; i >= 0; i--) {
						GameObject obj = game.getObjectHandler().objects.get(i);
						if (obj.getObjectId() == ObjectId.PLAYER) {
							obj.setPos(ax, ay, blockSize, blockSize);
						}
					}
				} else if (checkRGB(pixel, 128, 0, 128)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					// levelObjects.add(new Tile(ax, ay, blockSize, blockSize,
					// ObjectId.BUILDER_TILE, game));
				} else if (checkRGB(pixel, 0, 0, 0)) {
					// levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.BLANK_TILE,
					// game));
				} else if (checkRGB(pixel, 255, 0, 0)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.DOOR_TILE, game));
				} else if (checkRGB(pixel, 1, 1, 255)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.CORNER_TILE, game));
				} else if (checkRGB(pixel, 255, 1, 1)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					levelObjects.add(new Enemy(ax, ay, 128, 128, ObjectId.ENEMY, game));
					enemies++;
				} else if (checkRGB(pixel, 255, 2, 2)) {
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.LAVA_TILE, game));
				} else if (checkRGB(pixel, 255, 255, 0)) {
					// Health chest
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					levelObjects.add(new Chest(ax + (blockSize / 2 - chestSize / 2),
							ay + (blockSize / 2 - chestSize / 2), chestSize, chestSize, ObjectId.CHEST, game, 0));
				} else if (checkRGB(pixel, 255, 255, 1)) {
					// Ammo chest
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					levelObjects.add(new Chest(ax + (blockSize / 2 - chestSize / 2),
							ay + (blockSize / 2 - chestSize / 2), chestSize, chestSize, ObjectId.CHEST, game, 1));
				} else if (checkRGB(pixel, 255, 255, 2)) {
					// Weapon chest
					levelObjects.add(new Tile(ax, ay, blockSize, blockSize, ObjectId.FLOOR_TILE, game));
					levelObjects.add(new Chest(ax + (blockSize / 2 - chestSize / 2),
							ay + (blockSize / 2 - chestSize / 2), chestSize, chestSize, ObjectId.CHEST, game, 2));
				}
			}
		}
		for (GameObject obj : levelObjects) {
			game.getObjectHandler().add(obj);
		}
	}

	public Image getMapImage() {
		int[] size = levelSize;
		int[] pixels = levelPixel;

		int[] newPixels = new int[size[0] * size[1]];
		for (int xx = 0; xx < size[0]; xx++) {
			for (int yy = 0; yy < size[1]; yy++) {
				if (pixels[xx + yy * size[0]] == new Color(255, 255, 255).getRGB()) {
					newPixels[xx + yy * size[0]] = new Color(128, 0, 128).getRGB();
				} else if (pixels[xx + yy * size[0]] == new Color(0, 0, 0).getRGB()) {
					newPixels[xx + yy * size[0]] = new Color(0, 0, 0, 0).getRGB();
				} else {
					newPixels[xx + yy * size[0]] = pixels[xx + yy * size[0]];
				}
			}
		}
		pixels = newPixels;

		BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, size[0], size[1], pixels, 0, size[0]);
		return image;
	}

	public Image getMapImage(int px, int py) {
		int[] size = levelSize;
		int[] pixels = levelPixel;

		px = ScreenCoordsToMapCoords(px, py)[0];
		py = ScreenCoordsToMapCoords(px, py)[1];

		int[] newPixels = new int[size[0] * size[1]];
		for (int x = 0; x < size[0]; x++) {
			for (int y = 0; y < size[1]; y++) {
				if (px == x && py == y) {
					newPixels[x + y * size[0]] = new Color(255, 255, 255).getRGB();
				} else if (pixels[x + y * size[0]] == new Color(255, 1, 1).getRGB()) {
					newPixels[x + y * size[0]] = new Color(128, 0, 128).getRGB();
				} else if (pixels[x + y * size[0]] == new Color(255, 255, 255).getRGB()) {
					newPixels[x + y * size[0]] = new Color(128, 0, 128).getRGB();
				} else if (pixels[x + y * size[0]] == new Color(0, 0, 0).getRGB()) {
					newPixels[x + y * size[0]] = new Color(0, 0, 0, 0).getRGB();
				} else if (pixels[x + y * size[0]] == new Color(255, 0, 0).getRGB()) {
					newPixels[x + y * size[0]] = new Color(0, 0, 255).getRGB();
				} else {
					newPixels[x + y * size[0]] = pixels[x + y * size[0]];
				}
			}
		}
		pixels = newPixels;

		BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, size[0], size[1], pixels, 0, size[0]);
		return image;
	}

	public Image getMapImage(int width, int height, int x, int y) {
		int[] size = levelSize;
		int[] pixels = levelPixel;

		int[] newPixels = new int[size[0] * size[1]];
		for (int xx = 0; xx < size[0]; xx++) {
			for (int yy = 0; yy < size[1]; yy++) {
				if (pixels[xx + yy * size[0]] == new Color(255, 255, 255).getRGB()) {
					newPixels[xx + yy * size[0]] = new Color(0, 0, 0).getRGB();
				} else {
					newPixels[xx + yy * size[0]] = pixels[xx + yy * size[0]];
				}
			}
		}

		pixels = newPixels;

		newPixels = new int[width * height];
		for (int xx = x - width / 2; xx < x + width / 2; xx++) {
			for (int yy = y - height / 2; yy < y + height / 2; yy++) {
				if (xx == x && yy == y) {
					newPixels[xx + yy * width] = new Color(255, 255, 255).getRGB();
				} else if (xx < 0 || xx > size[0] || yy < 0 || yy > size[1]) {
					newPixels[xx + yy * width] = new Color(0, 0, 0).getRGB();
				} else {
					newPixels[xx + yy * width] = pixels[xx + yy * size[0]];
				}
			}
		}

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, newPixels, 0, width);
		return image;
	}

	public int[] getMapSize() {
		return levelSize;
	}

	public int[] getMapPixels() {
		return levelPixel;
	}

	private boolean checkRGB(int pixel, int r, int g, int b) {
		if (((pixel >> 16) & 0xff) == r && ((pixel >> 8) & 0xff) == g && ((pixel) & 0xff) == b)
			return true;
		return false;
	}

	public void clearLevel() {
		for (int i = levelObjects.size() - 1; i >= 0; i--) {
			GameObject obj = levelObjects.get(i);
			obj.setDead(true);
			levelObjects.remove(i);
		}
	}

	public void add(GameObject obj) {
		levelObjects.add(obj);
	}

	public int getPixel(int x, int y) {
		int[] pos = ScreenCoordsToMapCoords(x, y);
		return levelPixel[pos[0] + pos[1] * levelSize[0]];
	}

	public int[] ScreenCoordsToMapCoords(int x, int y) {
		int[] size = levelSize;
		x = (x - (game.getMain().getWidth() / 2 - size[0] * blockSize / 2)) / blockSize;
		y = (y - (game.getMain().getHeight() / 2 - size[1] * blockSize / 2)) / blockSize;
		return new int[] { x, y };
	}

	public int[] MapCoordsToScreenCoords(int x, int y) {
		int[] size = levelSize;
		x = (game.getMain().getWidth() / 2 - size[0] * blockSize / 2) + x * blockSize;
		y = (game.getMain().getHeight() / 2 - size[1] * blockSize / 2) + y * blockSize;
		return new int[] { x, y };
	}

	public int getEnemies() {
		return enemies;
	}
}
