package minesweeper;


import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		// create JFrame
		JFrame frame = new JFrame("Mine Sweeper");
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setUndecorated(true);		
		
		// add header panel
		Header header = new Header();
		frame.add(header);
		
		// add game panel
		Game game = new Game();
		frame.add(game);
		
		// finish showing JFrame
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}


}