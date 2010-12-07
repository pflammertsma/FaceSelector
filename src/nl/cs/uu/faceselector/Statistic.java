package nl.cs.uu.faceselector;

/**
 * Represents a statistic; an undefined method that has an arbitrary number of
 * requirements. This is useful for determining whether a method could
 * theoretically work based upon an array of specified fields, without requiring
 * any specific implementation.
 * 
 * @author Paul Lammertsma
 */
public class Statistic {

	private final String name;
	private final Field[] fields;
	private final Double maxAngle;
	private final boolean all;

	/**
	 * Creates a new {@link Statistic} that passes on photographs if they
	 * contain data for <b>any</b> of the fields specified by <tt>fields</tt>.
	 * 
	 * @param name
	 * @param fields
	 */
	public Statistic(final String name, final Field[] fields) {
		this(name, fields, null);
	}

	/**
	 * Creates a new {@link Statistic} that passes on photographs if they
	 * contain data for <b>any</b> of the fields specified by {@code fields}.
	 * <p>
	 * If {@code all} is {@code true}, then the statistic only passes for
	 * photographs that contain data for <b>all</b> of the fields specified by
	 * {@code fields}.
	 * </p>
	 * 
	 * @param name
	 * @param fields
	 * @param all
	 */
	public Statistic(final String name, final Field[] fields, final boolean all) {
		this(name, fields, null, all);
	}

	/**
	 * Creates a new {@link Statistic} that passes on photographs if they
	 * contain data for <b>any</b> of the fields specified by {@code fields}
	 * <b>and</b> the tilt of the head is less than {@code maxAngle}.
	 * 
	 * @param name
	 * @param fields
	 * @param maxAngle
	 */
	public Statistic(final String name, final Field[] fields,
			final Double maxAngle) {
		this(name, fields, maxAngle, true);
	}

	/**
	 * Creates a new {@link Statistic} that passes on photographs if they
	 * contain data for <b>any</b> of the fields specified by {@code fields}
	 * <b>and</b> the tilt of the head is less than {@code maxAngle}.
	 * <p>
	 * If {@code all} is {@code true}, then the statistic only passes for
	 * photographs that contain data for <b>all</b> of the fields specified by
	 * {@code fields}.
	 * </p>
	 * 
	 * @param name
	 * @param fields
	 * @param maxAngle
	 * @param all
	 */
	public Statistic(final String name, final Field[] fields,
			final Double maxAngle, final boolean all) {
		this.name = name;
		this.fields = fields;
		this.maxAngle = maxAngle;
		this.all = all;
	}

	/**
	 * Returns the name of the statistic
	 * 
	 * @return
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the array of {@link Field}s
	 * 
	 * @return
	 */
	public Field[] fields() {
		return fields;
	}

	/**
	 * Returns the maximum tolerated tilt of the head
	 * 
	 * @return
	 */
	public Double maxAngle() {
		return maxAngle;
	}

	/**
	 * Returns whether or not all of the {@link Field}s are required
	 * 
	 * @return
	 */
	public boolean all() {
		return all;
	}

}
