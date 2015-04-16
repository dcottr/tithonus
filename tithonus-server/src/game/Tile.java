package game;

public class Tile {
	public final int x;
	public final int y;
	public Ant ant;
	private boolean obstacle = false;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean filled() {
		return obstacle() || ant != null;
	}
	
	public boolean obstacle() {
		return obstacle;
	}
	
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}
	
	public String toString() {
		return "Tile:  " + "X: " + x + "  Y: " + y + ((ant == null)? "" : ("  ANT: " + ant.antID));
	}
}
