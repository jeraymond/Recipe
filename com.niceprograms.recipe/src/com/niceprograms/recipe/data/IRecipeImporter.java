package com.niceprograms.recipe.data;

/**
 * Recipe importers import recipes in different formats to the format used by
 * {@link IRecipeBookModel}.
 */
public interface IRecipeImporter {

	/**
	 * Imports recipes from the specified file into the given recipe book.
	 * 
	 * @param recipeBook the recipe book in which to import the recipes.
	 * @param filePath the path to the recipes to import.
	 * @throws RecipeImportException if an error occurs importing the recipes.
	 */
	void importRecipes(IRecipeBookModel recipeBook, String filePath)
			throws RecipeImportException;
}
