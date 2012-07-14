package com.niceprograms.recipe.ui;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.utility.StringUtilities;

/**
 * The recipe book viewer filter. Filters recipes shown based upon the selected
 * cuisine and category.
 */
public class RecipeBookModelViewerFilter extends ViewerFilter implements
		ISelectionChangedListener {

	private ComboViewer cuisineComboViewer;

	private ComboViewer categoryComboViewer;

	private ListViewer targetViewer;

	private String selectedCuisine = RecipeBookCuisineContentProvider.ALL_CUISINE_KEY;

	private String selectedCategory = RecipeBookCategoryContentProvider.ALL_CATEGORY_KEY;

	private String searchString;

	private SearchFieldTypes searchType;

	/**
	 * Gets the search type.
	 * 
	 * @return the search type.
	 */
	public SearchFieldTypes getSearchType() {
		return searchType;
	}

	/**
	 * Sets the search type.
	 * 
	 * @param searchType the search type.
	 */
	public void setSearchType(SearchFieldTypes searchType) {
		this.searchType = searchType;
	}

	/**
	 * Gets the search string.
	 * 
	 * @return the search string.
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * Sets the search string.
	 * 
	 * @param searchString the search string.
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
		refresh();
	}

	/**
	 * Creates a new RecipeBookViewerFilter.
	 * 
	 * @param viewer the viewer.
	 * @param cuisineComboViewer the cuisine viewer.
	 * @param categoryComboViewer the category viewer.
	 */
	public RecipeBookModelViewerFilter(ListViewer viewer,
			ComboViewer cuisineComboViewer, ComboViewer categoryComboViewer) {
		this.cuisineComboViewer = cuisineComboViewer;
		this.categoryComboViewer = categoryComboViewer;
		if (this.cuisineComboViewer != null) {
			this.cuisineComboViewer.addSelectionChangedListener(this);
		}
		if (this.categoryComboViewer != null) {
			this.categoryComboViewer.addSelectionChangedListener(this);
		}
		this.targetViewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IRecipeModel) {
			if (element instanceof IRecipeModel) {
				boolean select = true;
				IRecipeModel recipe = (IRecipeModel) element;
				if (!RecipeBookCuisineContentProvider.ALL_CUISINE_KEY
						.equals(selectedCuisine)) {
					if (!recipe.getCuisine().equals(selectedCuisine)) {
						select = false;
					}
				}

				if (!RecipeBookCategoryContentProvider.ALL_CATEGORY_KEY
						.equals(selectedCategory)) {
					if (!StringUtilities.arrayContainsStartsWith(recipe
							.getCategories(), selectedCategory)) {
						select = false;
					}
				}

				if (searchString != null && !searchString.equals("")) {
					String upperCaseSearchString = searchString.toUpperCase()
							.trim();
					boolean contains = false;
					if (searchType == null
							|| searchType == SearchFieldTypes.ALL) {
						contains = contains(upperCaseSearchString, recipe
								.getTitle())
								|| StringUtilities.arrayContainsStartsWith(
										recipe.getCategories(),
										upperCaseSearchString)
								|| contains(upperCaseSearchString, recipe
										.getCuisine())
								|| contains(upperCaseSearchString, recipe
										.getDirections())
								|| contains(upperCaseSearchString, recipe
										.getIngredients());
					} else {
						switch (searchType) {
						case TITLE:
							contains = contains(upperCaseSearchString, recipe
									.getTitle());
							break;
						case CUISINE:
							contains = contains(upperCaseSearchString, recipe
									.getCuisine());
							break;
						case CATEGORY:
							contains = StringUtilities.arrayContainsStartsWith(
									recipe.getCategories(),
									upperCaseSearchString);
							break;
						case INGREDIENTS:
							contains = contains(upperCaseSearchString, recipe
									.getIngredients());
							break;
						case DIRECTIONS:
							contains = contains(upperCaseSearchString, recipe
									.getDirections());
							break;
						default:
							throw new RuntimeException("Bad search type: "
									+ searchType);
						}
					}
					if (!contains) {
						select = false;
					}
				}
				return select;
			}
		}
		return false;
	}

	private boolean contains(String search, String contents) {
		if (search != null && contents != null) {
			String[] words = contents.split(" ");
			for (String word : words) {
				if (word.toUpperCase().startsWith(search)) {
					return true;
				}
			}

			String concat = "";
			for (int i = words.length - 1; i >= 0; i--) {
				concat = words[i].toUpperCase() + " " + concat;
				if (concat.startsWith(search)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() == cuisineComboViewer) {
			if (event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				selectedCuisine = (String) selection.getFirstElement();
				refresh();
			}
		}

		if (event.getSource() == categoryComboViewer) {
			if (event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				selectedCategory = (String) selection.getFirstElement();
				refresh();
			}
		}
	}

	private void refresh() {
		if (targetViewer != null) {
			targetViewer.refresh();
		}
	}
}
