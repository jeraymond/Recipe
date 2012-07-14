package com.niceprograms.recipe.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Imports recipes in the Meal Master format.
 */
public class MealMasterImporter implements IRecipeImporter {

	private static final String INVALID_TEXT = "Invalid Meal Master recipe file.";

	private enum ReadStates {
		/** Ready state. */
		READY,

		/** Title state. */
		TITLE,

		/** Categories state. */
		CATEGORIES,

		/** Yield state. */
		YIELD,

		/** Ingredients state. */
		INGREDIENTS,

		/** Directions state. */
		DIRECTIONS
	}

	private ReadStates state;

	/**
	 * Creates a new MealMasterImporter.
	 */
	public MealMasterImporter() {
		super();
		state = ReadStates.READY;
	}

	/**
	 * {@inheritDoc}
	 */
	public void importRecipes(IRecipeBookModel recipeBook, String filePath)
			throws RecipeImportException {
		String title = null;
		String cuisine = null;
		String[] categories = null;
		String ingredients = null;
		String directions = null;

		File file = new File(filePath);

		if (!file.exists()) {
			throw new RecipeImportException("Specified file does not exist: "
					+ filePath);
		}

		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line = null;

			while ((line = reader.readLine()) != null) {
				switch (state) {
				case READY:
					if (line.startsWith("MMMMM----- ")
							|| line.startsWith("----------")) {
						line = reader.readLine();
						if (line.trim().equals("")) {
							title = null;
							cuisine = "Default Cuisine";
							categories = null;
							ingredients = null;
							directions = null;

							state = ReadStates.TITLE;
						} else {
							throw new RecipeImportException(INVALID_TEXT);
						}
					} else {
						throw new RecipeImportException(INVALID_TEXT);
					}
					break;
				case TITLE:
					if (line.trim().startsWith("Title:")) {
						title = line.trim().replaceFirst("Title:", "").trim();
						state = ReadStates.CATEGORIES;
					} else {
						throw new RecipeImportException(INVALID_TEXT);
					}
					break;
				case CATEGORIES:
					if (line.trim().startsWith("Categories:")) {
						categories = line.trim()
								.replaceFirst("Categories:", "").split(",");
						int numCategories = categories.length;
						for (int i = 0; i < numCategories; i++) {
							categories[i] = categories[i].trim();
						}
						state = ReadStates.YIELD;
					} else {
						throw new RecipeImportException(INVALID_TEXT);
					}
					break;
				case YIELD:
					if (line.trim().startsWith("Yield:")) {
						@SuppressWarnings("unused")
						String yield = line.trim().replaceFirst("Yield:", "");

						// Yield unused
						line = reader.readLine();
						if (line.trim().equals("")) {
							state = ReadStates.INGREDIENTS;
						} else {
							throw new RecipeImportException(INVALID_TEXT);
						}
					} else {
						throw new RecipeImportException(INVALID_TEXT);
					}
					break;
				case INGREDIENTS:
					if (line.trim().equals("")) {
						state = ReadStates.DIRECTIONS;
					} else {
						String currentIngredients = ingredients;
						if (currentIngredients == null) {
							currentIngredients = line
									.replace("MMMMM------", "")
									.replace("-", "").trim();
							ingredients = currentIngredients;
						} else {
							currentIngredients += "\n";
							currentIngredients += line.replace("MMMMM------",
									"").replace("-", "").trim();
							ingredients = currentIngredients;
						}
					}
					break;
				case DIRECTIONS:
					if (line.startsWith("MMMMM------")
							|| line.startsWith("------")) {
						String currentIngredients = ingredients;
						if (currentIngredients == null) {
							currentIngredients = line
									.replace("MMMMM------", "")
									.replace("-", "").trim();
							ingredients = currentIngredients;
						} else {
							currentIngredients += "\n\n";
							currentIngredients += line.replace("MMMMM------",
									"").replace("-", "").trim();
							ingredients = currentIngredients;
						}
						state = ReadStates.INGREDIENTS;
					} else if (line.trim().equals("MMMMM")
							|| line.trim().equals("-----")) {
						if (recipeBook instanceof RecipeBookModel) {
							((RecipeBookModel) recipeBook).createRecipeSilent(
									title, cuisine, categories, ingredients,
									directions, null);
						} else {
							recipeBook.createRecipe(title, cuisine, categories,
									ingredients, directions, null);
						}
						line = reader.readLine();
						state = ReadStates.READY;
					} else if (line.trim().startsWith("From: ")) {
						// do no include from line
					} else {
						String currentDirections = directions;
						if (currentDirections == null) {
							currentDirections = line.trim();
							directions = currentDirections;
						} else {
							currentDirections += "\n";
							currentDirections += line.trim();
							directions = currentDirections;
						}
					}
					break;
				default:
					throw new RecipeImportException("Invalid state: " + state);
				}
			}
			recipeBook.save();
		} catch (FileNotFoundException e) {
			throw new RecipeImportException(e);
		} catch (IOException e) {
			throw new RecipeImportException(e);
		}
	}
}
