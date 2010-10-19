package nl.cs.uu.faceselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class Icon extends Composite {

	private Image img;

	public Icon(final Composite parent) {
		this(parent, (Image) null);
	}

	public Icon(final Composite parent, final Image im) {
		super(parent, SWT.NONE);
		img = im;
		addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				if (img != null && !img.isDisposed()) {
					e.gc.drawImage(img, 0, 0);
				}
			}
		});
	}

	public void setImage(final Image im) {
		img = im;
		redraw();
	}

	public Image getImage() {
		return img;
	}

}
