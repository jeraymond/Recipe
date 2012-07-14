package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * The cuisine label provider.
 */
public class RecipeBookCuisineLabelProvider extends LabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String string = (String) element;
			if (RecipeBookCuisineContentProvider.ALL_CUISINE_KEY.equals(string)) {
				return "Any";
			}
			return string;
		}
		return super.getText(element);
	}
}
