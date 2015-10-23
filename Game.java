/*
 * Title: DSA Assignment 15-16
 * Description: 2048 game
 * Student name: Lin Ziqiao 
 * Student ID: 6826257
 * Date: 27 Nov 2015 
 */

import java.util.*;
import java.io.*;

public class Game {

	private static final char null_char = '\u0000';
	private static final int grid_width = 4;
	private static char[][] grid;
	private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private int score;
	private static char[][] clone_grid;
	private int clone_score;
	private static final int undo_list_size = 20;
	private ArrayQueue undo_grid;
	private ArrayQueue undo_score;
	int counter = 0;

	// constructor for the Game class
	public Game(ArrayQueue undo_grid, ArrayQueue undo_score) {
		this.undo_grid = undo_grid;
		this.undo_score = undo_score;
		grid = new char[grid_width][grid_width];
		score = 0;
	}

	// display the grid and player's score
	public void display() {
		if (gridIsEmpty()) {
			init();
		}

		System.out.println("-----------------");
		for (int i = 0; i < grid.length; i++) {
			System.out.print("| ");
			for (int j = 0; j < grid[i].length; j++) {
				if (System.getProperty("os.name").contains("Windows")) { //for display on Window's cmd
					System.out.printf("%s | ", Character.toString(grid[i][j]));
				} else if (System.getProperty("os.name").contains("Mac")) { // for display on Mac os's terminal
					if (grid[i][j] == null_char) {
						System.out.printf(" %s | ", Character.toString(grid[i][j]));
					} else {
						System.out.printf("%s | ", Character.toString(grid[i][j]));
					}
				}
			}
			System.out.println();
			System.out.println("-----------------");
		}
		System.out.println("Score: " + score);
		System.out.println("Count: " + counter++);
	}

	// slide up function for the grid
	public void slideUp() {
		cloneArray();

		for (int j = 0; j < grid.length; j++) {
			for (int i = 0; i < grid.length; i++) {
				if (grid[i][j] != null_char) { // check if cell will empty
					if ((i + 1) < grid.length) { // check if array will outbound
						for (int k = i + 1; k < grid.length; k++) {
							if (grid[k][j] != null_char) { // check if cell will empty
								if (grid[i][j] == grid[k][j]) { // merge if two tiles are the same 
									grid[i][j] = mergeTile(grid[k][j]);
									grid[k][j] = null_char;
									break; // break for finished one mapping
								} else {
									break; //break for jump to next cell
								}
							}
						}
					}
				}
			}
		}

		for (int i = 1; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				int k = i;
				while (grid[k - 1][j] == null_char) {
					grid[k - 1][j] = grid[k][j];
					grid[k][j] = null_char;
					k--;
					if (k == 0) {
						break;
					}
				}
			}
		}

		if (moveValidate()) {
			randomFill();
			addToUndoList();
		}
	}

	// slide down function for the grid
	public void slideDown() {
		cloneArray();

		for (int j = 0; j < grid.length; j++) {
			for (int i = grid.length - 1; i >= 0; i--) {
				if (grid[i][j] != null_char) {
					if ((i - 1) >= 0) {
						for (int k = i - 1; k >= 0; k--) {
							if (grid[k][j] != null_char) {
								if (grid[i][j] == grid[k][j]) {
									grid[i][j] = mergeTile(grid[k][j]);
									grid[k][j] = null_char;
									break;
								} else {
									break;
								}
							}
						}
					}
				}
			}
		}

		for (int i = grid.length - 2; i >= 0; i--) {
			for (int j = 0; j < grid.length; j++) {
				int k = i;
				while (grid[k + 1][j] == null_char) {
					grid[k + 1][j] = grid[k][j];
					grid[k][j] = null_char;
					k++;
					if (k == 3) {
						break;
					}
				}
			}
		}

		if (moveValidate()) {
			randomFill();
			addToUndoList();
		}
	}

	// slide left function for the grid
	public void slideLeft() {
		cloneArray();

		for (int i = 0; i < grid.length; i++) {		
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] != null_char) {
					if ((j + 1) < grid.length) {
						for (int k = j + 1; k < grid.length; k++) {
							if (grid[i][k] != null_char) {
								if (grid[i][j] == grid[i][k]) {
									grid[i][j] = mergeTile(grid[i][k]);
									grid[i][k] = null_char;
									break;
								} else {
									break;
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < grid.length; i++) {
			for (int j = 1; j < grid.length; j++) {
				int k = j;
				while (grid[i][k - 1] == null_char) {
					grid[i][k - 1] = grid[i][k];
					grid[i][k] = null_char;
					k--;
					if (k == 0) {
						break;
					}
				}
			}
		}

		if (moveValidate()) {
			randomFill();
			addToUndoList();
		}
	}

	// slide right function for the grid
	public void slideRight() {
		cloneArray();

		for (int i = 0; i < grid.length; i++) {	
			for (int j = grid.length - 1; j >= 0; j--) {
				if (grid[i][j] != null_char) {
					if ((j - 1) >= 0) {
						for (int k = j - 1; k >= 0; k--) {
							if (grid[i][k] != null_char) {
								if (grid[i][j] == grid[i][k]) {
									grid[i][j] = mergeTile(grid[i][k]);
									grid[i][k] = null_char;
									break;
								} else {
									break;
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < grid.length; i++) {
			System.out.print(i);
			for (int j = grid.length - 2; j >= 0; j--) {
				int k = j;
				while (grid[i][k + 1] == null_char) {
					grid[i][k + 1] = grid[i][k];
					grid[i][k] = null_char;
					k++;
					if (k == 3) {
						break;
					}
				}
			}
		}

		if (moveValidate()) {
			randomFill();
			addToUndoList();
		}
	}

	// random fill tile function for the grid
	public void randomFill() {
		boolean filled = true;
		int x, y;
		char tile;
		Random rd1 = new Random();
		Random rd2 = new Random();
		Random rd3 = new Random();

		int ratio = rd3.nextInt(50) + 1;
		if (ratio <= 40) {
			tile = '0';
		} else if (ratio == 50) {
			tile = 'B';
		} else {
			tile = 'A';
		}

		while (filled) {
			x = rd1.nextInt(4);
			y = rd2.nextInt(4);
			if (grid[x][y] == null_char) {
				grid[x][y] = tile;
				filled = false;
			}
		}
	}

	// game over function for the grid
	public boolean gameover() {
		return true;
	}

	// undo function for grid
	public void undo() {
		if (undo_grid.length() > 0 && undo_score.length() > 0) {
			char[][] undo = new char[grid_width][grid_width];
			undo = (char[][])undo_grid.pop();
			for (int i = 0; i < undo.length; i++) {
			  	for (int j = 0; j < undo[i].length; j++) {
			    	grid[i][j] = undo[i][j];
			  	}
			}
			score = (int)undo_score.pop();
		} else {
			System.out.println("No moves can undo!");
		}
	}

	// reset the game
	public void reset() {
		grid = new char[grid_width][grid_width];
		clone_grid = new char[grid_width][grid_width];
		score = 0;
		clone_score = 0;
		emptyUndoLists();
	}

	// check the grid is empty or not
	public boolean gridIsEmpty() {
		boolean empty = false;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] != null_char) {
					empty = true;
					break;
				}
			}
		}
		if (empty) {
			return false;
		} else {
			return true;
		}
	}

	// initial the grid with random tile
	public void init() {
		randomFill();
		randomFill();
	}

	// using number to represent the letters,
	// used to calculate the score
	public int convertLetter(char c) {
		int x = 0;
		for (int i = 0; i < alphabet.length; i++) {
			if (c == alphabet[i]) {
				x =  i + 1;
				break;
			}
		}
		return x;
	}

	// return next letter of current one for merging tile
	public char mergeTile(char c) {
		score += Math.pow(2, convertLetter(c)); // calculate score
		return alphabet[convertLetter(c)];
	}

	// clone grid array for validation
	public void cloneArray() {
		clone_grid = new char[grid_width][grid_width];
		for (int i = 0; i < grid.length; i++) {
		  	for (int j = 0; j < grid[i].length; j++) {
		    	clone_grid[i][j] = grid[i][j];
		  	}
		}
		clone_score = score;
	}

	// moves validation
	public boolean moveValidate() {
		return !(Arrays.deepEquals(grid, clone_grid));
	}

	// add the grid and score to the undo list
	public void addToUndoList() {
		if (undo_grid.length() == undo_list_size && undo_score.length() == undo_list_size) {
			undo_grid.dequeue(); // remove the last grid and add the new grid
			undo_grid.enqueue(clone_grid); // add grid in to undo list

			undo_score.dequeue(); // remove the last score and add the new score
			undo_score.enqueue(clone_score); // add score in to undo list
		} else {
			undo_grid.enqueue(clone_grid);
			undo_score.enqueue(clone_score);
		}
	}

	// empty the undo lists if user had did the undo before
	public void emptyUndoLists() {
		while (undo_grid.length() > 0 && undo_score.length() > 0) {
			undo_grid.pop();
			undo_score.pop();
		}
	}

	public void start() {
		// read user"s input
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		// control the user"s input life cycle
		boolean cont = true;

		// print the menu and grid
		try {
			while(cont) {
				System.out.println();
				System.out.println("      2048");
				display();
				System.out.println("(2) Down");
				System.out.println("(4) Left");
				System.out.println("(6) Right");
				System.out.println("(8) Up");
				System.out.println("(0) Undo");
				System.out.println("(R) Reset");
				System.out.println("(Q) Quit");
				System.out.print("Which Move: ");
				String cmd = br.readLine();
				if (cmd.equals("2")) {
					slideDown();
				} else if (cmd.equals("4")) {
					slideLeft();
				} else if (cmd.equals("6")) {
					slideRight();
				} else if (cmd.equals("8")) {
					slideUp();
				} else if (cmd.equalsIgnoreCase("q")) {	
					System.out.println();
					System.out.println("Leave Game. Good Bye!!!");
					System.out.println();

					//end the user"s input life cycle
					cont = false;
				} else if (cmd.equals("0")) {
					undo();
				} else if (cmd.equalsIgnoreCase("r")) {
					reset();
				} else {
					System.out.println();
					System.out.println("Invalid Input! Please try again.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}