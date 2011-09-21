package nl.cs.uu.faceselector;

import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Face {

	public static final double THRESHOLD = 0.5;
	public static final boolean BINARY_MATCHING = false;
	public static final int MEASUREMENT_COUNT = 4;

	private static final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;

	private static final int DEBUG = 0;

	public Double rotation = null;
	public Rectangle box = null, boxSquare = null;
	private double x, y;
	public double width, height;
	public Point imageSize;

	public Face(HashMap<String, String> data, Point imageSize) {
		this(data, imageSize, false);
	}

	public Face(HashMap<String, String> data, Point imageSize,
			final boolean absolute) {
		this.box = getBoundingBox(data);
		this.imageSize = imageSize;
		if (this.box == null) {
			return;
		}
		this.width = this.box.width;
		this.height = this.box.height;
		final Field eyeL = Fields.FIELD_EYE_L;
		final Field eyeR = Fields.FIELD_EYE_R;
		final Point2D p1 = MatrixMath.toPoint2D(data.get(eyeL.field()));
		final Point2D p2 = MatrixMath.toPoint2D(data.get(eyeR.field()));
		if (p1 != null && p2 != null) {
			this.width = MatrixMath.distance(p1, p2) * 2;
			final Point2D pA = new Point2D(1.0, 0.0);
			final Point2D pB = MatrixMath.norm(new Point2D(p1.x - p2.x, p1.y
					- p2.y));
			final double radians = Math.acos(MatrixMath.dot(pA, pB));
			rotation = radians * MatrixMath.RADIANS_TO_DEGREES;
			if (pB.x < 0) {
				rotation *= -1;
			}
			if (absolute) {
				rotation = Math.abs(this.rotation);
			}
		}
		final Field headT = Fields.FIELD_HEAD_T;
		final Field headB = Fields.FIELD_HEAD_B;
		final Point2D p3 = MatrixMath.toPoint2D(data.get(headT.field()));
		final Point2D p4 = MatrixMath.toPoint2D(data.get(headB.field()));
		if (p3 != null && p4 != null) {
			this.height = MatrixMath.distance(p3, p4);
			if (this.width == 0) {
				this.width = this.height / GOLDEN_RATIO;
			}
			final Point2D pA = new Point2D(0.0, -1.0);
			final Point2D pB = MatrixMath.norm(new Point2D(p3.x - p4.x, p3.y
					- p4.y));
			final double radians = Math.acos(MatrixMath.dot(pA, pB));
			rotation = radians * MatrixMath.RADIANS_TO_DEGREES;
			if (pB.x < 0) {
				rotation *= -1;
			}
			if (absolute) {
				rotation = Math.abs(this.rotation);
			}
		}
		// Get the face's bounding box
		x = this.box.x + (double) this.box.width / 2;
		y = this.box.y + (double) this.box.height / 2;
		double boxSquareSize = this.height;
		double half = boxSquareSize / 2;
		this.boxSquare = new Rectangle(
				(int) (x - half),
				(int) (y - half),
				(int) boxSquareSize,
				(int) boxSquareSize);
	}

	public double get(int index) {
		return get(index, false);
	}

	public double get(int index, boolean normalized) {
		if (imageSize == null) {
			normalized = false;
		}
		if (index > MEASUREMENT_COUNT) {
			throw new IndexOutOfBoundsException("Measurement index " + index
					+ " does not exist");
		}
		switch (index) {
		case 0:
			if (normalized) {
				return x / imageSize.x;
			}
			return x;
		case 1:
			if (normalized) {
				return y / imageSize.y;
			}
			return y;
		case 2:
		case 3:
			double size = boxSquare.width;
			if (normalized) {
				return size / imageSize.x;
			}
			return size;
		case 4:
			if (normalized) {
				return width / imageSize.x;
			}
			return width;
		case 5:
			if (normalized) {
				return height / imageSize.y;
			}
			return height;
		}
		throw new IndexOutOfBoundsException("Invalid field index " + index);
	}

	public static Rectangle getBoundingBox(HashMap<String, String> data) {
		boolean first = true;
		// FIXME replace with Recangle2D
		Rectangle bounds = new Rectangle(0, 0, 0, 0);
		if (data.containsKey("head")) {
			String value = data.get("head");
			return MatrixMath.toRectangle(value);
		}
		for (final Field f : Fields.FIELDS_COORD) {
			final String field = f.field();
			String value = data.get(field);
			if (value != null) {
				Point2D point = MatrixMath.toPoint2D(value);
				if (point.x < bounds.x || first) {
					bounds.x = (int) point.x;
				}
				if (point.y < bounds.y || first) {
					bounds.y = (int) point.y;
				}
				if (point.x > bounds.width || first) {
					bounds.width = (int) point.x;
				}
				if (point.y > bounds.height || first) {
					bounds.height = (int) point.y;
				}
				first = false;
			}
		}
		if (first) {
			return null;
		}
		bounds.width -= bounds.x;
		bounds.height -= bounds.y;
		return bounds;
	}

	public double computeMatch(Face other, HashMap<String, String> faceData) {
		if (other == null || other.boxSquare == null) {
			return Double.MAX_VALUE;
		}
		Rectangle intersection = boxSquare.intersection(other.boxSquare);
		return area(intersection);
	}

	public double similarity(Face other,
			HashMap<String, String> faceData) {
		return similarity(other, faceData, THRESHOLD);
	}

	public double similarity(Face other,
			HashMap<String, String> faceData,
			double threshold) {
		if (other == null || other.boxSquare == null) {
			return 0.0;
		}
		if (!containsFeatures(other.box, faceData)) {
			return 0.0;
		}
		if (BINARY_MATCHING) {
			double distance = distance(boxSquare, other.box);
			if (distance < threshold) {
				return 1.0;
			}
			// return distance;
		}
		double intersectionSize = computeMatch(other, faceData);
		double boxSize = Math.max(boxSquare.width * boxSquare.height,
				other.box.width * other.box.height);
		return intersectionSize / boxSize;
	}

	public double similarity2(Face other) {
		if (this.boxSquare == null || other.boxSquare == null) {
			return 0;
		}
		LinkedList<Face> facesM = new LinkedList<Face>();
		LinkedList<LinkedList<Face>> facesA = new LinkedList<LinkedList<Face>>();
		for (AnnotationData faceData : FaceSelector.getAllData()) {
			facesM.add(new Face(faceData.manual, faceData.imageSize));
			LinkedList<Face> facesA2 = new LinkedList<Face>();
			for (String mode : FaceSelector.AUTOMATIC_MODES) {
				if (faceData.automatic.containsKey(mode)) {
					facesA2.add(new Face(faceData.automatic.get(mode),
							faceData.imageSize));
				}
			}
			facesA.add(facesA2);
		}
		double sum = 0;
		String msg = "";
		// Iterates through the different distance measures of the face (e.g. x,
		// y, width, height, rotation)
		for (int i = 0; i < MEASUREMENT_COUNT; i++) {
			// Obtain the sigma from FaceSelector
			double sigma = FaceSelector.getSigma(i, facesM, facesA);
			if (DEBUG > 1) {
				System.out.println("distance_" + i + ": " + this.get(i) + " - "
						+ other.get(i) + " = " + distance(i, this, other));
				System.out.println("sigma_" + i + ": " + sigma);
			}
			double val = (-Math.pow(distance(i, this, other), 2))
					/ (2 * Math.pow(sigma, 2));
			if (DEBUG > 0) {
				System.out.println(i + ": -(" + distance(i, this, other)
						+ ")^2 / "
						+ sigma + "^2 = " + val);
			}
			if (i > 0) {
				msg += " + ";
			}
			msg += val;
			sum += val;
		}
		double similarity = Math.exp(sum);
		if (DEBUG > 0) {
			System.out.println("e^(" + msg + ") = " + similarity);
		}
		return similarity;
	}

	public static double distance(int i, Face face1, Face face2) {
		return face1.get(i) - face2.get(i);
	}

	private static double distance(Rectangle from, Rectangle to) {
		return 1.0 - area(from.intersection(to))
				/ Math.max(area(from), area(to));
	}

	private static double area(Rectangle rect) {
		return rect.width * rect.height;
	}

	private static boolean containsFeatures(Rectangle box,
			HashMap<String, String> faceData) {
		for (final Field field : Fields.FIELDS_REQUIRE_MATCH) {
			final String value = faceData.get(field.field());
			if (value != null) {
				Point p = MatrixMath.toPoint(value);
				if (!box.contains(p)) {
					// Face doesn't contain a required point; it is not a match
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Face(" + x + ", " + y + "; " + width + " x " + height + ")";
	}

}
