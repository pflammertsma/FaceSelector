package nl.cs.uu.faceselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ProgressLabel {

	public static final int MODE_TODO = 1;
	public static final int MODE_BUSY = 2;
	public static final int MODE_COMPLETE = 3;
	public static final int MODE_SKIP = 4;
	public static final int MODE_ERROR = 5;

	private final Composite box;
	private final Icon img;
	private final Label lbl;

	private int mode = MODE_TODO;
	private final boolean clickable;
	private Color color;
	private static Cursor cursor;
	private static Color color1, color2, color3, color4, color5;
	private static Font font1, font2;

	private static Image image1, image2, image3, image4, image5;

	public ProgressLabel(final Composite parent, final boolean clickable) {
		if (color1 == null) {
			final Display display = parent.getDisplay();
			color1 = new Color(display, 0, 0, 0);
			color2 = new Color(display, 128, 128, 128);
			color3 = new Color(display, 0, 160, 0);
			color4 = new Color(display, 160, 0, 0);
			color5 = new Color(display, 0, 0, 255);
			font1 = parent.getFont();
			final FontData[] fd = font1.getFontData();
			fd[0].setStyle(SWT.BOLD);
			font2 = new Font(display, fd);
			final String pkg = "res/";
			image1 = new Image(display, pkg + "progress1.png");
			image2 = new Image(display, pkg + "progress2.png");
			image3 = new Image(display, pkg + "progress3.png");
			image4 = new Image(display, pkg + "progress4.png");
			image5 = new Image(display, pkg + "progress5.png");
			cursor = new Cursor(display, SWT.CURSOR_HAND);
		}
		this.clickable = clickable;
		box = new Composite(parent, SWT.NORMAL);
		final RowLayout layout = new RowLayout();
		layout.marginLeft = 0;
		layout.marginTop = layout.marginBottom = layout.marginRight = 1;
		layout.spacing = 2;
		layout.wrap = false;
		box.setLayout(layout);
		img = new Icon(box);
		final RowData data = new RowData();
		data.width = 16;
		data.height = 16;
		img.setLayoutData(data);
		lbl = new Label(box, SWT.NORMAL);
		setMode(MODE_TODO);
		if (this.clickable) {
			lbl.addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseHover(final MouseEvent e) {
				}

				@Override
				public void mouseExit(final MouseEvent e) {
					lbl.setForeground(color);
				}

				@Override
				public void mouseEnter(final MouseEvent e) {
					lbl.setForeground(color5);
				}
			});
			lbl.setCursor(cursor);
		}
	}

	public void setText(final String text) {
		lbl.setText(text);
		box.pack();
	}

	public void setFont(final Font font) {
		lbl.setFont(font);
		box.pack();
	}

	public void setSelection(final boolean selection) {
		if (selection) {
			setMode(MODE_COMPLETE);
		} else {
			setMode(MODE_TODO);
		}
	}

	public void setGrayed(final boolean grayed) {
		if (grayed) {
			setMode(MODE_BUSY);
		}
	}

	public void setMode(final int mode) {
		this.mode = mode;
		switch (mode) {
		case MODE_TODO:
			img.setImage(image1);
			break;
		case MODE_BUSY:
			img.setImage(image2);
			break;
		case MODE_COMPLETE:
			img.setImage(image3);
			break;
		case MODE_SKIP:
			img.setImage(image4);
			break;
		case MODE_ERROR:
			img.setImage(image5);
			break;
		default:
			img.setImage(null);
			break;
		}
		switch (mode) {
		case MODE_SKIP:
		case MODE_TODO:
			color = color2;
			break;
		case MODE_BUSY:
			color = color3;
			break;
		case MODE_ERROR:
			color = color4;
			break;
		default:
			color = color1;
			break;
		}
		lbl.setForeground(color);
		switch (mode) {
		case MODE_BUSY:
			lbl.setFont(font2);
			break;
		default:
			lbl.setFont(font1);
			break;
		}
		img.redraw();
		box.pack();
	}

	public int getMode() {
		return mode;
	}

	public void setData(final String key, final Object value) {
		lbl.setData(key, value);
	}

	public Object getData(final String key) {
		return lbl.getData(key);
	}

	public void addMouseListener(final MouseListener listener, final Object data) {
		lbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(final MouseEvent e) {
				e.data = data;
				listener.mouseUp(e);
			}

			@Override
			public void mouseDown(final MouseEvent e) {
				e.data = data;
				listener.mouseDown(e);
			}

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				e.data = data;
				listener.mouseDoubleClick(e);
			}
		});
	}

	public void setLayoutData(final Object data) {
		box.setLayoutData(data);
	}

}
