package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * Label provider for recipe books.
 */
public class RecipeBookCategoryLabelProvider extends LabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String string = (String) element;
			if (RecipeBookCategoryContentProvider.ALL_CATEGORY_KEY
					.equals(string)) {
				return "All";
			}
			return string;
		}
		return super.getText(element);
	}
}
