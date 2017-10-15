package me.negative3.vital.battleroyal;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = -4842973624139603991L;
	public static JFrame frame;
	
	public Main() {
		setUndecorated(true);
		setTitle("Vital: Battle Royal");
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAutoRequestFocus(true);
		
		new Game(this).start();
		
		setVisible(true);
	}
	
	public static void main(String args[]) {
		frame = new Main();
	}
	
	public int getWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	
	public int getHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
}
