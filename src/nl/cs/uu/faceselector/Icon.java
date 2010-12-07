/*
 * Copyright 2010, CrimsonBase B.V. The Netherlands
 * 
 * All rights reserved. This program and the accompanying materials may not
 * be redistributed without the explicit, written permission of:
 *     CrimsonBase B.V.
 *     Padualaan 8
 *     3584 CH Utrecht
 *     The Netherlands
 *     +31 (0)30 890 3214
 *     http://crimsonbase.com
 */

package nl.cs.uu.faceselector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author CrimsonBase B.V.
 */
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
