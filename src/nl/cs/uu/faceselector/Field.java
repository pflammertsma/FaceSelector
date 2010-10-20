package nl.cs.uu.faceselector;

public class Field {

	private String name;
	private String field;

	public Field(String name, String field) {
		this.name = name;
		this.field = field;
	}
	
	public String name() {
		return name;
	}
	
	public String field() {
		return field;
	}
	
}
