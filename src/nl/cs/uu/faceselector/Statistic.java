package nl.cs.uu.faceselector;

public class Statistic {

	private final String name;
	private final Field[] fields;
	private final Double maxAngle;

	public Statistic(final String name, final Field[] fields) {
		this(name, fields, null);
	}

	public Statistic(final String name, final Field[] fields,
			final Double maxAngle) {
		this.name = name;
		this.fields = fields;
		this.maxAngle = maxAngle;
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

}
