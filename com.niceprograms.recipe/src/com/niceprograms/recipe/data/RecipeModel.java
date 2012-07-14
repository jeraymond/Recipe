package com.niceprograms.recipe.data;

import java.util.Arrays;

/**
 * The recipe model.
 */
public class RecipeModel implements IRecipeModel {

	/**
	 * The recipe title.
	 */
	private String title;

	/**
	 * The cuisine.
	 */
	private String cuisine;

	/**
	 * The recipie's category.
	 */
	private String[] categories;

	/**
	 * The recipe ingredients.
	 */
	private String ingredients;

	/**
	 * The recipe instructions.
	 */
	private String directions;
	
	/**
	 * The image path.
	 */
	private String imagePath;

	/**
	 * Creates a new Recipe.
	 * 
	 * @param title the title.
	 * @param cuisine the cuisine.
	 * @param categories the categories.
	 * @param ingredients the ingredients.
	 * @param directions the directions.
	 * @param imagePath the image path.
	 */
	public RecipeModel(String title, String cuisine, String[] categories,
			String ingredients, String directions, String imagePath) {
		this.title = title;
		this.ingredients = ingredients;
		this.directions = directions;
		if (categories != null) {
			this.categories = new String[categories.length];
			System.arraycopy(categories, 0, this.categories, 0,
					categories.length);
		}
		this.cuisine = cuisine;
		this.imagePath = imagePath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCuisine() {
		return cuisine;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getCategories() {
		if (this.categories != null) {
			String[] ret = new String[this.categories.length];
			System
					.arraycopy(this.categories, 0, ret, 0,
							this.categories.length);
			return ret;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getIngredients() {
		return this.ingredients;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDirections() {
		return this.directions;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * Returns the string representation of this class.
	 * 
	 * @return the string representation of this class.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String separator = System.getProperty("line.separator");

		sb.append("Title: ");
		sb.append(title);
		sb.append(separator);
		sb.append("Cuisine:");
		sb.append(cuisine);
		sb.append(separator);
		sb.append("Categories:");
		sb.append(Arrays.toString(this.categories));
		sb.append(separator);
		sb.append("Ingredients:\n");
		sb.append(ingredients);
		sb.append(separator);
		sb.append("Directions:\n");
		sb.append(directions);

		return sb.toString();
	}
}
