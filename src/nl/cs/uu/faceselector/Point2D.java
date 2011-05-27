package nl.cs.uu.faceselector;

import org.eclipse.swt.graphics.Point;

/**
 * Simple {@link Point} class for {@link Double}s.
 * 
 * @author Paul Lammertsma
 */
public class Point2D {

	public double x;
	public double y;

	/**
	 * Creates a new {@link Point2D} from two {@link Double}s
	 * 
	 * @param x
	 * @param y
	 */
	public Point2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public Point toPoint() {
		return new Point((int) Math.round(this.x), (int) Math.round(this.y));
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
