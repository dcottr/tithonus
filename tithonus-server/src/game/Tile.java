package game;

public class Tile {
	public final int x;
	public final int y;
	public Ant ant;
	
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean obstacle() {
		return ant != null;
	}
}
