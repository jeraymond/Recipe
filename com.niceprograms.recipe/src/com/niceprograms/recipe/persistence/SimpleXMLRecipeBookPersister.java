package com.niceprograms.recipe.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.xml.LiteXmlParserWriter;

/**
 * A class that persists recipes to a file in a simple XML format.
 */
public class SimpleXMLRecipeBookPersister implements IRecipeBookPersister {

	private static final String RECIPEBOOK_TAG = "recipebook";

	private static final String DIRECTIONS_TAG = "directions";

	private static final String INGREDIENTS_TAG = "ingredients";

	private static final String CATEGORY_TAG = "category";

	private static final String CATEGORIES_TAG = "categories";

	private static final String CUISINE_TAG = "cuisine";

	private static final String TITLE_TAG = "title";
	
	private static final String IMAGE_PATH_TAG = "image";

	private String filePath;

	private final Lock lock = new ReentrantLock();

	/**
	 * Creates a new instance of SimpleXMLRecipeBookPersister.
	 * 
	 * @param filePath the file path.
	 */
	public SimpleXMLRecipeBookPersister(String filePath) {
		super();
		this.filePath = filePath;
	}

	/**
	 * Loads the recipes into the recipe book from the specified xml file.
	 * 
	 * @param recipeBook the recipe book.
	 */
	public void loadRecipes(IRecipeBookModel recipeBook) {
		lock.lock();
		try {
			if (recipeFileIsValid(filePath)) {
				BufferedReader reader;
				File file = new File(filePath);

				if (file.exists()) {
					try {
						reader = new BufferedReader(new FileReader(file));
					} catch (FileNotFoundException ex) {
						throw new RuntimeException(ex);
					}

					LiteXmlParserWriter root;
					try {
						root = new LiteXmlParserWriter(reader);
						int numRecipes = root.size();
						for (int i = 0; i < numRecipes; ++i) {
							LiteXmlParserWriter xmlRecipe = root.getElement(i);
							String title = cleanse(xmlRecipe.findElement(
									TITLE_TAG).getValue());
							String cuisine = cleanse(xmlRecipe.findElement(
									CUISINE_TAG).getValue());

							String singleCategory = xmlRecipe.findElement(
									CATEGORY_TAG).getValue();

							String[] categories = null;
							if (singleCategory != null) {
								categories = new String[] { singleCategory };
							} else {
								LiteXmlParserWriter xmlCategories = xmlRecipe
										.findElement(CATEGORIES_TAG);
								if (xmlCategories != null) {
									ArrayList<String> strCats = new ArrayList<String>();
									int numCategories = xmlCategories.size();
									for (int j = 0; j < numCategories; j++) {
										LiteXmlParserWriter xmlCategory = xmlCategories
												.getElement(j);
										if (xmlCategory.getTag().equals(
												CATEGORY_TAG)) {
											strCats.add(xmlCategory.getValue());
										}
									}
									categories = strCats.toArray(new String[0]);
								}
							}

							String ingredients = cleanse(xmlRecipe.findElement(
									INGREDIENTS_TAG).getValue());
							String directions = cleanse(xmlRecipe.findElement(
									DIRECTIONS_TAG).getValue());
							
							LiteXmlParserWriter element = xmlRecipe.findElement(
									IMAGE_PATH_TAG);
							String val = (element == null ? "" : element.getValue());
							String imagePath = cleanse(val);
							
							recipeBook.createRecipe(title, cuisine, categories,
									ingredients, directions, imagePath);
						}
					} catch (LiteXmlParserWriter.LiteXmlParserWriterEncodingException ex) {
						throw new RuntimeException(ex);
					} catch (LiteXmlParserWriter.LiteXmlParserWriterException ex) {
						throw new RuntimeException(ex);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					} finally {
						try {
							reader.close();
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				} else {
					throw new RuntimeException("Invalid recipe file.");
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Saves the recipes from the recipe book into the specified xml file.
	 * 
	 * @param recipeBook the recipe book to save.
	 */
	public void saveRecipes(IRecipeBookModel recipeBook) {
		lock.lock();
		try {
			List<IRecipeModel> recipes = recipeBook.getRecipes();
			LiteXmlParserWriter root = new LiteXmlParserWriter(RECIPEBOOK_TAG);

			for (IRecipeModel recipe : recipes) {
				LiteXmlParserWriter xmlRecipe;
				try {
					xmlRecipe = root.addElement("recipe");
					xmlRecipe.addElement(TITLE_TAG, cleanse(recipe.getTitle()));
					xmlRecipe.addElement(CUISINE_TAG, cleanse(recipe
							.getCuisine()));
					LiteXmlParserWriter xmlCategories = xmlRecipe
							.addElement(CATEGORIES_TAG);
					String[] categories = recipe.getCategories();
					for (String category : categories) {
						xmlCategories.addElement(CATEGORY_TAG,
								cleanse(category));
					}
					xmlRecipe.addElement(INGREDIENTS_TAG, cleanse(recipe
							.getIngredients()));
					xmlRecipe.addElement(DIRECTIONS_TAG, cleanse(recipe
							.getDirections()));
					xmlRecipe.addElement(IMAGE_PATH_TAG, cleanse(recipe
							.getImagePath()));
				} catch (LiteXmlParserWriter.LiteXmlParserWriterException ex) {
					throw new RuntimeException(ex);
				}
			}

			PrintWriter pw = null;

			try {
				pw = new PrintWriter(new File(filePath));
				root.serialize(pw);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private String cleanse(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	/**
	 * Creates an empty recipe file.
	 */
	public void createEmptyRecipeFile() {
		lock.lock();
		try {
			LiteXmlParserWriter root = new LiteXmlParserWriter(RECIPEBOOK_TAG);

			PrintWriter pw = null;

			try {
				pw = new PrintWriter(new File(filePath));
				root.serialize(pw);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Determines if the recipe file contains valid data.
	 * 
	 * @param filePath the file path.
	 * @return true if valid, false otherwise.
	 */
	public static boolean recipeFileIsValid(String filePath) {
		boolean valid = true;

		BufferedReader reader;
		File file = new File(filePath);

		if (file.exists()) {
			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}

			LiteXmlParserWriter root;
			try {
				root = new LiteXmlParserWriter(reader);
				int numRecipes = root.size();
				for (int i = 0; i < numRecipes; ++i) {
					LiteXmlParserWriter xmlRecipe = root.getElement(i);
					if (xmlRecipe.findElement(TITLE_TAG) == null
							|| xmlRecipe.findElement(CUISINE_TAG) == null
							|| xmlRecipe.findElement(CATEGORY_TAG) == null ||

							xmlRecipe.findElement(INGREDIENTS_TAG) == null
							|| xmlRecipe.findElement(DIRECTIONS_TAG) == null) {
						valid = false;
						break;
					}
				}
			} catch (Exception e) {
				valid = false;
			} finally {
				try {
					reader.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		return valid;
	}
}
