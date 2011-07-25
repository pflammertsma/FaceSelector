package nl.cs.uu.faceselector;

import java.util.HashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Face {

	public static final double THRESHOLD = 0.5;
	public static final boolean BINARY_MATCHING = false;

	public Double rotation = null;
	public Rectangle box = null, boxSquare = null;
	public double width, height;

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

	private static double distance(Rectangle from, Rectangle to) {
		return 1.0 - area(from.intersection(to))
				/ Math.max(area(from), area(to));
	}

	private static double area(Rectangle rect) {
		return rect.width * rect.height;
	}

	private static boolean containsFeatures(Rectangle box,
			HashMap<String, String> faceData) {
		for (final Field field : FaceSelector.FIELDS_REQUIRE_MATCH) {
			final String value = faceData.get(field.field());
			if (value != null) {
				Point p = FaceSelector.toPoint(value);
				if (!box.contains(p)) {
					// Face doesn't contain a required point; it is not a match
					return false;
				}
			}
		}
		return true;
	}

}
