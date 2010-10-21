package nl.cs.uu.faceselector;

public class Point2D {

	public double x;
	public double y;

	public Point2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
