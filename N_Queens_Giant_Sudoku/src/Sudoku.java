import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
	ArrayList<int[]> fixedValues = new ArrayList<int[]>();
	boolean verbose = false;

	public Sudoku(int[][] board, boolean verbose) {
		this.verbose = verbose;
		updatefixedValues(board)
;		populateBoard(board);
		int[][] board_copy = board.clone();
		//playHillClimbing(board);
		//playSimulatedAnnealing(board_copy);
	}

	public void playHillClimbing(int[][] board) {

		System.out.println("Hill climbing begin. . .");
		
		int previous_conflicts = 0;
		boolean repeating;
		int repeats = 0;

		long time_start = System.nanoTime();

		while(repeats < 10) {
			//iterates through boxes on sudoku board
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {

					//iterates through tiles in boxes on sudoku board
					for (int y = 0; y < 5; y++) {
						for (int x = 0; x < 5; x++) {
							if (!isFixedValue(new int[] {5 * i + y,5 * j + x})) {
								int[] best_swap = evaluateSuccessors(board,j,i,x,y);
								int temp = board[i * 5 + best_swap[0]][j * 5 + best_swap[1]];
								board[i * 5 + best_swap[0]][j * 5 + best_swap[1]] = board[i * 5 + y][j * 5 + x];
								board[i * 5 + y][j * 5 + x] = temp;
								int current_conflicts = boardConflicts(board);
								if (current_conflicts == previous_conflicts) {
									repeats++;
								} else {
									repeats = 0;
									previous_conflicts = current_conflicts;
								}

							}
						}
					}

				}
			}

		}
		long time_end = System.nanoTime();
		long elapsed_time = time_end - time_start;		
		printBoard(board);
		System.out.println("Elapsed time: " + (double) elapsed_time / 1000000000);
		if (boardConflicts(board) == 0) {
			System.out.println("Zero Conflicts! The Sudoku Puzzle has been solved.");
		}
		System.out.println("Suboptimal state. There are still "+ boardConflicts(board) + " conflicts remaining.");
	}

	public void playSimulatedAnnealing(int[][] board) {

		System.out.println("Simulated Annealing begin. . . ");
		int maxBoardConflicts = boardConflicts(board);
		float temperature;
		long time_start = System.nanoTime();
		while(boardConflicts(board) != 0) {
			//iterates through boxes on sudoku board
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {

					//iterates through tiles in boxes on sudoku board
					for (int y = 0; y < 5; y++) {
						for (int x = 0; x < 5; x++) {
							if (!isFixedValue(new int[] {5 * i + y,5 * j + x})) {
								int[] best_swap = evaluateSuccessors(board,j,i,x,y);
								Random r = new Random();
								temperature = (float) boardConflicts(board)/maxBoardConflicts * 0.1f; 
								float chance = r.nextFloat();
								if (chance <= temperature) {
									best_swap[0] = r.nextInt(5);
									best_swap[1] = r.nextInt(5);
									while(isFixedValue(new int[] {i * 5 + best_swap[0],j * 5 + best_swap[1]})) {
										best_swap[0] = r.nextInt(5);
										best_swap[1] = r.nextInt(5);	
									}
								}
								int temp = board[i * 5 + best_swap[0]][j * 5 + best_swap[1]];
								board[i * 5 + best_swap[0]][j * 5 + best_swap[1]] = board[i * 5 + y][j * 5 + x];
								board[i * 5 + y][j * 5 + x] = temp;
							}
						}
					}
				}

			}
System.out.println("Conflicts: "+ boardConflicts(board));
		}
		long time_end = System.nanoTime();
		long elapsed_time = time_end - time_start;		
		printBoard(board);
		System.out.println("Elapsed time: " + (double) elapsed_time / 1000000000);
		if (boardConflicts(board) == 0) {
			System.out.println("Zero Conflicts! The Sudoku Puzzle has been solved.");
		}
		System.out.println("Suboptimal state. There are still "+ boardConflicts(board) + " conflicts remaining.");
	}

	//method that evaluates swapping all other numbers in tile with current number and returns coordinates of the best one to swap with
	public int[] evaluateSuccessors(int[][] board, int grid_x, int grid_y, int current_x, int current_y) {

		int[][] conflict_array = new int[5][5];

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				int[][] tempboard = new int[25][25];
				for (int i = 0; i < 25; i++) {
					for (int j = 0; j < 25; j++) {
						tempboard[i][j] = board[i][j];
					}
				}				


				int global_x = grid_x * 5 + x;
				int global_y = grid_y * 5 + y;
				if ((current_x != x || current_y != y) && !isFixedValue(new int[] {global_y,global_x})) {
					int temp = board[5 * grid_y + y][5 * grid_x + x];
					tempboard[5 * grid_y + y][5 * grid_x + x] = board[5 * grid_y + current_y][5 * grid_x + current_x];
					tempboard[5 * grid_y + current_y][5 * grid_x + current_x] = temp;
					conflict_array[y][x] = boardConflicts(tempboard);
					if (verbose) {
						printBoard(tempboard);
					}
				} else {
					conflict_array[y][x] = Integer.MAX_VALUE;
				}
			}
		}


		int best_x = current_x;
		int best_y = current_y;
		int conflicts = boardConflicts(board);

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				if (conflict_array[y][x] < conflicts) {
					best_x = x;
					best_y = y;
					conflicts = conflict_array[y][x];
				}
			}
		}
		return new int[] {best_y,best_x};
	}

	public boolean isFixedValue(int[] coordinates) {
		for (int i = 0; i < fixedValues.size(); i++) {
			int[] fixedValueCoordinates = fixedValues.get(i);
			if (fixedValueCoordinates[0] == coordinates[0] && fixedValueCoordinates[1] == coordinates[1]) {
				return true;
			}
		}
		return false;
	}

	//adds the coordinates of the unmovable numbers already on the sudoku board
	public void updatefixedValues(int board[][]) {
		for(int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] != 0) {
					fixedValues.add(new int[] {i,j});
				}
			}
		}
	}

	//prints the sudoku board
	public void printBoard(int[][] board) {
		System.out.println();
		for(int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] < 10) {
					System.out.print("  " + board[i][j] + "  ");
				} else {
					System.out.print("  " + board[i][j] + " ");
				}
			}
			System.out.println();
		}
	}

	//method that randomly populates the Sudoku board with values that make each 5x5 grid have all numbers
	public int[][] populateBoard(int[][] board) {

		Random r = new Random();

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				ArrayList<Integer> existingNumbers = new ArrayList<Integer>();

				//adds existing integers in each grid to ArrayList
				for (int y = 0; y < 5; y++) {
					for (int x = 0; x < 5; x++) {
						if (board[5 * j + y][5 * i + x] != 0) {
							existingNumbers.add(board[5 * j + y][5 * i + x]);
						}
					}
				}

				for (int y = 0; y < 5; y++) {
					for (int x = 0; x < 5; x++) {
						if (board[5 * j + y][5 * i + x] == 0) {
							while (true) {
								int number = r.nextInt(25) + 1;
								if (!existingNumbers.contains(number)) {
									board[5 * j + y][5 * i + x] = number;
									existingNumbers.add(number);
									break;
								}
							}
						}
					}
				}


			}
		}

		return board;
	}

	//determines the total number of conflicts on a sudoku board
	public int boardConflicts(int[][] board) {
		int rows_conflicts = 0;
		int columns_conflicts = 0;

		for (int i = 0; i < 25; i++) {
			ArrayList<Integer> existingNumbers = new ArrayList<Integer>();
			for (int j = 0; j < 25; j++) {
				if (board[i][j] != 0) {
					if (!existingNumbers.contains(board[i][j])) {
						existingNumbers.add(board[i][j]);
					} else {
						rows_conflicts++;
					}
				}
			}
		}

		for (int i = 0; i < 25; i++) {
			ArrayList<Integer> existingNumbers = new ArrayList<Integer>();
			for (int j = 0; j < 25; j++) {
				if (board[j][i] != 0) {
					if (!existingNumbers.contains(board[j][i])) {
						existingNumbers.add(board[j][i]);
					} else {					
						columns_conflicts++;
					}
				}
			}
		}

		return rows_conflicts + columns_conflicts;
	}
}
