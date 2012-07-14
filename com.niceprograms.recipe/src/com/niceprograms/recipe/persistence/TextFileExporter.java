package com.niceprograms.recipe.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.utility.StringUtilities;

/**
 * Exports recipes to a text file.
 */
public class TextFileExporter implements IRecipeBookPersister {

	private boolean errorDialogOnErrors;

	private String filePath;

	/**
	 * Creates a new TextFileExporter
	 * 
	 * @param errorDialogOnErrors true to show dialog on errors.
	 * @param filePath the file path.
	 */
	public TextFileExporter(boolean errorDialogOnErrors, String filePath) {
		this.errorDialogOnErrors = errorDialogOnErrors;
		this.filePath = filePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void loadRecipes(IRecipeBookModel recipeBook) {
		throw new RuntimeException(
				"Method TextFileExporter.loadRecipes() not supported.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveRecipes(IRecipeBookModel recipeBook) {
		BufferedWriter out = null;
		final String separator = System.getProperty("line.separator");
		try {
			out = new BufferedWriter(new FileWriter(filePath));
			for (IRecipeModel recipe : recipeBook.getRecipes()) {
				out.write(recipe.getTitle().replace("\n", separator));
				out.write(separator);
				out.write(separator);
				out.write(recipe.getCuisine().replace("\n", separator));
				out.write(" - ");
				out.write(StringUtilities.toString(recipe.getCategories()));
				out.write(separator);
				out.write(separator);
				out.write("Ingredients");
				out.write(separator);
				out.write(separator);
				out.write(recipe.getIngredients().trim().replace("\n",
						separator));
				out.write(separator);
				out.write(separator);
				out.write("Directions");
				out.write(separator);
				out.write(separator);
				out.write(recipe.getDirections().trim()
						.replace("\n", separator));
				out.write(separator);
				out.write(separator);
				out.write(separator);
			}
		} catch (final IOException e) {
			RecipeLog.logError("Error exporting recipes.", e);
			if (errorDialogOnErrors) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(new Shell(),
								"Error Exporting Recipes",
								"An error occurred exporting the recipes."
										+ separator + e.getMessage());
					}
				});
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				    RecipeLog.logError("Error saving recipes.", e);
				}
			}
		}
	}
}
