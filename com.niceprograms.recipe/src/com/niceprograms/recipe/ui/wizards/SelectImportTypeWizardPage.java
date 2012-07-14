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
 * Select import type wizard page for importing recipes.
 */
public class SelectImportTypeWizardPage extends WizardPage {

	private ImportTypes importType;

	/**
	 * Recipe import types.
	 */
	public enum ImportTypes {
		/** Meal master import type. */
		MEAL_MASTER,

		/** Master cook import type. */
		MASTER_COOK
	}

	private Button mealMasterButton;

	// TODO: use this when importer created for Master Cook
	// private Button masterCookButton;

	/**
	 * Creates a new instance of this class.
	 */
	public SelectImportTypeWizardPage() {
		super("selectImportType");
		setTitle("Import Recipes");
		setDescription("Select the type of recipes in import.");
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
		label.setText("Select the type of recipes to import:");

		mealMasterButton = new Button(container, SWT.RADIO);
		final GridData buttonGridData = new GridData();
		buttonGridData.horizontalSpan = 1;
		mealMasterButton.setLayoutData(buttonGridData);
		mealMasterButton.setText("Import Meal Master recipes.");
		mealMasterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				importType = ImportTypes.MEAL_MASTER;
				updatePageComplete();
			}
		});

		// TODO: use this when importer created for Master Cook
		// masterCookButton = new Button(container, SWT.RADIO);
		// final GridData mcGridData = new GridData();
		// mcGridData.horizontalSpan = 1;
		// masterCookButton.setLayoutData(mcGridData);
		// masterCookButton.setText("Import Master Cook recipes.");
		// masterCookButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(@SuppressWarnings("unused")
		// SelectionEvent e) {
		// importType = ImportTypes.MASTER_COOK;
		// updatePageComplete();
		// }
		// });

		updatePageComplete();
	}

	private void updatePageComplete() {
		setPageComplete(false);
		setPageComplete(importType != null);
	}

	/**
	 * Gets the selected import type.
	 * 
	 * @return the selected import type or <code>null</code> if no import type
	 * is selected.
	 */
	public ImportTypes getImportType() {
		return importType;
	}
}
