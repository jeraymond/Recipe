package com.niceprograms.recipe.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for selecting a file.
 */
public class SelectFileWizardPage extends WizardPage {

	private Text recipeFileText;

	private IPath recipeImportPath;

	private boolean open;

	/**
	 * Creates a new SelectFileWizardPage instance.
	 */
	public SelectFileWizardPage() {
		super("selectFile");
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);
		label.setText("Select recipe file from which to import the recipes.");

		final Label recipeLabel = new Label(container, SWT.NONE);
		final GridData recipeLabelGridData = new GridData(
				GridData.HORIZONTAL_ALIGN_END);
		recipeLabel.setLayoutData(recipeLabelGridData);
		recipeLabel.setText("Recipe File:");

		recipeFileText = new Text(container, SWT.BORDER);
		final GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
		recipeFileText.setLayoutData(textGridData);
		recipeFileText.addModifyListener(new ModifyListener() {
			public void modifyText(@SuppressWarnings("unused")
			ModifyEvent e) {
				updatePageComplete();
			}
		});

		final Button browseButton = new Button(container, SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				browseForSourceFile();
			}
		});
		browseButton.setText("Browse...");

		setPageComplete(false);
	}

	private void updatePageComplete() {
		setPageComplete(false);
		IPath sourceLoc = getSourceLocation();
		if (open
				&& (sourceLoc.toFile().isDirectory() || !sourceLoc.toFile()
						.exists())) {
			setMessage(null);
			setErrorMessage("Please select an existing file.");
			recipeImportPath = null;
		} else {
			setMessage(null);
			setErrorMessage(null);
			recipeImportPath = sourceLoc;
			setPageComplete(true);
		}
	}

	private IPath getSourceLocation() {
		String text = recipeFileText.getText().trim();
		if (text.length() == 0) {
			return null;
		}
		IPath path = new Path(text);
		if (!path.isAbsolute()) {
			path = path.makeAbsolute();
		}
		return path;
	}

	private void browseForSourceFile() {
		IPath path = browse(getSourceLocation(), open);
		if (path == null) {
			return;
		}
		recipeFileText.setText(path.toOSString());
	}

	private IPath browse(IPath path, boolean mustExist) {
		FileDialog dialog = new FileDialog(getShell(), mustExist ? SWT.OPEN
				: SWT.SAVE);
		if (path != null) {
			File file = path.toFile();
			if (file.isDirectory()) {
				dialog.setFilterPath(path.removeLastSegments(0).toOSString());
			} else if (file.isFile()) {
				dialog.setFilterPath(path.removeLastSegments(1).toOSString());
				dialog.setFileName(path.lastSegment());
			}
		}
		String result = dialog.open();
		if (result == null) {
			return null;
		}
		return new Path(result);
	}

	/**
	 * Gets the recipe import path.
	 * 
	 * @return the recipe file import path or <code>null</code> if no file is
	 * selected.
	 */
	public IPath getRecipeImportPath() {
		return recipeImportPath;
	}

	/**
	 * True if page set to open mode, false if set to save mode.
	 * 
	 * @return true if page set to open mode, false if set to save mode.
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Set to true for file open mode, false for file save mode.
	 * 
	 * @param open set to true for file open mode, false for file save mode.
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}
}
