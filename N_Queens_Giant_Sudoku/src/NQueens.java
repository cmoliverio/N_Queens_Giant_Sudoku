import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NQueens {
	
	private static Random oracle;
	private static int board_size;
	private static int[] queens;
	private static boolean verbose;
	
	private static final int ceiling;
	
	static {
		oracle = new Random();
		ceiling = Integer.MAX_VALUE;
	}
	
	public NQueens(int number_of_queens, boolean verbose) {
		NQueens.board_size = number_of_queens;
		NQueens.verbose = verbose;
		
		queens = new int[NQueens.board_size];
		
		resetQueens();
	}
	
	/**
	 * Does not often find solution for N Queens,
	 * but finds a local minimum number of conflicts,
	 * which iterates until there are no better moves. 
	 */
	public void playHillClimbing() {
		
		System.out.println("Hill climbing begin. . .");
		
		int steps = 0;
		int conflicts = ceiling;
		
		long time_start = System.nanoTime();
		
		while(true) {
			steps++;
			
			conflicts = getConflicts(queens);
			
			List<Object> heuristic_data = generateSuccessors(queens.clone());
			
			int[][] successors = (int[][]) heuristic_data.get(0);
			int smallest_heuristic = (int) heuristic_data.get(1);
			
			// Move until there are no better moves
			if(conflicts > smallest_heuristic){
				ArrayList<Coordinate> possible_moves = findPossibleMoves(successors, smallest_heuristic);
				
				int move = oracle.nextInt(possible_moves.size());
				
				Coordinate next_queen = possible_moves.get(move);
				
				int row = next_queen.getRow();
				int column = next_queen.getColumn();
				
				queens[column] = row;
				
				if(verbose) {
					printBoard(conflicts);
				}
				
				continue;
			}
			
			break;
		}
		
		long time_end = System.nanoTime();
		
		long elapsed_time = time_end - time_start;
		
		printBoard(conflicts);
		System.out.println("Elapsed time: " + (double) elapsed_time / 1000000000 +
				"\tSteps: " + steps);
		System.out.println("");
		
		resetQueens();
	}
	
	/**
	 * Turns the queens[] board into a completed puzzle
	 * using an algorithm with controlled, slowly tempered
	 * random moves.  
	 */
	public void playSimulatedAnnealing() {
		
		System.out.println("Simulated Annealing begin. . . ");
		
		int steps = 0;
		int conflicts = ceiling;
		int max_possible_conflicts = ceiling;
		float temperature = 1.0f;
		
		long time_start = System.nanoTime();
		
		while(true) {
			steps++;
			
			conflicts = getConflicts(queens);
			
			if(steps == 1) {
				max_possible_conflicts = conflicts;
			}
			
			/*
			 * Temperature is the percentage of current conflicts
			 * over the total possible, which means as the board gets
			 * closer to being solved, and the number of conflicts goes
			 * down, we are less likely to choose a random move.  
			 */
			temperature = ((float) conflicts / max_possible_conflicts);
			
			// Grabs all the conflicts of all next possible moves. 
			List<Object> heuristic_data = generateSuccessors(queens.clone());
			
			/*
			 * Successors is map of conflicts in next move, move
			 * with least conflicts is also found.  
			 */
			int[][] successors = (int[][]) heuristic_data.get(0);
			int smallest_heuristic = (int) heuristic_data.get(1);
			
			// Don't stop until board is a solution.
			if(conflicts > 0) {
				float chance = oracle.nextFloat();
				
				/*
				 * Does random move if lucky, since temperature
				 * is percentage of current conflicts over total 
				 * possible conflicts, less likely to choose random 
				 * move as we get closer to a solution.
				 */
				if(chance <= temperature) {
					// Random indices
					int row = oracle.nextInt(board_size);
					int column = oracle.nextInt(board_size);
					
					// Random move
					queens[column] = row;
					
					if(verbose) {
						printBoard(conflicts);
					}
				}
				
				// Does best move if not lucky
				if(chance > temperature) {
					// List of possible moves with least possible conflicts
					ArrayList<Coordinate> possible_moves = findPossibleMoves(successors, smallest_heuristic);
					
					/*
					 * There might be multiple moves that lead to the 
					 * same minimum number of conflicts, choose one
					 * of them randomly.
					 */
					int move = oracle.nextInt(possible_moves.size());
					
					// Gets location object for smallest move
					Coordinate next_queen = possible_moves.get(move);
					
					// Indices
					int row = next_queen.getRow();
					int column = next_queen.getColumn();
					
					// Assign to current board
					queens[column] = row;
					
					if(verbose) {
						printBoard(conflicts);
					}
				}
				
				continue;
			}
			
			break;
		}
		
		// End of program. 
		long time_end = System.nanoTime();
		
		// Total time converted from nanoseconds to seconds
		long elapsed_time = time_end - time_start;
		double total_time = elapsed_time / 1000000000;
		
		// Output final info to user. 
		printBoard(conflicts);
		System.out.println("Elapsed time: " + total_time + "\tSteps: " + steps);
		System.out.println("");
		
		// Reset board
		resetQueens();
	}
	
	/**
	 * Returns locations of moves that match the number
	 * of conflicts, typically conflicts is the lowest
	 * value found by generateSuccessors()
	 * @param board
	 * @param smallest_heuristic
	 * @return
	 */
	public ArrayList<Coordinate> findPossibleMoves(int[][] board, int conflicts){		
		ArrayList<Coordinate> possible_moves = new ArrayList<Coordinate>();
		
		for(int i = 0; i < board_size; i++) {
			for(int j = 0; j < board_size; j++) {
				if(board[i][j] == conflicts) {
					possible_moves.add(new Coordinate(i, j));
				}
			}
		}
		
		return possible_moves;
	}
	
	/**
	 * For every spot in the board, finds the number
	 * of conflicts if the queen in that column
	 * was placed there, returns the board and 
	 * smallest number of conflicts found.  
	 * @param board
	 * @return
	 */
	public List<Object> generateSuccessors(int[] board) {
		int[][] future_conflicts = new int[board_size][board_size];
		int smallest_conflict = ceiling;
		
		// Initalizes board with current locations of queens
		for(int i = 0; i < board_size; i++) {
			future_conflicts[board[i]][i] = ceiling;
		}
		
		/*
		 * When we find a location that does not have a queen on it,
		 * compute the possible conflicts by passing into getConflicts(),
		 * then record 
		 */
		for(int row = 0; row < board_size; row++) {
			for(int column = 0; column < board_size; column++) {
				if(future_conflicts[row][column] == 0) {
					// Used to pass into getConflicts() to simulate actual board
					int[] temp_board = board.clone();
					
					// Put new queen location into temporary board
					temp_board[column] = row;
					
					// Conflicts are determined by attacking queens
					int conflicts = getConflicts(temp_board);
					
					// Record smallest seen conflicts
					if(conflicts < smallest_conflict) {
						smallest_conflict = conflicts;
					}
					
					// Assign future move conflicts into board
					future_conflicts[row][column] = conflicts;
				}
			}
		}
		
		return Arrays.asList(future_conflicts, smallest_conflict);
	}
	
	/**
	 * To check conflicts of board, check the conflicts
	 * on the rows and the diagonals, since there will 
	 * never be moves left or right, queens will never
	 * be on top of or below each other. 
	 * @param board
	 * @return
	 */
	public int getConflicts(int[] board) {
		return checkRows(board) + checkDiagonals(board);
	}
	
	/**
	 * Used https://www.google.com/url?sa=t&rct=j&q=&esrc=s&
	 * source=web&cd=&cad=rja&uact=8&ved=2ahUKEwi5uPaTo5XvAh
	 * XZF1kFHTDaAj4QFjAAegQIARAD&url=https%3A%2F%2Ftowardsd
	 * atascience.com%2Fcomputing-number-of-conflicting-pair
	 * s-in-a-n-queen-board-in-linear-time-and-space-complex
	 * ity-e9554c0e0645&usg=AOvVaw1SudMi10P7PG2icVILUbvo
	 * to figure out checking diagonal conflicts in 
	 * linear time. 
	 * @param board
	 * @return
	 */
	public int checkDiagonals(int[] board) {
		int result = 0;
		int[] frequency_positive = new int[board_size * 2];
		int[] frequency_negative = new int[board_size * 2];
		
		// For each diagonal up and rightwards, +1 for each queen
		for(int i = 0; i < board_size; i++) {
			int diagonal = board[i] + i;
			frequency_positive[diagonal]++;
		}
		
		// For each diagonal down and rightwards, +1 for each queen
		for(int i = 0; i < board_size; i++) {
			int diagonal = board_size - board[i] + i;
			frequency_negative[diagonal]++;
		}
		
		// For each diagonal, find number of conflicts and add to total
		for(int i = 0; i < board_size * 2; i++) {
			int positive_diagonal = frequency_positive[i];
			int negative_diagonal = frequency_negative[i];
			result += positive_diagonal * (positive_diagonal - 1) / 2;
			result += negative_diagonal * (negative_diagonal - 1) / 2;
		}
		
		return result;
	}
	
	/**
	 * Used https://www.google.com/url?sa=t&rct=j&q=&esrc=s&
	 * source=web&cd=&cad=rja&uact=8&ved=2ahUKEwi5uPaTo5XvAh
	 * XZF1kFHTDaAj4QFjAAegQIARAD&url=https%3A%2F%2Ftowardsd
	 * atascience.com%2Fcomputing-number-of-conflicting-pair
	 * s-in-a-n-queen-board-in-linear-time-and-space-complex
	 * ity-e9554c0e0645&usg=AOvVaw1SudMi10P7PG2icVILUbvo
	 * to figure out checking row conflicts in 
	 * linear time. 
	 * @param board
	 * @return
	 */
	public int checkRows(int[] board) {
		int result = 0;
		int[] frequency = new int[board_size];
		
		// For each queen, +1 for the row it is located
		for(int i = 0; i < board_size; i++) {
			int row = board[i];
			frequency[row]++;
		}
		
		// For each row, find number of conflicts and add to total
		for(int i = 0; i < board_size; i++) {
			result += frequency[i] * (frequency[i] - 1) / 2;
		}
		
		return result;
	}
	
	public void resetQueens() {
		int depth = 0;
		for(int i = 0; i < board_size; i++) {
			queens[i] = depth;
			depth++;
		}
	}
	
	public void printBoard(int conflicts) {
		System.out.println("Conflicts: " + conflicts + "    Queens: " + Arrays.toString(queens));
	}
	

}
