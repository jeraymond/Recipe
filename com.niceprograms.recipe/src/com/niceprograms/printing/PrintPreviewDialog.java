package com.niceprograms.printing;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.ui.PrintPreview;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * A print preview dialog that displays a print job and allows for the job to be
 * printed.
 */
public class PrintPreviewDialog extends Dialog {

	private static final int PRINT_ID = IDialogConstants.NO_TO_ALL_ID + 1;

	private Shell shell;

	private PrintJob printJob;

	private Button fitHorz;

	private Button fitVert;

	private Button fitBest;

	private Button exactSize;

	private Button zoomIn;

	private Button zoomOut;

	private Button prevPage;

	private Button nextPage;

	private Button portrait;

	private Button landscape;

	/**
	 * Creates a new print preview dialog.
	 * 
	 * @param parent the parent shell.
	 * @param printJob the print job to preview.
	 */
	public PrintPreviewDialog(Shell parent, PrintJob printJob) {
		super(parent);
		setShellStyle(SWT.RESIZE | getShellStyle());
		this.printJob = printJob;
		this.shell = parent;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(11, false));

		fitHorz = new Button(composite, SWT.PUSH);
		fitVert = new Button(composite, SWT.PUSH);
		fitBest = new Button(composite, SWT.PUSH);
		exactSize = new Button(composite, SWT.PUSH);
		zoomIn = new Button(composite, SWT.PUSH);
		zoomOut = new Button(composite, SWT.PUSH);
		prevPage = new Button(composite, SWT.PUSH);
		nextPage = new Button(composite, SWT.PUSH);
		portrait = new Button(composite, SWT.PUSH);
		landscape = new Button(composite, SWT.PUSH);

		final PrintJob job = printJob;
		final ScrolledComposite scroll = new ScrolledComposite(composite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		final PrintPreview preview = new PrintPreview(scroll, SWT.NONE);

		// configure buttons
		fitHorz.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		fitHorz.setText("Fit Horz.");
		fitHorz.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setFitHorizontal(true);
				preview.setFitVertical(false);

				Rectangle bounds = scroll.getClientArea();
				Point size = preview.computeSize(bounds.width, SWT.DEFAULT);
				bounds.width = Math.max(bounds.width, size.x);
				bounds.width = bounds.width - 20;
				bounds.height = Math.max(bounds.height, size.y);
				preview.setBounds(bounds);
			}
		});

		fitVert.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		fitVert.setText("Fit Vert.");
		fitVert.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setFitVertical(true);
				preview.setFitHorizontal(false);

				Rectangle bounds = scroll.getClientArea();
				Point size = preview.computeSize(SWT.DEFAULT, bounds.height);
				bounds.width = Math.max(bounds.width, size.x);
				bounds.height = Math.max(bounds.height, size.y);
				preview.setBounds(bounds);
			}
		});

		fitBest.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		fitBest.setText("Best Fit");
		fitBest.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setFitVertical(true);
				preview.setFitHorizontal(true);

				preview.setBounds(scroll.getClientArea());
			}
		});

		exactSize.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		exactSize.setText("Exact Size");
		exactSize.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setFitVertical(false);
				preview.setFitHorizontal(false);
				preview.setScale(1);

				Rectangle bounds = scroll.getClientArea();
				Point size = preview.computeSize(1);
				bounds.width = Math.max(bounds.width, size.x);
				bounds.height = Math.max(bounds.height, size.y);
				preview.setBounds(bounds);
			}
		});

		zoomIn.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		zoomIn.setText("Zoom In");
		zoomIn.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				float scale = preview.getAbsoluteScale();
				scale *= 1.1;

				preview.setFitVertical(false);
				preview.setFitHorizontal(false);
				preview.setScale(scale);

				Rectangle bounds = scroll.getClientArea();
				Point size = preview.computeSize(scale);
				bounds.width = Math.max(bounds.width, size.x);
				bounds.height = Math.max(bounds.height, size.y);
				preview.setBounds(bounds);
			}
		});

		zoomOut.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		zoomOut.setText("Zoom Out");
		zoomOut.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				float scale = preview.getAbsoluteScale();
				scale /= 1.1;

				preview.setFitVertical(false);
				preview.setFitHorizontal(false);
				preview.setScale(scale);

				Rectangle bounds = scroll.getClientArea();
				Point size = preview.computeSize(scale);
				bounds.width = Math.max(bounds.width, size.x);
				bounds.height = Math.max(bounds.height, size.y);
				preview.setBounds(bounds);
			}
		});

		prevPage.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		prevPage.setText("<< Page");
		prevPage.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.max(0, preview.getPageIndex() - 1));
			}
		});

		nextPage.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		nextPage.setText("Page >>");
		nextPage.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.min(preview.getPageIndex() + 1,
						preview.getPageCount() - 1));
			}
		});

		portrait.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		portrait.setText("Portrait");
		portrait.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				job.setOrientation(PaperClips.ORIENTATION_PORTRAIT);
				preview.setPrintJob(job);
			}
		});

		landscape.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		landscape.setText("Landscape");
		landscape.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				job.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
				preview.setPrintJob(job);
			}
		});

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 11;
		scroll.setLayoutData(data);
		scroll.setContent(preview);
		scroll.setLayout(null);
		Listener scrollListener = new Listener() {
			@SuppressWarnings("unused")
			public void handleEvent(Event event) {
				Rectangle bounds = scroll.getClientArea();

				scroll.getHorizontalBar()
						.setPageIncrement(bounds.width * 2 / 3);
				scroll.getVerticalBar().setPageIncrement(bounds.height * 2 / 3);

				if (preview.isFitHorizontal()) {
					if (preview.isFitVertical()) {
						// fit in both directions, just use client area.
					} else {
						Point size = preview.computeSize(bounds.width,
								SWT.DEFAULT);
						bounds.width = Math.max(size.x, bounds.width);
						bounds.height = Math.max(size.y, bounds.height);
					}
				} else if (preview.isFitVertical()) {
					Point size = preview
							.computeSize(SWT.DEFAULT, bounds.height);
					bounds.width = Math.max(size.x, bounds.width);
					bounds.height = Math.max(size.y, bounds.height);
				} else {
					Point size = preview.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					bounds.width = Math.max(size.x, bounds.width);
					bounds.height = Math.max(size.y, bounds.height);
				}
				preview.setBounds(bounds);
			}
		};

		scroll.addListener(SWT.Resize, scrollListener);
		preview.setBounds(scroll.getClientArea());
		preview.setFitVertical(true);
		preview.setFitHorizontal(true);
		preview.setPrintJob(job);
		return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Print Preview - " + printJob.getName());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, PRINT_ID, "&Print", true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		getButton(PRINT_ID).forceFocus();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == PRINT_ID) {
			PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
			PrinterData printerData = dialog.open();
			if (printerData != null) {
				PaperClips.print(printJob, printerData);
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Display display = Display.getCurrent();
		Rectangle bounds = display.getBounds();
		int width = 600;
		int height = bounds.height; // full height
		int x = (bounds.width - width) / 2;
		int y = (bounds.height - height) / 2;
		getShell().setBounds(x, y, width, height);
	}
}
