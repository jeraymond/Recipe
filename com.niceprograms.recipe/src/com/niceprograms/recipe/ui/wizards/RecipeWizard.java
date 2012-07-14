package com.niceprograms.recipe.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;

/**
 * Base class for recipe wizards.
 */
public abstract class RecipeWizard extends Wizard {

	/**
	 * Gets the recipe book from the workbench.
	 * 
	 * @param workbench the workbench.
	 * @return the recipe book or null if none is found.
	 */
	protected IRecipeBookModel getRecipeBook() {
		return RecipeBookRegistry.getDefault();
	}
}
