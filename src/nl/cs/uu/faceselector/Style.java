package nl.cs.uu.faceselector;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Represents the style in which a field should be drawn onto the photograph
 * 
 * @author Paul Lammertsma
 */
public class Style {

	public static final int CIRCLE = 1;
	public static final int BOX = 2;
	public static final int LINE_ABOVE = 3;
	public static final int LINE_BELOW = 4;
	public static final int LINE_LEFT = 5;
	public static final int LINE_RIGHT = 6;

	private final int type;
	private final String text;

	/**
	 * Creates a new {@link Style} with a type and text.
	 * 
	 * @param type
	 * @param text
	 */
	public Style(final int type, final String text) {
		this.type = type;
		this.text = text;
	}

	/**
	 * Returns the style type
	 * 
	 * @return
	 */
	public int type() {
		return type;
	}

	/**
	 * Returns the text to display
	 * 
	 * @return
	 */
	public String text() {
		return text;
	}

	/**
	 * Draws the {@link Style} into a {@link GC} object at a specified location
	 * with a given radius and size.
	 * 
	 * @param gc
	 * @param p
	 * @param radius
	 * @param size
	 */
	public void draw(final GC gc, final Point p, final int radius,
			final int size) {
		gc.setAlpha(128);
		switch (type) {
		case CIRCLE:
			gc.fillOval(p.x - radius - 1, p.y - radius - 1, size + 3, size + 3);
			break;
		default:
		case BOX:
			gc.fillRectangle(p.x - radius - 1, p.y - radius - 1, size + 3,
					size + 3);
			break;
		}
		gc.setAlpha(255);
		switch (type) {
		default:
		case CIRCLE:
			gc.drawOval(p.x - radius, p.y - radius, size, size);
			break;
		case BOX:
			gc.drawRectangle(p.x - radius, p.y - radius, size, size);
			break;
		case LINE_ABOVE:
			gc.drawLine(p.x - radius, p.y - radius, p.x + radius, p.y - radius);
			break;
		case LINE_BELOW:
			gc.drawLine(p.x - radius, p.y + radius, p.x + radius, p.y + radius);
			break;
		case LINE_LEFT:
			gc.drawLine(p.x - radius, p.y - radius, p.x - radius, p.y + radius);
			break;
		case LINE_RIGHT:
			gc.drawLine(p.x + radius, p.y - radius, p.x + radius, p.y + radius);
			break;
		}
		gc.drawString(text, p.x - radius / 2, p.y - FaceSelector.FONT_SIZE + 2,
				true);
	}

}
