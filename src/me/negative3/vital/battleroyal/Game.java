package me.negative3.vital.battleroyal;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class Game extends JPanel
		implements Runnable, KeyListener, MouseListener, MouseInputListener, MouseWheelListener, MouseMotionListener {
	private static final long serialVersionUID = -7572350765336837501L;
	private Main main;
	private Thread thread;
	private boolean running = false, vsync = true;
	private BufferedImage background;
	private int ticks = 0, pixels[];

	// Constructor
	public Game(Main main) {
		main.add(this);
		this.main = main;
		setFocusable(true);
		background = new BufferedImage(main.getWidth(), main.getHeight(), BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) background.getRaster().getDataBuffer()).getData();
	}

	// Start Method
	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	// Stop Method (for applets)
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

	// Tick Method
	public void update() {
		ticks++;

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (int)(Math.floor(i / main.getWidth()) * main.getWidth()) * ticks;
		}
	}

	// Render Method
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, main.getWidth(), main.getHeight(), null);
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

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
