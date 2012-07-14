package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * The cuisine sorter.
 */
public class RecipeBookCuisineViewerSorter extends ViewerSorter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof String && e2 instanceof String) {
			if (e1.equals(e2)) {
				return 0;
			} else if (e1
					.equals(RecipeBookCuisineContentProvider.ALL_CUISINE_KEY)) {
				return -1;
			} else if (e2
					.equals(RecipeBookCuisineContentProvider.ALL_CUISINE_KEY)) {
				return 1;
			} // else fall through if to super.compare()
		}
		return super.compare(viewer, e1, e2);
	}
}
