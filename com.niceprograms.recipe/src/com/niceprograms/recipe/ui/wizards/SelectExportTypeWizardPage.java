package com.niceprograms.recipe.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Select export type wizard page for exporting recipes.
 */
public class SelectExportTypeWizardPage extends WizardPage {

	private ExportTypes exportType;

	/**
	 * Recipe export types.
	 */
	public enum ExportTypes {

		/** Plain text export type. */
		PLAIN_TEXT
	}

	private Button plainTextButton;

	/**
	 * Creates a new instance of this class.
	 */
	public SelectExportTypeWizardPage() {
		super("selectExportType");
		setTitle("Export Recipes");
		setDescription("Select the type of recipes in export.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);
		label.setText("Select recipe export format:");

		plainTextButton = new Button(container, SWT.RADIO);
		final GridData buttonGridData = new GridData();
		buttonGridData.horizontalSpan = 1;
		plainTextButton.setLayoutData(buttonGridData);
		plainTextButton.setText("Plain text");
		plainTextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				exportType = ExportTypes.PLAIN_TEXT;
				updatePageComplete();
			}
		});

		updatePageComplete();
	}

	private void updatePageComplete() {
		setPageComplete(false);
		setPageComplete(exportType != null);
	}

	/**
	 * Gets the selected export type.
	 * 
	 * @return the selected export type or <code>null</code> if no export type
	 * is selected.
	 */
	public ExportTypes getExportType() {
		return exportType;
	}
}
