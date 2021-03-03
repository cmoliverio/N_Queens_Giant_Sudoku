
public class Coordinate {
	
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int[] getCoordinates() {
		return new int[] {x, y};
	}
	
	public int getRow() {
		return x;
	}
	
	public int getColumn() {
		return y;
	}
}