package nl.cs.uu.faceselector;

import java.util.LinkedList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class MatrixMath {

	public static final double RADIANS_TO_DEGREES = 180 / Math.PI;

	public static double dot(final Point2D p1, final Point2D p2) {
		return p1.x * p2.x + p1.y * p2.y;
	}

	public static Point2D norm(final Point2D point) {
		if (point == null) {
			return null;
		}
		final double len = Math.sqrt(Math.pow(point.x, 2)
				+ Math.pow(point.y, 2));
		return new Point2D(point.x / len, point.y / len);
	}

	public static double distance(final Point a, final Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static double distance(final Point2D a, final Point2D b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static void rotate(AnnotationData curData, final double radians) {
		Matrix matrix = Matrix.rotation(radians);
		matrix(curData, matrix);
	}

	public static void scale(AnnotationData curData, final double scale) {
		Matrix matrix = Matrix.scale(scale);
		matrix(curData, matrix);
	}

	public static double stdDev(int count, double sum1, double sum2) {
		return Math.sqrt(count * sum2 - Math.pow(sum1, 2))
				/ (count * (count - 1));
	}

	public static void matrix(AnnotationData curData, Matrix matrix) {
		int x = 0, y = 0;
		if (Fields.TRANSLATE_TO_ORIGIN) {
			Rectangle bounds = Face.getBoundingBox(curData.manual);
			x = bounds.x + bounds.width / 2;
			y = bounds.y + bounds.height / 2;
		}
		if (FaceSelector.DEBUG) {
			System.out.println("Matrix transformation with " + matrix);
		}
		for (final Field f : Fields.FIELDS_COORD) {
			final String field = f.field();
			String value = curData.manual.get(field);
			if (value != null) {
				final Point2D point2d = toPoint2D(value);
				point2d.x -= x;
				point2d.y -= y;
				Point2D point = Matrix.cross(point2d, matrix);
				point.x += x;
				point.y += y;
				if (FaceSelector.DEBUG) {
					System.out.println(field + " transformed from " + value
							+ " to " + point.x
							+ "," + point.y);
				}
				value = point.x + "," + point.y;
				curData.manual.put(field, value);
			}
		}
	}

	public static Point toPoint(final String value) {
		return toPoint(value, 1.0);
	}

	public static Point toPoint(final String string, final double scale) {
		if (string == null) {
			return null;
		}
		final int pos = string.indexOf(',');
		final Point p = new Point(0, 0);
		if (pos > 0) {
			p.x = (int) (Double.parseDouble(string.substring(0, pos)) * scale);
			p.y = (int) (Double.parseDouble(string.substring(pos + 1)) * scale);
		}
		return p;
	}

	public static Point2D toPoint2D(final String value) {
		return toPoint2D(value, 1.0);
	}

	public static Point2D toPoint2D(final String string, final double scale) {
		if (string == null) {
			return null;
		}
		final int pos = string.indexOf(',');
		final Point2D p = new Point2D(0, 0);
		if (pos > 0) {
			p.x = Double.parseDouble(string.substring(0, pos)) * scale;
			p.y = Double.parseDouble(string.substring(pos + 1)) * scale;
		}
		return p;
	}

	public static Rectangle toRectangle(final String value) {
		return toRectangle(value, 1.0);
	}

	public static Rectangle toRectangle(final String string, final double scale) {
		if (string == null) {
			return null;
		}
		int pos = 0;
		int pos2 = string.indexOf(',', pos + 1);
		final Rectangle p = new Rectangle(0, 0, 0, 0);
		if (pos2 > 0) {
			p.x = (int) (Double.parseDouble(string.substring(pos, pos2)) * scale);
			pos = pos2 + 1;
			pos2 = string.indexOf(',', pos);
			p.y = (int) (Double.parseDouble(string.substring(pos, pos2)) * scale);
			pos = pos2 + 1;
			pos2 = string.indexOf(',', pos);
			p.width = (int) (Double.parseDouble(string.substring(pos, pos2)) * scale);
			pos = pos2 + 1;
			p.height = (int) (Double.parseDouble(string.substring(pos)) * scale);
		}
		return p;
	}

	public static double stdDev(LinkedList<Double> distances) {
		int count = distances.size();
		double distanceSum = 0;
		for (double distance : distances) {
			distanceSum += distance;
		}
		double distanceMean = 0;
		if (count > 0) {
			distanceMean = distanceSum / count;
		}
		double distanceVariance = 0;
		for (double distance : distances) {
			distanceVariance += Math.pow(distance - distanceMean, 2);
		}
		distanceVariance /= count - 1;
		return Math.sqrt(distanceVariance);
	}

}
