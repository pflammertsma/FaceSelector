package nl.cs.uu.faceselector;

/**
 * Represents a line between to {@link Field}s.
 * 
 * @author Paul Lammertsma
 */
public class Line {

	private final Field from;
	private final Field to;

	/**
	 * Draws a line from one {@link Field} to another.
	 * 
	 * @param from
	 *            {@link Field}
	 * @param to
	 *            {@link Field}
	 */
	public Line(final Field from, final Field to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Obtains the source {@link Field}.
	 * 
	 * @return source {@link Field}
	 */
	public Field from() {
		return from;
	}

	/**
	 * Obtains the target {@link Field}.
	 * 
	 * @return target {@link Field}
	 */
	public Field to() {
		return to;
	}

}
