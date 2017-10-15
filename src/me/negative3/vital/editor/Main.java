package me.negative3.vital.editor;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = -1290349840571890153L;
	public static JFrame frame;

	public Main() {
		setTitle("Vital Editor v0.0.1");
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setAutoRequestFocus(true);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		new Game(this).start();
		
		setVisible(true);
	}
	
	public static void main(String args[]) {
		frame = new Main();
	}
}
