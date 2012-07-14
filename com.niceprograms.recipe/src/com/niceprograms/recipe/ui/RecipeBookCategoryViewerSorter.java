package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sorter for recipe book categories.
 */
public class RecipeBookCategoryViewerSorter extends ViewerSorter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof String && e2 instanceof String) {
			if (e1.equals(e2)) {
				return 0;
			} else if (e1
					.equals(RecipeBookCategoryContentProvider.ALL_CATEGORY_KEY)) {
				return -1;
			} else if (e2
					.equals(RecipeBookCategoryContentProvider.ALL_CATEGORY_KEY)) {
				return 1;
			} // else fall through if to super.compare()
		}
		return super.compare(viewer, e1, e2);
	}
}
