package com.niceprograms.recipe.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.persistence.TextFileExporter;

/**
 * Exports recipes.
 */
public class ExportRecipesWizard extends RecipeWizard implements IExportWizard {

	private IRecipeBookModel recipeBook;

	private SelectExportTypeWizardPage exportTypeWizardPage;

	private SelectFileWizardPage selectFileWizardPage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		Job exportJob = new Job("Exporting recipes") {

			@Override
			protected IStatus run(@SuppressWarnings("unused")
			IProgressMonitor monitor) {
				IPath exportPath = selectFileWizardPage.getRecipeImportPath();
				if (exportPath != null) {
					TextFileExporter exporter = new TextFileExporter(true,
							exportPath.toOSString());
					exporter.saveRecipes(recipeBook);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openInformation(new Shell(),
									"Export Complete",
									"The export has completed.");
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		exportJob.setUser(true);
		exportJob.schedule();

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		recipeBook = RecipeBookRegistry.getDefault();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		setWindowTitle("Export recipes from the Recipe Book");
		exportTypeWizardPage = new SelectExportTypeWizardPage();
		selectFileWizardPage = new SelectFileWizardPage();
		selectFileWizardPage.setTitle("Export Recipes");
		selectFileWizardPage
				.setDescription("Select a file for which to export the recipes.");
		selectFileWizardPage.setOpen(false);

		addPage(exportTypeWizardPage);
		addPage(selectFileWizardPage);
	}
}
