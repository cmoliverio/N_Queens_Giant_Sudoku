/**
 * @author Christian Oliverio, Benajmin Bowering, Jack Peterson
 * @date 23 February 2021
 * @project Project 2
 * @description Project fits n number of queens into an n x n grid
 * or solves a sudoku of size 25 x 25
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Driver {
	
	public static final int number_of_queens_high_bound;
	public static final int number_of_queens_low_bound;
	
	public static Scanner keys;
	
	static {
		keys = new Scanner(System.in);
		number_of_queens_high_bound = 200;
		number_of_queens_low_bound = 8;
	}
	
	public static boolean invalidInput(int value, int low_bound, int high_bound) {
		return (value < low_bound || value > high_bound) ? true : false;
	}
	
	public static int getNumberOfQueens() {
		int number_of_queens = -1;
		
		while(invalidInput(number_of_queens, 8, 200)) {
			System.out.println("Enter the number of queens, 8-200:");
			number_of_queens = keys.nextInt();
		}
		return number_of_queens;
	}
	
	public static boolean getVerbose() {
		int verbose = -1;
		
		while(invalidInput(verbose, 0, 1)) {
			System.out.println("Verbose output? 0 for no, 1 for yes");
			verbose = keys.nextInt();
		}
		
		return verbose != 0;
	}
	
	public static BufferedReader getFile() {
		FileReader file;
		BufferedReader reader;
		
		System.out.println("Enter the name of the csv file with Sudoku puzzle (without .csv file extension)");
		while(true) {
			try {
				String file_name = keys.nextLine();
				file = new FileReader(file_name + ".csv");
				reader = new BufferedReader(file);
			} catch (FileNotFoundException e) {
				System.out.println("File does not exist. Try again.");
				
				continue;
			}
			
			break;
		}
		
		return reader;
	}
	
	public static void main(String[] args) {
		Scanner keys = new Scanner(System.in);
		int game = -1;
		
		while(invalidInput(game, 0, 1)) {
			System.out.println("Enter 0 for N-Queens, enter 1 for sudoku:");
			game = keys.nextInt();
		}
		
		if (game == 0) {
			
			int number_of_queens = getNumberOfQueens();
			boolean verbose = getVerbose();
			
			NQueens queen_game = new NQueens(number_of_queens, verbose);
			
			queen_game.playHillClimbing();
			queen_game.playSimulatedAnnealing();
		}
		
		if(game == 1) {
			
			BufferedReader file = getFile();
			boolean verbose = getVerbose();
			
		    String line;
		    int[][] board = new int[25][25];
		    int depth = 0;
		    
		    try {
				while ((line = file.readLine()) != null) {
				    String[] values = line.split(",");
				    
				    for (int i = 0; i < values.length; i++) {
				    	board[depth][i] = Integer.valueOf(values[i]);
				    }
				    depth++;
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    Sudoku sudoku_game = new Sudoku(board, verbose);
		    
		    sudoku_game.playHillClimbing(board.clone());
		    sudoku_game.playSimulatedAnnealing(board.clone());
		}
		
	}
}
