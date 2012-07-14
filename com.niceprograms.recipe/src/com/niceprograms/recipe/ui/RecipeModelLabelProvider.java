package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.LabelProvider;

import com.niceprograms.recipe.data.IRecipeModel;

/**
 * Label provider for recipe models.
 */
public class RecipeModelLabelProvider extends LabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IRecipeModel) {
			return ((IRecipeModel) element).getTitle();
		}
		return super.getText(element);
	}
}
