package ca.uvic.cs.chisel.nerf.model;

public class Throw {

	int x;
	int y;
	int point;
	
	public Throw(int x, int y, int point) {
		this.x = x;
		this.y = y;
		this.point = point;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getPoint() {
		return point;
	}
	
	@Override
	public String toString() {
		return point + " (" + x + "," + y + ")";
	}
}