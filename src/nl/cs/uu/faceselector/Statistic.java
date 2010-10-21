package nl.cs.uu.faceselector;

public class Statistic {

	private final String name;
	private final Field[] fields;
	private final Double maxAngle;
	private final boolean all;

	public Statistic(final String name, final Field[] fields) {
		this(name, fields, null);
	}

	public Statistic(final String name, final Field[] fields, final boolean all) {
		this(name, fields, null, all);
	}

	public Statistic(final String name, final Field[] fields,
			final Double maxAngle) {
		this(name, fields, maxAngle, true);
	}

	public Statistic(final String name, final Field[] fields,
			final Double maxAngle, final boolean all) {
		this.name = name;
		this.fields = fields;
		this.maxAngle = maxAngle;
		this.all = all;
	}

	public String name() {
		return name;
	}

	public Field[] fields() {
		return fields;
	}

	public Double maxAngle() {
		return maxAngle;
	}

	public boolean all() {
		return all;
	}

}
