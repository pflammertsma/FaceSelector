package nl.cs.uu.faceselector;

import java.util.HashMap;

public class AnnotationData {

	public HashMap<String, String> manual = new HashMap<String, String>();
	public HashMap<String, String> automatic = new HashMap<String, String>();

	public void clear() {
		manual.clear();
		automatic.clear();
	}

}
