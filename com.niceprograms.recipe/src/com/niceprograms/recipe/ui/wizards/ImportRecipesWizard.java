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
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeImporter;
import com.niceprograms.recipe.data.MealMasterImporter;
import com.niceprograms.recipe.data.RecipeBookRegistry;

/**
 * Wizard for importing recipes.
 */
public class ImportRecipesWizard extends RecipeWizard implements IImportWizard {

	private SelectImportTypeWizardPage importTypePage;

	private SelectFileWizardPage filePage;

	private IRecipeBookModel recipeBook;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		Job importJob = new ImportJob("Importing recipes...", recipeBook,
				new MealMasterImporter(), filePage.getRecipeImportPath());
		importJob.setUser(true);
		importJob.schedule();

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
		setWindowTitle("Import Recipes into the Recipe Book");
		importTypePage = new SelectImportTypeWizardPage();
		filePage = new SelectFileWizardPage();
		filePage.setTitle("Import Recipes");
		filePage.setDescription("Select the recipe file to import.");
		filePage.setOpen(true);

		addPage(importTypePage);
		addPage(filePage);
	}

	private class ImportJob extends Job {
		private IRecipeImporter importer;

		private IPath recipePath;

		private IRecipeBookModel targetRecipeBook;

		/**
		 * Creates a new Import Job.
		 * 
		 * @param name
		 * @param recipeBook
		 * @param importer
		 * @param recipePath
		 */
		public ImportJob(String name, IRecipeBookModel recipeBook,
				IRecipeImporter importer, IPath recipePath) {
			super(name);
			this.importer = importer;
			this.recipePath = recipePath;
			this.targetRecipeBook = recipeBook;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (importer != null && recipePath != null
					&& targetRecipeBook != null) {
				try {
					importer.importRecipes(targetRecipeBook, recipePath
							.toOSString());
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog
									.openInformation(new Shell(),
											"Import Complete",
											"The recipes have been successfully imported.");
						}
					});
				} catch (final Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(new Shell(),
									"Error Importing Recipes", e.getMessage());
						}
					});
				} finally {
					monitor.done();
				}
			} else {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(new Shell(),
								"Error Importing Recipes",
								"Invalid recipe input.");
					}
				});
			}
			return Status.OK_STATUS;
		}
	}
}
