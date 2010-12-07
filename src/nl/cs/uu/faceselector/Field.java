package nl.cs.uu.faceselector;

/**
 * Represents a field of data, including a descriptive name, the field name by
 * which the data is stored, and the {@link Style} in which it should be drawn
 * (if applicable).
 * 
 * @author Paul Lammertsma
 */
public class Field {

	private final String name;
	private final String field;
	private final Style style;

	/**
	 * Creates a new {@link Field}.
	 * 
	 * @param name
	 *            A descriptive name to show in the UI
	 * @param field
	 *            The field name by which data is stored
	 */
	public Field(final String name, final String field) {
		this(name, field, null);
	}

	/**
	 * Creates a new {@link Field}.
	 * 
	 * @param name
	 *            A descriptive name to show in the UI
	 * @param field
	 *            The field name by which data is stored
	 * @param style
	 *            The {@link Style} in which it should be drawn onto the
	 *            photograph (if applicable)
	 */
	public Field(final String name, final String field, final Style style) {
		this.name = name;
		this.field = field;
		this.style = style;
	}

	/**
	 * Obtains a descriptive name to show in the UI
	 * 
	 * @return Descriptive name
	 */
	public String name() {
		return name;
	}

	/**
	 * Obtains the field name by which data is stored
	 * 
	 * @return Field name
	 */
	public String field() {
		return field;
	}

	/**
	 * The {@link Style} in which it should be drawn onto the photograph (if
	 * applicable)
	 * 
	 * @return {@link Style}
	 */
	public Style style() {
		return style;
	}

}
