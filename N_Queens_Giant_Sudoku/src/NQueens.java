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
		
		addQueens();
	}
	
	public void playHillClimbing() {
		
		System.out.println("Hill climbing begin. . .");
		
		int steps = 0;
		int conflicts = ceiling;
		
		long time_start = System.nanoTime();
		
		while(true) {
			steps++;
			
			conflicts = getConflicts(queens);
			
			List<Object >heuristic_data = generateSuccessors(queens.clone());
			
			int[][] successors = (int[][]) heuristic_data.get(0);
			int smallest_heuristic = (int) heuristic_data.get(1);
			
			if(conflicts > smallest_heuristic){
				ArrayList<Coordinate> possible_moves = findSmallestMoves(successors, smallest_heuristic);
				
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
		
		addQueens();
	}
	
	public void playSimulatedAnnealing() {
		
		System.out.println("Simulated Annealing begin. . . ");
		
		int steps = 0;
		int conflicts = ceiling;
		int temperature = board_size * board_size;
		
		long time_start = System.nanoTime();
		
		while(true) {
			steps++;
			
			conflicts = getConflicts(queens);
			
			List<Object >heuristic_data = generateSuccessors(queens.clone());
			
			int[][] successors = (int[][]) heuristic_data.get(0);
			int smallest_conflict_move = (int) heuristic_data.get(1);
			
			temperature = (int) (Math.random() * (conflicts - smallest_conflict_move + 1) + smallest_conflict_move);
			
			int largest_allowed_heuristic = temperature;
			
			if(conflicts >= smallest_conflict_move){
				ArrayList<Coordinate> possible_moves = findPossibleMoves(successors, largest_allowed_heuristic);
				
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
		
		addQueens();
	}
	
	public ArrayList<Coordinate> findPossibleMoves(int[][] board, int greatest_allowed_heuristic){		
		ArrayList<Coordinate> possible_moves = new ArrayList<Coordinate>();
		
		for(int i = 0; i < board_size; i++) {
			for(int j = 0; j < board_size; j++) {
				if(board[i][j] <= greatest_allowed_heuristic) {
					possible_moves.add(new Coordinate(i, j));
				}
			}
		}
		
		return possible_moves;
	}
	
	public ArrayList<Coordinate> findSmallestMoves(int[][] board, int smallest_heuristic){		
		ArrayList<Coordinate> possible_moves = new ArrayList<Coordinate>();
		
		for(int i = 0; i < board_size; i++) {
			for(int j = 0; j < board_size; j++) {
				if(board[i][j] == smallest_heuristic) {
					possible_moves.add(new Coordinate(i, j));
				}
			}
		}
		
		return possible_moves;
	}
	
	public List<Object> generateSuccessors(int[] board) {
		int[][] heuristics = new int[board_size][board_size];
		int smallest_heuristic = ceiling;
		
		for(int i = 0; i < board_size; i++) {
			heuristics[board[i]][i] = ceiling;
		}
		
		int[] temp_board = board.clone();
		
		for(int row = 0; row < board_size; row++) {
			for(int column = 0; column < board_size; column++) {
				if(heuristics[row][column] == 0) {
					temp_board = board.clone();
					temp_board[column] = row;
					
					int heuristic = getConflicts(temp_board);
					
					if(heuristic < smallest_heuristic) {
						smallest_heuristic = heuristic;
					}
					
					heuristics[row][column] = heuristic;
				}
			}
		}
		
		return Arrays.asList(heuristics, smallest_heuristic);
	}
	
	public int getConflicts(int[] board) {
		return checkRows(board) + checkDiagonals(board);
	}
	
	// Looked at towardsdatascience.com/computing-number-of-conflicting-pairs-
	// in-a-n-queen-board-in-linear-time-and-space-complexity
	// Couldn't grab link atm because wifi is down, had to use phone.  
	public int checkDiagonals(int[] board) {
		int result = 0;
		int[] frequency_positive = new int[board_size * 2];
		int[] frequency_negative = new int[board_size * 2];
		
		for(int i = 0; i < board_size; i++) {
			int diagonal = board[i] + i;
			frequency_positive[diagonal]++;
		}
		
		for(int i = 0; i < board_size; i++) {
			int diagonal = board_size - board[i] + i;
			frequency_negative[diagonal]++;
		}
		
		for(int i = 0; i < board_size * 2; i++) {
			int positive_diagonal = frequency_positive[i];
			int negative_diagonal = frequency_negative[i];
			result += positive_diagonal * (positive_diagonal - 1) / 2;
			result += negative_diagonal * (negative_diagonal - 1) / 2;
		}
		
		return result;
	}
	
	// Looked at towardsdatascience.com/computing-number-of-conflicting-pairs-
	// in-a-n-queen-board-in-linear-time-and-space-complexity
	// Couldn't grab link atm because wifi is down, had to use phone.  
	public int checkRows(int[] board) {
		int result = 0;
		int[] frequency = new int[board_size];
		
		for(int i = 0; i < board_size; i++) {
			int row = board[i];		// Gets row of queen
			frequency[row]++;			// Adds number of queens to that row
		}
		
		for(int i = 0; i < board_size; i++) {
			result += frequency[i] * (frequency[i] - 1) / 2;
		}
		
		return result;
	}
	
	public void addQueens() {
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
