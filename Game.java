package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Game extends JPanel implements ActionListener, MouseListener{

	// game tracking booleans
	boolean gameStarted;
	boolean gameOver;
	boolean gameWon;
	
	// Colors
	Color lightGreen = new Color(170, 215, 81);
	Color darkGreen = new Color(162, 209, 73);
	Color mouseHover = new Color(200, 250, 80);
	Color darkRed = new Color(230, 51, 7);
	Color lightRed = new Color(242, 54, 7);
	Color lightGray = new Color(229, 194, 159);
	Color darkGray = new Color(215, 184, 153);
	
	// timer for ActionListener
	Timer timer;
	
	// tracking the mouses position when clicking
	int mouseRowIndex;
	int mouseColIndex;
	
	// contains where the flags and bombs are
	ArrayList<Point> flags;
	ArrayList<Point> bombs;
	
	// contains each square's values
	int[][] squareValues;
	
	// contains whether or not each square should be shown
	boolean[][] showSquare;

	// constructor for game panel
	Game(){
		
		// set the basics of the panel and add mouseListener
		this.setPreferredSize(new Dimension(720, 560));
		this.setLayout(new FlowLayout());
		this.addMouseListener(this);
		
		// create and start timer
		timer = new Timer(10, this);
		timer.start();
		
		// game not started yet
		gameStarted = false;
	
		// instantiate lists/arrays
		flags = new ArrayList<Point>();
		bombs = new ArrayList<Point>();
		squareValues = new int[18][14];
		showSquare = new boolean[18][14];
	}
	
	/**
	 *  When the game is over (won/lost), all values are reset so 
	 *  	the user can play another game
	 */
	public void reset() {
		gameStarted = false;
		gameOver = false;
		gameWon = false;
		
		flags = new ArrayList<Point>();
		bombs = new ArrayList<Point>();
		
		squareValues = new int[18][14];
		showSquare = new boolean[18][14];
		repaint();
	}

	/**
	 * Paints the background, flags, numbers, bombs, etc
	 */
	protected void paintComponent(Graphics g) {
		
		// first, paint checkered background
		boolean useLightGreen = false; // tracks whether a square is light/dark green
		
		// loop through each box
		for (int column = 0; column < 18; column++) { 
			useLightGreen = !useLightGreen;
			for (int row = 0; row < 14; row++) {
				
				// if that square should be shown, show it 
				if (showSquare[column][row]) {
					if (bombs.contains(new Point(column, row))) {
						g.setColor(Color.red);
						g.fillRect(column * 40, row * 40, 40, 40); // bombs are red squares
					}
					else { // box is not a bomb
						if (useLightGreen) {
							g.setColor(lightGray); // light gray corresponds with the light green (and vice versa)
						}
						else {
							g.setColor(darkGray);
						}
						g.fillRect(column * 40, row * 40, 40, 40); 
						
						// show the number of the square (if 0, leave blank)
						if (squareValues[column][row] > 0) {
							g.setColor(Color.white);
							g.drawString(""+squareValues[column][row], column * 40 + 20, row * 40 + 20);
						}
						
					}
				}
				
				// if the mouse is hovering over a box, make it a lighter color
				else if (column == mouseColIndex && row == mouseRowIndex && !gameOver) {
					g.setColor(mouseHover);
					g.fillRect(column * 40, row * 40, 40, 40);
				}
				
				// otherwise, just paint the default box color (green/light green)
				else {
					if (useLightGreen) {
						g.setColor(lightGreen);
					}
					else {
						g.setColor(darkGreen);
					}
					g.fillRect(column * 40, row * 40, 40, 40);
				}
				useLightGreen = !useLightGreen;
				
			}
		}
		
		// paint flags
		for (Point box : flags) {
			g.setColor(darkRed);
			g.fillRect(box.x * 40 + 10, box.y * 40 + 5, 4, 25);
			g.fillRect(box.x * 40 + 8, box.y * 40 + 30, 8, 5);
			g.setColor(lightRed);
			int[] xPoints = {box.x * 40 + 14, box.x * 40 + 30, box.x *40 + 14};
			int[] yPoints = {box.y * 40 + 5, box.y * 40 + 13, box.y * 40 + 21};
			g.fillPolygon(xPoints, yPoints, 3);
		}
		
		// check if game over. If true, let user know
		if (gameOver) {
			g.setColor(Color.white);
			g.setFont(new Font("Monospaced", Font.BOLD, 100));
			g.drawString("GAME OVER", 100, 150);
			g.setFont(new Font("Monospaced", Font.BOLD, 40));
			g.drawString("Right Click Mouse to Reset", 50, 300);
		}
		
		// check if game won. If true, let user know
		if (gameWon) {
			g.setColor(Color.white);
			g.setFont(new Font("Monospaced", Font.BOLD, 100));
			g.drawString("YOU WON!", 100, 150);
			g.setFont(new Font("Monospaced", Font.BOLD, 40));
			g.drawString("Right Click Mouse to Reset", 50, 300);
		}
		
		
		
	}
	
	/**
	 * When the game is started, the bombs will be created
	 * @param boxClicked
	 * boxClicked or its surrounding boxes can NOT have bombs
	 */
	public void placeBombs(Point boxClicked) {
		
		// create list of points where there can not be bombs
		ArrayList<Point> noBombs = new ArrayList<Point>(); 
		noBombs.add(boxClicked); 
		noBombs.add(new Point(boxClicked.x-1, boxClicked.y-1));
		noBombs.add(new Point(boxClicked.x, boxClicked.y-1));
		noBombs.add(new Point(boxClicked.x+1, boxClicked.y-1));
		noBombs.add(new Point(boxClicked.x-1, boxClicked.y));
		noBombs.add(new Point(boxClicked.x+1, boxClicked.y));
		noBombs.add(new Point(boxClicked.x-1, boxClicked.y+1));
		noBombs.add(new Point(boxClicked.x+1, boxClicked.y+1));
		noBombs.add(new Point(boxClicked.x, boxClicked.y+1));

		// until all bombs are placed, keep checking if a new random square is valid
		while(bombs.size() < 40) {
			int columnIndex = (int) (Math.random() * 18); // num from 0 to 17
			int rowIndex = (int) (Math.random() * 14); // num from 0 to 13 
			Point bombPoint = new Point(columnIndex, rowIndex);
			if (noBombs.contains(bombPoint) || bombs.contains(bombPoint)){
				continue;
			}
			bombs.add(new Point(columnIndex, rowIndex));
		}
		
		// find each square's numerical value
		findSquareValues();
		
		// show the initial move
		showMove(boxClicked);
	}
	
	/**
	 * Finds the number value for each box on the grid
	 * negative number means bomb
	 * 0-8 represents the amount of bombs in its vicinity
	 */
	public void findSquareValues() {
		for (Point bomb: bombs) {
			int x = bomb.x;
			int y = bomb.y;
			squareValues[x][y] = -10;
			
			if (x > 0 && y > 0) squareValues[x-1][y-1]++;
			if (x > 0 ) squareValues[x-1][y]++;
			if (x > 0 && y < 13) squareValues[x-1][y+1]++;
			if (y > 0) squareValues[x][y-1]++;
			if (y < 13)squareValues[x][y+1]++;
			if (x < 17 && y > 0) squareValues[x+1][y-1]++;
			if (x < 17) squareValues[x+1][y]++;
			if (x < 17 && y < 13) squareValues[x+1][y+1]++;
		}
		
	}
	
	/**
	 * Shows the result of a player's move
	 * @param boxClicked
	 */
	public void showMove(Point boxClicked) {		
		
		// if point is flagged
		if (flags.contains(boxClicked)) {
			
			// do nothing
		}
		
		// if point is bomb, show the bombs and set game over to true
		else if (bombs.contains(boxClicked)) {
			showBombs();
			repaint();
			gameOver = true;
		}
		
		// point is not bomb
		else {
			int x = boxClicked.x;
			int y = boxClicked.y;
			
			// if the value is greater than 0, show that box
			if (squareValues[x][y] > 0) { 
				showSquare[x][y] = true;
			}
			
			// if the value is 0, show that box and recursively call each surrounding point's showMove until each is > 0
			else if (!showSquare[x][y]) {
				showSquare[x][y] = true; 
				if (x > 0 && y > 0) showMove(new Point(x-1, y-1));
				if (x > 0 ) showMove(new Point(x-1,y));
				if (x > 0 && y < 13) showMove(new Point(x-1,y+1));
				if (y > 0) showMove(new Point(x,y-1));
				if (y < 13) showMove(new Point(x,y+1));
				if (x < 17 && y > 0) showMove(new Point(x+1, y-1));
				if (x < 17) showMove(new Point(x+1, y));
				if (x < 17 && y < 13) showMove(new Point(x+1,y+1));
			}
		}
		
		// check if the user won
		checkGameWon();
	}
	
	/**
	 *  sets gameWon to true if user has clicked all non-bombs
	 */
	public void checkGameWon() {
		
		// iterates through each box. Checks if it is not a bomb and it has been clicked
		for (int i = 0; i < showSquare.length; i++) {
			for (int j = 0; j < showSquare[i].length; j++) {
				if (squareValues[i][j] >= 0) {
					if (!(showSquare[i][j])) {
						return;
					}
				}
			}
		}
		gameWon = true;
	}
	
	/**
	 *  When game is lost, show user all bombs
	 */
	public void showBombs() {
		for (Point bomb : bombs) {
			showSquare[bomb.x][bomb.y] = true;
		}
	}
	
	/**
	 * Every time the timer ticks, update the mouse position
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePos, this);
		mouseColIndex = mousePos.x / 40;
		mouseRowIndex = mousePos.y / 40;
		repaint();
	}
	

	@Override // unused
	public void mouseClicked(MouseEvent e) {}

	@Override // unused
	public void mousePressed(MouseEvent e) {}

	/**
	 *  Controls the user's moves: flagging, unflagging, and clicking
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		
		// if user left clicked and the game is still going
		if (e.getButton() == MouseEvent.BUTTON1 && !gameOver && !gameWon) {
			
			// get mouses position
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(mousePos, this);
			
			// if game is not started, start it. If it is, show the move 
			if (!gameStarted) {
				gameStarted = true;
				placeBombs(new Point(mousePos.x/40, mousePos.y/40));
			}
			else {
				showMove(new Point(mousePos.x/40, mousePos.y/40));
			}
		}
		
		// if the user right clicks and the game has begun
		else if (e.getButton() == MouseEvent.BUTTON3 && gameStarted) {
			
			// if the game is not over
			if (!gameWon && !gameOver) {
				
				// get the mouse position and store it as a point
				Point mousePos = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(mousePos, this);
				Point boxClicked = new Point(mousePos.x/40, mousePos.y/40);
				
				// unflag if already flagged
				if (flags.contains(boxClicked)) {
					flags.remove(flags.indexOf(boxClicked));
					Header.flagsLeft++;
				}
				
				// flag if not flagged (and there are flags left)
				else if (!(showSquare[boxClicked.x][boxClicked.y]) && Header.flagsLeft > 0){
					flags.add(boxClicked);
					Header.flagsLeft--;
				}
			}
			
			// if game is over, rest flags and the panel
			else {
				Header.flagsLeft = 40;
				reset();
			}
		}
		
	}

	@Override // unused
	public void mouseEntered(MouseEvent e) {}

	@Override // unused
	public void mouseExited(MouseEvent e) {}	
}
