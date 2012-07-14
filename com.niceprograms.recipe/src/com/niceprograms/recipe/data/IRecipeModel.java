package com.niceprograms.recipe.data;

/**
 * The model interface for recipes.
 */
public interface IRecipeModel {

	/**
	 * Gets the title.
	 * 
	 * @return the title.
	 */
	String getTitle();

	/**
	 * Gets the cuisine.
	 * 
	 * @return the cuisine.
	 */
	String getCuisine();

	/**
	 * Gets the categories.
	 * 
	 * @return the category.
	 */
	String[] getCategories();

	/**
	 * Gets the ingredients.
	 * 
	 * @return the ingredients.
	 */
	String getIngredients();

	/**
	 * Gets the directions.
	 * 
	 * @return the directions.
	 */
	String getDirections();

	/**
	 * Gets the image path.
	 * 
	 * @return the image path.
	 */
	String getImagePath();
}
