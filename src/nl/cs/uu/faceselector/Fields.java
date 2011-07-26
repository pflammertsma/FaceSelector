package nl.cs.uu.faceselector;

import org.eclipse.swt.graphics.Point;

public interface Fields {

	/*
	 * Various fields; be sure to add them to FIELDS_TOGGLE or FIELDS_COORD
	 */
	public static final Field FIELD_HEAD_T = new Field("Head top", "headT",
			new Style(Style.LINE_ABOVE, "T"));
	public static final Field FIELD_HEAD_B = new Field("Head bottom", "headB",
			new Style(Style.LINE_BELOW, "B"));
	public static final Field FIELD_EYE_L = new Field("Eye left", "eyeL",
			new Style(Style.CIRCLE, "L"));
	public static final Field FIELD_EYE_R = new Field("Eye right", "eyeR",
			new Style(Style.CIRCLE, "R"));
	public static final Field FIELD_NOSE = new Field("Nose", "nose",
			new Style(Style.BOX, "N"));
	public static final Field FIELD_MOUTH = new Field("Mouth", "mouth",
			new Style(Style.BOX, "M"));

	public static final Field FIELD_CROP_T = new Field("Crop top", "cropT");
	public static final Field FIELD_CROP_R = new Field("Crop right", "cropR");
	public static final Field FIELD_CROP_B = new Field("Crop bottom", "cropB");
	public static final Field FIELD_CROP_L = new Field("Crop left", "cropL");

	/**
	 * Array of {@link Field}s that take a boolean value.
	 */
	public static final Field[] FIELDS_TOGGLE = new Field[] { FIELD_CROP_T,
			FIELD_CROP_R, FIELD_CROP_B, FIELD_CROP_L };
	/**
	 * Array of {@link Field}s that take a {@link Point} value (specified by
	 * clicking in the photograph).
	 */
	public static final Field[] FIELDS_COORD = new Field[] { FIELD_HEAD_T,
			FIELD_HEAD_B, FIELD_EYE_L, FIELD_EYE_R, FIELD_NOSE, FIELD_MOUTH };

	/**
	 * Array of {@link Field}s that must be within the detection box in order to
	 * qualify a match.
	 */
	public static final Field[] FIELDS_REQUIRE_MATCH = new Field[] {
			FIELD_EYE_L, FIELD_EYE_R, FIELD_NOSE, FIELD_MOUTH };

	public static final Line[] LINES = new Line[] {
			new Line(FIELD_EYE_L, FIELD_EYE_R),
			new Line(FIELD_HEAD_T, FIELD_HEAD_B),
		};

	public static Statistic[] STATISTICS = new Statistic[] {
			new Statistic("Facial features", new Field[] { FIELD_EYE_L,
					FIELD_EYE_R, FIELD_MOUTH }, null, false),
			new Statistic("Haar", new Field[] { FIELD_HEAD_T, FIELD_HEAD_B,
					FIELD_EYE_L, FIELD_EYE_R }, 10.0),
			new Statistic("Android", new Field[] { FIELD_EYE_L, FIELD_EYE_R,
					FIELD_MOUTH }, 10.0),
			};

	public static final boolean TRANSLATE_TO_ORIGIN = true;

}
