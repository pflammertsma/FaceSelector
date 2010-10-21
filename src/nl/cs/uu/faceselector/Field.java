package nl.cs.uu.faceselector;

public class Field {

	private final String name;
	private final String field;
	private final Style style;

	public Field(final String name, final String field) {
		this(name, field, null);
	}

	public Field(final String name, final String field, final Style style) {
		this.name = name;
		this.field = field;
		this.style = style;
	}

	public String name() {
		return name;
	}

	public String field() {
		return field;
	}

	public Style style() {
		return style;
	}

}
