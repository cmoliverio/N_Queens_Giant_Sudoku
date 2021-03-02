import java.util.Arrays;

public class NQueens {
	
	int board_size;
	int number_of_queens;
	int[][] board;
	
	public NQueens(int number_of_queens) {
		this.board_size = number_of_queens;
		this.number_of_queens = number_of_queens;
		
		board = new int[this.board_size][this.board_size];
		
		addQueens();
		printBoard();
	}
	
	public void addQueens() {
		int depth = 0;
		for(int i = 0; i < board_size; i++) {
			board[i][depth] = 1;
			depth++;
		}
	}
	
	public void printBoard() {
		for(int i = 0; i < board_size; i++) {
			System.out.println(Arrays.toString(board[i]));
		}
	}
	

}
