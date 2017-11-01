package me.negative3.vital.game.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import me.negative3.vital.game.Game;
import me.negative3.vital.game.framework.GameMenu;
import me.negative3.vital.library.framework.GameState;

public class MainMenu extends GameMenu {
	BufferedImage[] buttons, graphics;
	Color[] colors;

	Color color;
	BufferedImage graphic;
	int option = 1, ticks = 0;

	public MainMenu(Game game) {
		super(game);
		buttons = game.getResourceHandler().getFrames("title_buttons");
		graphics = game.getResourceHandler().getFrames("title_graphics");
		colors = new Color[buttons.length];
		colors[2] = new Color(243, 218, 220); // RED
		colors[4] = new Color(208, 222, 208); // GREEN
		colors[6] = new Color(203, 220, 235); // BLUE
		colors[8] = new Color(207, 216, 220); // GRAY
		color = colors[2];
		graphic = graphics[0];
	}

	@Override
	public void update() {
		Point mousePoint = game.getMousePoint();
		boolean[] mouseButtons = game.getMouseButtons();

		if (option != 0) {
			graphic = graphics[option - 1];
			color = colors[option * 2];
		}

		option = 0;
		for (int i = 0; i < buttons.length; i += 2) {
			BufferedImage button = buttons[i];
			if (i == (option * 2)) {
				button = buttons[i + 1];
			}
			int height = 32;
			int width = (int) Math.round(height * (1.0 * button.getWidth() / button.getHeight()));
			int x = 150;
			int y = 125 + (i * (height - 5));
			if ((mousePoint.getX() > x && mousePoint.getX() < x + width)
					&& (mousePoint.getY() > y && mousePoint.getY() < y + height) && i != 0) {
				option = i / 2;
			}
		}

		if (mouseButtons[MouseEvent.BUTTON1]) {
			switch (option) {
			case 1:
				game.setState(GameState.GAME);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				System.exit(0);
				break;
			default:
				break;
			}
		}

		ticks++;
	}

	@Override
	public void render(Graphics g) {
		int sidebarY = 0;
		int sidebarHeight = 0;
		g.setColor(color);
		g.fillRect(0, 0, game.getMain().getWidth(), game.getMain().getHeight());
		g.setColor(Color.BLACK);

		int width = 350;
		int height = (int) Math.round(width * (1.0 * graphic.getHeight() / graphic.getWidth()));

		g.drawImage(graphic,
				(game.getMain().getWidth() / 2 - width / 2)
						- ((game.getMousePoint().x - game.getMain().getWidth() / 2) * 1 / 7),
				(game.getMain().getHeight() / 2 - height / 2)
						- ((game.getMousePoint().y - game.getMain().getHeight() / 2) * 1 / 7),
				width, height, null);

		for (int i = 0; i < buttons.length; i += 2) {
			BufferedImage button = buttons[i];
			if (i == (option * 2) && i != 0) {
				button = buttons[i + 1];
			}
			height = 32;
			width = (int) Math.round(height * (1.0 * button.getWidth() / button.getHeight()));
			if (i == 2) {
				sidebarY = 125 + (i * (height - 10));
			} else if (i == buttons.length - 2) {
				sidebarHeight = 125 + (i * (height - 5)) - sidebarY + height;
			}
			g.drawImage(button, 150, 125 + (i * (height - 5)), width, height, null);
		}

		g.fillRect(130, sidebarY, 5, sidebarHeight);
	}

}
