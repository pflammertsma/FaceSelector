package nl.cs.uu.faceselector;

public class Line {

	private final Field from;
	private final Field to;

	public Line(final Field from, final Field to) {
		this.from = from;
		this.to = to;
	}

	public Field from() {
		return from;
	}

	public Field to() {
		return to;
	}

}
