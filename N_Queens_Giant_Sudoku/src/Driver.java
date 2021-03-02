/**
 * @author Christian Oliverio
 * @date 23 February 2021
 * @project Project 2
 * @description Project fits n number of queens into an n x n grid
 * or solves a sudoku of size 25 x 25
 */

import java.util.Scanner;

public class Driver {
	
	public static final int number_of_queens_high_bound;
	public static final int number_of_queens_low_bound;
	
	static {
		number_of_queens_high_bound = 200;
		number_of_queens_low_bound = 8;
	}
	
	public static boolean invalidInput(int value, int low_bound, int high_bound) {
		return (value < low_bound || value > high_bound) ? true : false;
	}
	
	public static void main(String[] args) {
		Scanner keys = new Scanner(System.in);
		int game = -1;
		
		while(invalidInput(game, 0, 1)) {
			System.out.println("Enter 0 for N-Queens, enter 1 for sudoku:");
			game = keys.nextInt();
		}
		
		if (game == 0) {
			int number_of_queens = -1;
			while(invalidInput(number_of_queens, 8, 200)) {
				System.out.println("Enter the number of queens, 8-200:");
				number_of_queens = keys.nextInt();
			}
			NQueens queen_game = new NQueens(number_of_queens);
		}
		
		if(game == 1) {
			String txt_file = "";
		}
		
	}
}
