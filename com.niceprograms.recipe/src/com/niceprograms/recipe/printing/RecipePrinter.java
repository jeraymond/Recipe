package com.niceprograms.recipe.printing;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.TextPrint;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;

import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.utility.StringUtilities;

/**
 * Recipe printer.
 */
public class RecipePrinter {

	private RecipePrinter() {
		super();
	}

	/**
	 * Opens the print dialog then prints the recipe if a printer is selected.
	 * 
	 * @param recipe the recipe to print.
	 * @param shell the program shell.
	 */
	public static void printRecipe(IRecipeModel recipe, Shell shell) {

		if (recipe != null) {

			PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
			PrinterData printerData = dialog.open();
			if (printerData != null) {
				PaperClips.print(createPrintJob(recipe), printerData);
			}
		}
	}

	/**
	 * Creates a print job from the given recipe.
	 * 
	 * @param recipe the recipe.
	 * @return the print job for the recipe.
	 */
	public static PrintJob createPrintJob(IRecipeModel recipe) {
		StringBuilder textToPrint = new StringBuilder();
		String separator = System.getProperty("line.separator");
		textToPrint.append(recipe.getTitle());
		textToPrint.append(separator);
		textToPrint.append(separator);
		textToPrint.append(recipe.getCuisine());
		textToPrint.append(" - ");
		textToPrint.append(StringUtilities.toString(recipe.getCategories()));
		textToPrint.append(separator);
		textToPrint.append(separator);
		textToPrint.append("Ingredients");
		textToPrint.append(separator);
		textToPrint.append(separator);
		textToPrint.append(recipe.getIngredients());
		textToPrint.append(separator);
		textToPrint.append(separator);
		textToPrint.append("Directions");
		textToPrint.append(separator);
		textToPrint.append(separator);
		textToPrint.append(recipe.getDirections());

		TextPrint text = new TextPrint(textToPrint.toString());
		PrintJob job = new PrintJob(recipe.getTitle(), text); // .setMargins(margins);
		return job;

	}

}
