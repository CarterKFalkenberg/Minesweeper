package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Header extends JPanel implements ActionListener{

	// exit button
	JButton exit;
	
	// bomb and flag trackers
	JLabel bombCounter;
	static int flagsLeft = 40;
	
	// timer to update flag count
	Timer timer;
	
	// background color
	Color darkGreen = new Color (75, 115, 45);
	
	// constructor for header panel
	Header(){
		
		// create green header
		this.setBackground(darkGreen);
		this.setPreferredSize(new Dimension(720, 60));
		this.setLayout(new FlowLayout(FlowLayout.LEADING, 150, 0));
		
		
		// show bombs left
		bombCounter = new JLabel("Flags Left: " + flagsLeft);
		bombCounter.setFont(new Font("Monospaced", Font.BOLD, 30));
		bombCounter.setBackground(darkGreen);
		bombCounter.setForeground(Color.white);
		this.add(bombCounter);
		timer = new Timer(10, this);
		timer.start();
		
		// create exit button
		exit = new JButton("EXIT");
		exit.setFont(new Font("Monospaced", Font.BOLD, 30));
		exit.setBackground(darkGreen);
		exit.setForeground(Color.white);
		exit.setBorder(null);
		exit.setFocusable(false);
		exit.addActionListener(this);
		exit.setPreferredSize(new Dimension(100, 50));
		this.add(exit);
	}

	/**
	 *  Update the flag count
	 *  Exit if exit button is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// if exit is clicked, close program
		if (e.getSource() == exit) {
			System.exit(ABORT);
		}
		else{
			if (flagsLeft < 10) {
				bombCounter.setText("Flags Left: " + flagsLeft + " ");
			}
			else {
				bombCounter.setText("Flags Left: " + flagsLeft);
			}
		}
	}
}
