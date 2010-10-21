package nl.cs.uu.faceselector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class FaceSelector {

	private static final boolean DEBUG = false;

	private static final Field FIELD_HEAD_T = new Field("Head top", "headT",
			new Style(Style.LINE_ABOVE, "T"));
	private static final Field FIELD_HEAD_B = new Field("Head bottom", "headB",
			new Style(Style.LINE_BELOW, "B"));
	private static final Field FIELD_EYE_L = new Field("Eye left", "eyeL",
			new Style(Style.CIRCLE, "L"));
	private static final Field FIELD_EYE_R = new Field("Eye right", "eyeR",
			new Style(Style.CIRCLE, "R"));
	private static final Field FIELD_NOSE = new Field("Nose", "nose",
			new Style(Style.BOX, "N"));

	private static final Field FIELD_CROP_T = new Field("Crop top", "cropT");
	private static final Field FIELD_CROP_R = new Field("Crop right", "cropR");
	private static final Field FIELD_CROP_B = new Field("Crop bottom", "cropB");
	private static final Field FIELD_CROP_L = new Field("Crop left", "cropL");

	private static Line[] lines = new Line[] {
			new Line(FIELD_EYE_L, FIELD_EYE_R),
			new Line(FIELD_HEAD_T, FIELD_HEAD_B),
		};

	private final static String PATH = "../Subjects/";

	protected static final double CIRCLE_SIZE = 5.0;
	protected static final int FONT_SIZE = (int) (CIRCLE_SIZE * 2);

	private static final Point EXPECTED_IMAGE_SIZE = new Point(176, 144);

	private static LinkedList<File> files = new LinkedList<File>();
	private static HashMap<String, String> curData = new HashMap<String, String>();

	private static Display display;
	private static Shell shell;
	private static Composite imgBox;
	private static Label imgLabel;

	private static Image curImg;
	private static int curFile = -1;

	protected static double scale;

	private static Button buttonT, buttonR, buttonB, buttonL;
	private static Label[] label1;
	private static ProgressLabel[] label2;
	private static ProgressLabel label3;

	private static Field[] fieldsToggle = new Field[] { FIELD_CROP_T,
			FIELD_CROP_R, FIELD_CROP_B, FIELD_CROP_L };
	private static Field[] fieldsCoord = new Field[] { FIELD_HEAD_T,
			FIELD_HEAD_B, FIELD_EYE_L, FIELD_EYE_R, FIELD_NOSE };

	private static int currentLabel;

	private static boolean onlyIncomplete;
	protected static Color color1, color2;
	protected static Font font;

	public static void main(final String[] args) {
		display = new Display();
		shell = new Shell(display);

		color1 = new Color(display, 0, 0, 0);
		color2 = new Color(display, 0, 255, 0);
		font = new Font(display, "Courier New", FONT_SIZE, SWT.NORMAL);

		display.addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				Button button = null;
				switch (e.keyCode) {
				case SWT.ARROW_RIGHT:
				case SWT.PAGE_DOWN:
					setFile(curFile + 1, true);
					break;
				case SWT.ARROW_LEFT:
				case SWT.PAGE_UP:
					setFile(curFile - 1, true);
					break;
				case SWT.DEL:
					File file;
					try {
						file = files.get(curFile).getCanonicalFile();
						final File destFile = new File(file + ".bak");
						final int result = showMessage(
								SWT.YES | SWT.NO | SWT.ICON_QUESTION,
								"This file will be renamed to:\n\n    "
										+ destFile
										+ "\n\nAre you sure you want to exclude it?");
						if (result == SWT.YES) {
							if (file.renameTo(destFile)) {
								files.remove(curFile);
								setFile(curFile, true);
							} else {
								showMessage(
										SWT.ERROR,
										"Failed to rename the following file:\n\n    "
												+ file);
							}
						}
					} catch (final IOException e1) {
						fatal(e1);
					}
					break;
				case SWT.ESC:
					final int result = showMessage(SWT.YES | SWT.NO
							| SWT.ICON_QUESTION, "Revert data from file?");
					if (result == SWT.YES) {
						load();
					}
					break;
				case 'w':
					button = buttonT;
					break;
				case 'a':
					button = buttonL;
					break;
				case 's':
					button = buttonB;
					break;
				case 'd':
					button = buttonR;
					break;
				}
				if (button != null) {
					button.setSelection(!button.getSelection());
					setData(button);
				}
			}
		});

		final String msg = "Finding unannotated files... ";
		if (DEBUG) {
			System.out.println(msg);
		} else {
			System.out.print(msg);
		}
		final File path = new File(PATH);
		onlyIncomplete = true;
		listFiles(path);
		onlyIncomplete = false;
		System.out.println(FaceSelector.files.size() + " file(s)");

		if (files.size() > 0) {
			final int result = showMessage(
					SWT.YES | SWT.NO | SWT.ICON_QUESTION,
					"Would you like to display only unannotated files?");
			if (result == SWT.YES) {
				onlyIncomplete = true;
			}
		}

		if (!onlyIncomplete) {
			files.clear();
			curData.clear();
			System.out.print("Collecting files... ");
			listFiles(path);
		}
		if (files.size() == 0) {
			fatal("No images found in path:\n\t" + PATH);
		} else {
			System.out.println(FaceSelector.files.size() + " file(s)");
		}

		final SelectionListener croppedListener = new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (e.getSource() != null && e.getSource() instanceof Button) {
					final Button button = (Button) e.getSource();
					setData(button);
				} else {
					throw new RuntimeException("Source is not a Button");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		};

		FormData fd1;
		final Group group1 = new Group(shell, SWT.NORMAL);
		{
			group1.setText("Cropped");
			fd1 = new FormData();
			fd1.bottom = new FormAttachment(100, -2);
			fd1.left = new FormAttachment(0, 2);
			group1.setLayoutData(fd1);
			final RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
			rowLayout.fill = true;
			rowLayout.justify = true;
			rowLayout.center = true;
			rowLayout.pack = false;
			group1.setLayout(rowLayout);
			buttonT = new Button(group1, SWT.TOGGLE);
			setButton(buttonT, FIELD_CROP_T);
			buttonT.addSelectionListener(croppedListener);
			buttonR = new Button(group1, SWT.TOGGLE);
			setButton(buttonR, FIELD_CROP_R);
			buttonR.addSelectionListener(croppedListener);
			buttonB = new Button(group1, SWT.TOGGLE);
			setButton(buttonB, FIELD_CROP_B);
			buttonB.addSelectionListener(croppedListener);
			buttonL = new Button(group1, SWT.TOGGLE);
			setButton(buttonL, FIELD_CROP_L);
			buttonL.addSelectionListener(croppedListener);
		}

		final Group group2 = new Group(shell, SWT.NORMAL);
		{
			group2.setText("Features");
			final FormData fd = new FormData();
			fd.bottom = new FormAttachment(100, -2);
			fd.left = new FormAttachment(group1, 4, SWT.RIGHT);
			fd.right = new FormAttachment(100, -2);
			fd1.top = new FormAttachment(group2, 0, SWT.TOP);
			group2.setLayoutData(fd);
			final GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.horizontalSpacing = 3;
			group2.setLayout(gridLayout);
			final MouseListener labelClick = new MouseListener() {
				@Override
				public void mouseUp(final MouseEvent e) {
				}

				@Override
				public void mouseDown(final MouseEvent e) {
					if (e.data != null && e.data instanceof Integer) {
						final int i = (Integer) e.data;
						final Object data = label2[i].getData("field");
						if (data != null && data instanceof String) {
							final String key = (String) data;
							curData.remove(key);
						} else {
							throw new RuntimeException(
									"Field is not a string: " + data);
						}
					} else {
						throw new RuntimeException("Source not specified");
					}
					updateCoords(true);
				}

				@Override
				public void mouseDoubleClick(final MouseEvent e) {
				}
			};
			label1 = new Label[fieldsCoord.length];
			label2 = new ProgressLabel[fieldsCoord.length];
			int i = 0;
			for (final Field f : fieldsCoord) {
				label1[i] = new Label(group2, SWT.NORMAL);
				label1[i].setText(f.name());
				label2[i] = new ProgressLabel(group2, true);
				label2[i].setData("field", f.field());
				label2[i].addMouseListener(labelClick, i);
				i++;
			}
			final GridData data = new GridData();
			data.horizontalSpan = 2;
			label3 = new ProgressLabel(group2, false);
			label3.setLayoutData(data);
		}

		final Composite imgControl = new Composite(shell, SWT.NORMAL);
		{
			FormData fd = new FormData();
			fd.bottom = new FormAttachment(group1, -2, SWT.TOP);
			fd.left = new FormAttachment(0, 2);
			fd.right = new FormAttachment(100, -2);
			imgControl.setLayoutData(fd);
			imgControl.setLayout(new FormLayout());
			imgLabel = new Label(imgControl, SWT.NORMAL);
			fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.right = new FormAttachment(40, 0);
			fd.bottom = new FormAttachment(100, -5);
			imgLabel.setLayoutData(fd);
			imgLabel.setAlignment(SWT.CENTER);
			final Button button1 = new Button(imgControl, SWT.PUSH);
			button1.setText("< Previous");
			fd = new FormData();
			fd.left = new FormAttachment(imgLabel, 0, SWT.RIGHT);
			fd.right = new FormAttachment(60, -1);
			button1.setLayoutData(fd);
			button1.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					setFile(curFile - 1, true);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}
			});
			final Button button2 = new Button(imgControl, SWT.PUSH);
			button2.setText("Next >");
			fd = new FormData();
			fd.left = new FormAttachment(button1, 1, SWT.RIGHT);
			fd.right = new FormAttachment(80, 0);
			button2.setLayoutData(fd);
			button2.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					setFile(curFile + 1, true);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}
			});
			final Button button3 = new Button(imgControl, SWT.PUSH);
			button3.setText("Statistics");
			fd = new FormData();
			fd.left = new FormAttachment(button2, 1, SWT.RIGHT);
			fd.right = new FormAttachment(100, 0);
			button3.setLayoutData(fd);
			button3.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					showStatistics();
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}
			});
		}

		imgBox = new Composite(shell, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		{
			final FormData fd = new FormData();
			fd.top = new FormAttachment(0, 2);
			fd.bottom = new FormAttachment(imgControl, -2, SWT.TOP);
			fd.left = new FormAttachment(0, 2);
			fd.right = new FormAttachment(100, -2);
			imgBox.setLayoutData(fd);
			imgBox.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(final MouseEvent e) {
				}

				@Override
				public void mouseDown(final MouseEvent e) {
					imgBox.setFocus();
					switch (e.button) {
					case 1:
						setCoord((int) (e.x / scale), (int) (e.y / scale));
						break;
					case 3:
						setCoord(null);
						break;
					}
				}

				@Override
				public void mouseDoubleClick(final MouseEvent e) {
				}
			});
			imgBox.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(final PaintEvent e) {
					if (curImg != null) {
						final int srcWidth = curImg.getBounds().width;
						final int srcHeight = curImg.getBounds().height;
						final double srcRatio = (double) srcWidth
								/ (double) srcHeight;
						int destWidth = imgBox.getSize().x;
						int destHeight = imgBox.getSize().y;
						final double destRatio = (double) destWidth
								/ (double) destHeight;
						if (destRatio < srcRatio) {
							destHeight = (int) (destWidth / srcRatio);
						} else {
							destWidth = (int) (destHeight * srcRatio);
						}
						scale = (((double) destWidth / (double) srcWidth) + ((double) destHeight / (double) srcHeight)) / 2;
						e.gc.drawImage(curImg, 0, 0, srcWidth, srcHeight, 0, 0,
								destWidth, destHeight);
						e.gc.setFont(font);
						e.gc.setBackground(color1);
						e.gc.setForeground(color2);
						final int radius = (int) (CIRCLE_SIZE / 2 * scale);
						final int size = (int) (CIRCLE_SIZE * scale);
						if (true) {
							final Point p[] = new Point[fieldsCoord.length];
							final Point l[][] = new Point[lines.length][2];
							int i = 0;
							for (final Field field : fieldsCoord) {
								final String value = curData.get(field.field());
								if (value != null) {
									p[i] = toPoint(value, scale);
								}
								int j = 0;
								for (final Line line : lines) {
									if (line.from().equals(field)) {
										l[j][0] = p[i];
									}
									if (line.to().equals(field)) {
										l[j][1] = p[i];
									}
									j++;
								}
								i++;
							}
							for (int j = 0; j < lines.length; j++) {
								if (l[j] != null && l[j][0] != null
										&& l[j][1] != null) {
									e.gc.drawLine(l[j][0].x, l[j][0].y,
											l[j][1].x, l[j][1].y);
								}
							}
							i = 0;
							for (final Field field : fieldsCoord) {
								final Style style = field.style();
								if (p[i] != null && style != null) {
									style.draw(e.gc, p[i], radius, size);
								}
								i++;
							}
						} else {
							Point eyeL = null;
							Point eyeR = null;
							Point headT = null;
							Point headB = null;
							Point nose = null;
							for (final Entry<String, String> set : curData
									.entrySet()) {
								if (set.getKey().equals(FIELD_EYE_L.field())) {
									eyeL = toPoint(set.getValue(), scale);
								} else if (set.getKey().equals(
										FIELD_EYE_R.field())) {
									eyeR = toPoint(set.getValue(), scale);
								} else if (set.getKey()
										.equals(FIELD_HEAD_T.field())) {
									headT = toPoint(set.getValue(), scale);
								} else if (set.getKey()
										.equals(FIELD_HEAD_B.field())) {
									headB = toPoint(set.getValue(), scale);
								} else if (set.getKey().equals(
										FIELD_NOSE.field())) {
									nose = toPoint(set.getValue(), scale);
								}
							}
							if (eyeL != null) {
								drawPoint(1, e.gc, eyeL, radius, size);
								drawString("L", e.gc, eyeL, radius);
							}
							if (eyeR != null) {
								drawPoint(1, e.gc, eyeR, radius, size);
								drawString("R", e.gc, eyeR, radius);
							}
							if (eyeL != null && eyeR != null) {
								e.gc.drawLine(eyeL.x + radius, eyeL.y, eyeR.x
										- radius, eyeR.y);
							}
							if (headT != null) {
								drawPoint(2, e.gc, headT, radius, size);
								drawString("T", e.gc, headT, radius);
							}
							if (headB != null) {
								drawPoint(2, e.gc, headB, radius, size);
								drawString("B", e.gc, headB, radius);
							}
							if (headT != null && headB != null) {
								e.gc.drawLine(headT.x, headT.y + radius,
										headB.x,
										headB.y - radius);
							}
							if (nose != null) {
								drawPoint(2, e.gc, nose, radius, size);
								drawString("N", e.gc, nose, radius);
							}
						}
					}
				}
			});
		}

		setFile(0, false);

		shell.addShellListener(new ShellListener() {

			@Override
			public void shellIconified(final ShellEvent arg0) {
			}

			@Override
			public void shellDeiconified(final ShellEvent arg0) {
			}

			@Override
			public void shellDeactivated(final ShellEvent arg0) {
			}

			@Override
			public void shellClosed(final ShellEvent arg0) {
				save();
			}

			@Override
			public void shellActivated(final ShellEvent arg0) {
			}
		});

		shell.setLayout(new FormLayout());
		shell.setText(FaceSelector.class.getSimpleName());
		final int height = 600;
		shell.setSize(500, height);
		shell.layout();
		shell.setMinimumSize(500, height - imgBox.getSize().y
				+ EXPECTED_IMAGE_SIZE.y);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	protected static void drawPoint(final int type, final GC gc,
			final Point p, final int radius, final int size) {
		gc.setAlpha(128);
		switch (type) {
		default:
		case 1:
			gc.fillOval(p.x - radius - 1, p.y - radius - 1, size + 3, size + 3);
			break;
		case 2:
			gc.fillRectangle(p.x - radius - 1, p.y - radius - 1, size + 3,
					size + 3);
			break;
		}
		gc.setAlpha(255);
		switch (type) {
		default:
		case 1:
			gc.drawOval(p.x - radius, p.y - radius, size, size);
			break;
		case 2:
			gc.drawRectangle(p.x - radius, p.y - radius, size, size);
			break;
		}
	}

	private static void drawString(final String string, final GC gc,
			final Point p, final int radius) {
		gc.drawString(string, p.x - radius / 2, p.y - FONT_SIZE + 2, true);
	}

	private static Point toPoint(final String value) {
		return toPoint(value, 1.0);
	}

	private static Point toPoint(final String string, final double scale) {
		if (string == null) {
			return null;
		}
		final int pos = string.indexOf(',');
		final Point p = new Point(0, 0);
		if (pos > 0) {
			p.x = (int) (Integer.parseInt(string.substring(0, pos)) * scale);
			p.y = (int) (Integer
					.parseInt(string.substring(pos + 1)) * scale);
		}
		return p;
	}

	protected static void showStatistics() {
		final int cropped[] = new int[fieldsCoord.length];
		int count = 0;
		int i = 0;
		for (final File file : files) {
			loadData(file);
			if (!isAnnotated()) {
				continue;
			}
			count++;
			int j = 0;
			for (final Field field : fieldsCoord) {
				if (curData.containsKey(field.field())) {
					final String key = curData.get(field.field());
					if (key == null) {
						cropped[j]++;
					}
				}
				j++;
			}
			i++;
		}
		String msg = "Annotated: " + count + " of " + files.size();
		int j = 0;
		for (final Field field : fieldsCoord) {
			msg += "\nCropped " + field.field() + ": " + cropped[j] + " of "
					+ count;
			j++;
		}
		showMessage(SWT.ICON_INFORMATION, msg);
		setFile(curFile, false);
	}

	private static void setButton(final Button button, final Field field) {
		button.setText(field.name());
		button.setData("field", field.field());
	}

	private static void fatal(final Exception e) {
		fatal(e.getMessage());
	}

	private static void fatal(final String message) {
		final RuntimeException e = new RuntimeException(message);
		e.printStackTrace();
		showMessage(SWT.ICON_ERROR, message);
		System.exit(1);
	}

	private static int showMessage(final int style, final String message) {
		return showMessage(style, null, message);
	}

	private static int showMessage(final int style, String title,
			final String message) {
		final MessageBox mg = new MessageBox(shell, style);
		if (title == null) {
			title = FaceSelector.class.getSimpleName();
		}
		mg.setText(title);
		mg.setMessage(message);
		return mg.open();
	}

	protected static void setCoord(final int x, final int y) {
		final String value = x + "," + y;
		if (currentLabel >= 0) {
			setCoord(value);
		} else {
			final Point point = new Point(x, y);
			Point nearestPoint = null;
			double nearest = -1;
			int nearestIndex = 0;
			int i = 0;
			for (final Field field : fieldsCoord) {
				final String value2 = curData.get(field.field());
				if (value2 != null) {
					final Point point2 = toPoint(value2);
					final double distance = distance(point, point2);
					if (distance < 20 && (nearest < 0 || distance < nearest)) {
						nearestPoint = point2;
						nearest = distance;
						nearestIndex = i;
					}
				}
				i++;
			}
			if (nearestPoint != null) {
				currentLabel = nearestIndex;
				setCoord(value);
			}
		}
	}

	private static double distance(final Point a, final Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	private static void setCoord(final String value) {
		if (currentLabel >= 0) {
			final Object data = label2[currentLabel].getData("field");
			if (data != null && data instanceof String) {
				final String key = (String) data;
				curData.put(key, value);
			} else {
				throw new RuntimeException("Field is not a string: " + data);
			}
		}
		updateCoords(true);
	}

	private static void setData(final Button button) {
		final Object data = button.getData("field");
		if (data != null && data instanceof String) {
			final String key = (String) data;
			String value = "false";
			if (button.getSelection()) {
				value = "true";
			}
			curData.put(key, value);
		} else {
			throw new RuntimeException("Button does not contain toggle data");
		}
	}

	private static String getData(final Field field) {
		final String key = field.field();
		if (curData.containsKey(key)) {
			return curData.get(key);
		}
		return null;
	}

	private static void setFile(final int i, final boolean save) {
		if (save) {
			save();
		}
		curFile = i;
		if (curFile > files.size() - 1) {
			curFile = 0;
		} else if (curFile < 0) {
			curFile = files.size() - 1;
		}
		imgLabel.setText("Image " + (curFile + 1) + " of " + files.size());
		load();
	}

	private static void load() {
		try {
			final String path = files.get(curFile).getCanonicalPath();
			curImg = new Image(display, path);
			loadData(path);
			for (final Field field : fieldsToggle) {
				final String value = curData.get(field.field());
				boolean selection = false;
				if (value != null && value.equals("true")) {
					selection = true;
				}
				Button button = null;
				if (field.equals(FIELD_CROP_T)) {
					button = buttonT;
				} else if (field.equals(FIELD_CROP_R)) {
					button = buttonR;
				} else if (field.equals(FIELD_CROP_B)) {
					button = buttonB;
				} else if (field.equals(FIELD_CROP_L)) {
					button = buttonL;
				}
				if (button != null) {
					button.setSelection(selection);
				}
			}
			imgBox.redraw();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		updateCoords(false);
	}

	private static void loadData(final String path) {
		final File file = new File(path + ".txt");
		loadData(file);
	}

	private static void loadData(File file) {
		if (!file.getName().endsWith(".txt")) {
			try {
				file = new File(file.getCanonicalPath() + ".txt");
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		curData.clear();
		if (file.exists()) {
			try {
				final FileReader input = new FileReader(file);
				final BufferedReader bufRead = new BufferedReader(input);
				String line;
				final int count = 0;
				int lineNo = 0;
				while ((line = bufRead.readLine()) != null) {
					lineNo++;
					if (line.startsWith("#")) {
						continue;
					}
					final int pos = line.indexOf('=');
					if (pos > 0) {
						final String key = line.substring(0, pos);
						String value = line.substring(pos + 1);
						if (value.equals("null")) {
							value = null;
						}
						curData.put(key, value);
					} else {
						System.err.println("Parse error on line " + count
								+ " of " + file);
					}
				}

				bufRead.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (DEBUG) {
				System.out.println("Read " + curData.size()
						+ " fields for image #" + (curFile + 1));
			}
		}
	}

	private static void updateCoords(final boolean fromUI) {
		currentLabel = -1;
		int count = 0;
		for (final ProgressLabel label : label2) {
			final Object data = label.getData("field");
			if (data != null && data instanceof String) {
				final String field = (String) data;
				if (curData.containsKey(field)) {
					String value = curData.get(field);
					boolean isNull = false;
					if (value == null) {
						value = "N/A";
						isNull = true;
						label2[count].setMode(ProgressLabel.MODE_SKIP);
					} else {
						label2[count].setMode(ProgressLabel.MODE_COMPLETE);
					}
					if (fromUI) {
						Button button = null;
						if (field.equals(FIELD_EYE_L.field())) {
							if (getData(FIELD_HEAD_T) != null
									&& getData(FIELD_HEAD_B) != null) {
								button = buttonL;
							}
						} else if (field.equals(FIELD_EYE_R.field())) {
							if (getData(FIELD_HEAD_T) != null
									&& getData(FIELD_HEAD_B) != null) {
								button = buttonR;
							}
						} else if (field.equals(FIELD_HEAD_T.field())) {
							button = buttonT;
						} else if (field.equals(FIELD_HEAD_B.field())) {
							button = buttonB;
						}
						if (button != null) {
							button.setSelection(isNull);
							setData(button);
						}
					}
					label2[count].setText("(" + value + ")");
				} else {
					if (currentLabel < 0) {
						label2[count]
								.setText("Left-click: set coordinates; right-click: N/A");
						label.setMode(ProgressLabel.MODE_BUSY);
						currentLabel = count;
					} else {
						label2[count].setText("Unset");
						label2[count].setMode(ProgressLabel.MODE_TODO);
					}
				}
			}
			count++;
		}
		if (currentLabel < 0) {
			label3.setText("Click the image to update the nearest coordinate");
			label3.setMode(ProgressLabel.MODE_BUSY);
		} else {
			label3.setText("Click the image to set the highlighted coordinate");
			label3.setMode(ProgressLabel.MODE_TODO);
		}
		imgBox.redraw();
	}

	private static void save() {
		if (curFile >= 0) {
			File file;
			if (curData.size() > 0) {
				try {
					file = new File(files.get(curFile).getCanonicalPath()
							+ ".txt");
					file.createNewFile();
					if (file.exists()) {
						final BufferedWriter bufferedWriter = new BufferedWriter(
								new FileWriter(file));
						int count = 0;
						for (final Entry<String, String> set : curData
								.entrySet()) {
							bufferedWriter.write(set.getKey() + "="
									+ set.getValue() + "\n");
							count++;
						}
						bufferedWriter.close();
						if (DEBUG) {
							System.out.println("Stored " + count
									+ " fields for image #" + (curFile + 1));
						}
					} else {
						throw new RuntimeException("Failed to save data:\n\t"
								+ file);
					}
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static void listFiles(final File directory) {
		if (!directory.exists()) {
			String path;
			try {
				path = directory.getCanonicalPath();
			} catch (final IOException e) {
				path = directory.getAbsolutePath();
			}
			fatal("Path does not exist:\n\n    " + path);
		}
		final File[] files = directory.listFiles();
		for (final File file : files) {
			if (file.isDirectory()) {
				listFiles(file);
			} else if (file.isFile() && file.getName().endsWith(".jpg")) {
				if (onlyIncomplete) {
					try {
						loadData(file.getCanonicalPath());
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
					if (isAnnotated()) {
						continue;
					}
				}
				FaceSelector.files.add(file);
			}
		}
	}

	private static boolean isAnnotated() {
		boolean complete = true;
		for (final Field f : fieldsCoord) {
			final String field = f.field();
			if (!curData.containsKey(field)) {
				complete = false;
				break;
			}
		}
		return complete;
	}
}
