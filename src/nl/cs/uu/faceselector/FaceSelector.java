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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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

	private final static String PATH = "../Subjects/";

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

	private static String[] fieldsToggle = new String[] { "cropT", "cropR",
			"cropB", "cropL" };
	private static String[] fieldsCoord = new String[] { "eyeL", "eyeR",
			"headT", "headB", "nose" };

	private static int currentLabel;

	private static boolean onlyIncomplete;

	public static void main(final String[] args) {
		display = new Display();
		shell = new Shell(display);

		display.addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				Button button = null;
				switch (e.keyCode) {
				case SWT.ARROW_RIGHT:
				case SWT.PAGE_DOWN:
					setFile(curFile + 1);
					break;
				case SWT.ARROW_LEFT:
				case SWT.PAGE_UP:
					setFile(curFile - 1);
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
								setFile(curFile);
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

		final int result = showMessage(SWT.YES | SWT.NO | SWT.ICON_QUESTION,
				"Would you like to display only unannotated files?");
		if (result == SWT.YES) {
			onlyIncomplete = true;
		}

		final File path = new File(PATH);
		if (onlyIncomplete) {
			System.out.println("Collecting incompletely annotated files...");
		} else {
			System.out.print("Collecting files... ");
		}
		listFiles(path);
		if (files.size() == 0) {
			fatal("No images found in path:\n\t" + PATH);
		} else {
			System.out.println("OK (" + FaceSelector.files.size() + ")");
		}

		shell.setLayout(new FormLayout());
		shell.setText(FaceSelector.class.getSimpleName());
		shell.setSize(500, 600);

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
			buttonT.setText("Top");
			buttonT.setData("field", "cropT");
			buttonT.addSelectionListener(croppedListener);
			buttonR = new Button(group1, SWT.TOGGLE);
			buttonR.setText("Right");
			buttonR.setData("field", "cropR");
			buttonR.addSelectionListener(croppedListener);
			buttonB = new Button(group1, SWT.TOGGLE);
			buttonB.setText("Bottom");
			buttonB.setData("field", "cropB");
			buttonB.addSelectionListener(croppedListener);
			buttonL = new Button(group1, SWT.TOGGLE);
			buttonL.setText("Left");
			buttonL.setData("field", "cropL");
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
					updateCoords();
				}

				@Override
				public void mouseDoubleClick(final MouseEvent e) {
				}
			};
			label1 = new Label[fieldsCoord.length];
			label2 = new ProgressLabel[fieldsCoord.length];
			label1[0] = new Label(group2, SWT.NORMAL);
			label1[0].setText("Left eye:");
			label2[0] = new ProgressLabel(group2, true);
			label1[1] = new Label(group2, SWT.NORMAL);
			label1[1].setText("Right eye:");
			label2[1] = new ProgressLabel(group2, true);
			label1[2] = new Label(group2, SWT.NORMAL);
			label1[2].setText("Head top:");
			label2[2] = new ProgressLabel(group2, true);
			label1[3] = new Label(group2, SWT.NORMAL);
			label1[3].setText("Head bottom:");
			label2[3] = new ProgressLabel(group2, true);
			label1[4] = new Label(group2, SWT.NORMAL);
			label1[4].setText("Nose:");
			label2[4] = new ProgressLabel(group2, true);
			for (int i = 0; i < label2.length; i++) {
				label2[i].addMouseListener(labelClick, i);
			}
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
			fd.right = new FormAttachment(50, 0);
			fd.bottom = new FormAttachment(100, -5);
			imgLabel.setLayoutData(fd);
			imgLabel.setAlignment(SWT.CENTER);
			Button button;
			button = new Button(imgControl, SWT.PUSH);
			button.setText("< Previous");
			fd = new FormData();
			fd.left = new FormAttachment(imgLabel, 0, SWT.RIGHT);
			fd.right = new FormAttachment(75, -1);
			button.setLayoutData(fd);
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					setFile(curFile - 1);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}
			});
			button = new Button(imgControl, SWT.PUSH);
			button.setText("Next >");
			fd = new FormData();
			fd.left = new FormAttachment(75, 1);
			fd.right = new FormAttachment(100, 0);
			button.setLayoutData(fd);
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					setFile(curFile + 1);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
				}
			});
		}

		imgBox = new Composite(shell, SWT.BORDER);
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
					}
				}
			});
		}

		setFile(0);

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

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void fatal(final Exception e) {
		fatal(e.getMessage());
	}

	private static void fatal(final String message) {
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
		setCoord(value);
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
		updateCoords();
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
			throw new RuntimeException(
					"Button does not contain toggle data");
		}
	}

	private static void setFile(final int i) {
		save();
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
			imgBox.redraw();
			loadData(path);
			for (final String field : fieldsToggle) {
				final String value = curData.get(field);
				boolean selection = false;
				if (value != null && value.equals("true")) {
					selection = true;
				}
				Button button = null;
				if (field.equals("cropT")) {
					button = buttonT;
				} else if (field.equals("cropR")) {
					button = buttonR;
				} else if (field.equals("cropB")) {
					button = buttonB;
				} else if (field.equals("cropL")) {
					button = buttonL;
				}
				if (button != null) {
					button.setSelection(selection);
				}
			}
			int count = 0;
			for (final String field : fieldsCoord) {
				label2[count].setData("field", field);
				count++;
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		updateCoords();
	}

	private static void loadData(final String path) {
		curData.clear();
		final File file = new File(path + ".txt");
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
			System.out.println("Read " + curData.size()
					+ " fields for image #" + (curFile + 1));
		}
	}

	private static void updateCoords() {
		currentLabel = -1;
		int count = 0;
		for (final ProgressLabel label : label2) {
			final Object data = label.getData("field");
			if (data != null && data instanceof String) {
				final String field = (String) data;
				if (curData.containsKey(field)) {
					String value = curData.get(field);
					if (value == null) {
						value = "N/A";
						label2[count].setMode(ProgressLabel.MODE_SKIP);
					} else {
						label2[count].setMode(ProgressLabel.MODE_COMPLETE);
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
						System.out.println("Stored " + count
								+ " fields for image #" + (curFile + 1));
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

	private static void listFiles(final File path) {
		if (!path.exists()) {
			throw new RuntimeException("Path does not exist:\n\t" + path);
		}
		final File[] files = path.listFiles();
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
					boolean incomplete = false;
					for (final String field : fieldsCoord) {
						if (!curData.containsKey(field)) {
							incomplete = true;
							break;
						}
					}
					if (!incomplete) {
						continue;
					}
				}
				FaceSelector.files.add(file);
			}
		}
	}
}
