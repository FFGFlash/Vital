package me.negative3.vital.game;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = 8540778207092607675L;

	private static Game game;
	private boolean hidecursor = true;

	public Main() {
		setTitle("title");
		setUndecorated(true);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		if (hidecursor) {
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
			setCursor(blankCursor);
		}
		new Game(this).start();
		setVisible(true);
	}

	public static void main(String args[]) {
		new Main();
	}

	public static Game getGame() {
		return game;
	}

	@Override
	public Dimension getSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	@Override
	public int getWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	@Override
	public int getHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
}
