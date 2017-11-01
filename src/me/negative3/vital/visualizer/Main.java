package me.negative3.vital.visualizer;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = 8540778207092607675L;

	private static Game game;

	public Main() {
		setTitle("title");
		setUndecorated(true);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
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
