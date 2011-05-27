package nl.cs.uu.faceselector;

public class Matrix {

	protected double a1, a2, a3, b1, b2, b3, c1, c2, c3;

	public Matrix() {
	}

	public Matrix(
			double a1, double a2, double a3,
			double b1, double b2, double b3,
			double c1, double c2, double c3) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
	}

	public static Matrix rotation(double radians) {
		return new Matrix(
				Math.cos(radians), Math.sin(radians), 0,
				-Math.sin(radians), Math.cos(radians), 0,
				0, 0, 1);
	}

	public static Matrix scale(double scale) {
		return new Matrix(
				scale, 0, 0,
				0, scale, 0,
				0, 0, scale);
	}

	public Matrix cross(Matrix other) {
		// TODO
		return null;
	}

	public static Point2D cross(Point2D point, Matrix matrix) {
		Point2D result = new Point2D(0, 0);
		result.x = point.x * matrix.a1 + point.y * matrix.b1 + matrix.c1;
		result.y = point.x * matrix.a2 + point.y * matrix.b2 + matrix.c2;
		return result;
	}

	@Override
	public String toString() {
		return "Matrix {" +
				a1 + "," + a2 + "," + a3 + "\n        " +
				b1 + "," + b2 + "," + b3 + "\n        " +
				c1 + "," + c2 + "," + c3 + "}";
	}

}
