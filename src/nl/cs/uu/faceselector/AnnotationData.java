package nl.cs.uu.faceselector;

import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

public class AnnotationData {

	public HashMap<String, String> manual = new HashMap<String, String>();
	public HashMap<String, String> automatic = new HashMap<String, String>();
	public Point imageSize = null;

	public void clear() {
		manual.clear();
		automatic.clear();
	}

}
