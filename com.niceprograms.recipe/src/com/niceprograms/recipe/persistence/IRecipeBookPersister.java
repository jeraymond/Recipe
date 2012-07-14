package com.niceprograms.recipe.persistence;

import com.niceprograms.recipe.data.IRecipeBookModel;

/**
 * Interface for recipe book persisters.
 */
public interface IRecipeBookPersister {
	/**
	 * Loads the recipes.
	 * 
	 * @param recipeBook the recipe book into which to load the recipes.
	 */
	void loadRecipes(IRecipeBookModel recipeBook);

	/**
	 * Saves the recipes.
	 * 
	 * @param recipeBook the recipe book to persist.
	 */
	void saveRecipes(IRecipeBookModel recipeBook);
}