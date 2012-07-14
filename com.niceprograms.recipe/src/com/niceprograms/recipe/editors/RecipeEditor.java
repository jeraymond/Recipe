package com.niceprograms.recipe.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.preferences.GeneralPreferencesPage;
import com.niceprograms.recipe.ui.CategoriesDialog;
import com.niceprograms.recipe.ui.UIConstants;
import com.niceprograms.utility.StringUtilities;

/**
 * This class edits recipes.
 */
public class RecipeEditor extends EditorPart implements ModifyListener,
		ISaveablePart2 {

	private static final String PICTURES = "pictures";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	/** This class' ID */
	public static String ID = "com.niceprograms.recipe.editors.recipeeditor";

	private boolean isDirty;

	private Label titleLabel;

	private Text titleText;

	private Label ingredientsLabel;

	private Text ingredientsText;

	private Label directionsLabel;

	private Text directionsText;

	private Label cuisineLabel;

	private Combo cuisineCombo;

	private Label categoryLabel;

	private Text categoriesText;

	private Label imagePathLabel;

	private Text imagePathText;

	private IRecipeModel recipe;

	private IRecipeBookModel recipeBook;

	/**
	 * Creates a new recipe editor.
	 */
	public RecipeEditor() {
		super();
	}

	/**
	 * Gets the recipe.
	 * 
	 * @return the recipe.
	 */
	public IRecipeModel getRecipe() {
		return recipe;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!recipeDataIsInvalid()) {

			// copy recipe image to pictures folder
			IEclipsePreferences preferences = new ConfigurationScope()
					.getNode(Application.PLUGIN_ID);

			String filePath = preferences.get(
					GeneralPreferencesPage.RECIPE_FILE_PATH, "");
			if (!"".equals(filePath)) {
				File file = new File(filePath);
				file = file.getAbsoluteFile();
				if (file.isFile()) {
					File dir = new File(file.getParent());
					if (dir.isDirectory()) {
						String picturesPath = dir.getAbsolutePath()
								+ FILE_SEPARATOR + PICTURES;
						File picturesDir = new File(picturesPath);
						if (!picturesDir.exists()) {
							picturesDir.mkdirs();
						}
						if (picturesDir.exists() && picturesDir.isDirectory()) {
							String imagePath = imagePathText.getText().trim();
							if (imagePath != null && !"".equals(imagePath)) {
								File sourceFile = new File(imagePath);
								if (sourceFile.exists() && sourceFile.isFile()) {
									String destinationPath = picturesDir
											.getAbsolutePath()
											+ FILE_SEPARATOR
											+ sourceFile.getName();
									File destinationFile = new File(
											destinationPath);
									if (!destinationFile.exists()) {
										InputStream in = null;
										OutputStream out = null;
										try {
											in = new FileInputStream(sourceFile);
											out = new FileOutputStream(
													destinationFile);

											// Transfer bytes from in to out
											byte[] buf = new byte[1024];
											int len;
											while ((len = in.read(buf)) > 0) {
												out.write(buf, 0, len);
											}
											in.close();
											out.close();
											imagePathText
													.setText(destinationFile
															.getAbsolutePath());
										} catch (IOException e) {
											RecipeLog
													.logError(
															"Error copying picture file.",
															e);
										}
									}
								}
							}
						}
					}
				}
			}

			// save the recipe
			IRecipeModel newRecipe = recipeBook.createRecipe(titleText
					.getText().trim(), cuisineCombo.getText().trim(),
					categoriesText.getText().split(", "), ingredientsText
							.getText().trim(), directionsText.getText().trim(),
					imagePathText.getText().trim());
			if (recipe != null) {
				recipeBook.deleteRecipe(recipe);
			}
			recipe = newRecipe;

			setTitle();
			updateInput();
		} else {
			MessageDialog
					.openInformation(
							getSite().getShell(),
							"Invalid Recipe",
							"The recipe is invalid. You must fill in all of the fields (except the Image). The recipe was not saved.");
		}
		setDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
		// unused
	}

	private void updateInput() {
		setInput(new RecipeEditorInput(recipeBook, recipe));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);

		RecipeEditorInput recipeEditorInput = (RecipeEditorInput) input;
		recipe = recipeEditorInput.getRecipe();
		recipeBook = recipeEditorInput.getRecipeBook();

		setTitle();
	}

	private void setTitle() {
		if (recipe != null) {
			setPartName("Edit Recipe - " + recipe.getTitle());
		} else {
			setPartName("Edit Recipe - Untitled");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		return this.isDirty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	private void setDirty() {
		isDirty = recipeDataIsInvalid() || recipe == null ? true
				: !(recipe.getTitle().equals(titleText.getText().trim())
						&& recipe.getCuisine().equals(
								cuisineCombo.getText().trim())
						&& imagePathText.getText().trim().equals(
								recipe.getImagePath())
						&& StringUtilities.stringArrayEquals(recipe
								.getCategories(), categoriesText.getText()
								.split(", "))
						&& StringUtilities.compareBodies(recipe
								.getIngredients(), ingredientsText.getText()) && StringUtilities
						.compareBodies(recipe.getDirections(), directionsText
								.getText()));
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = SWT.NONE;
		layout.marginHeight = SWT.NONE;
		layout.verticalSpacing = UIConstants.VERTICAL_SPACING;
		layout.marginLeft = UIConstants.BORDER_SPACING;
		layout.marginRight = UIConstants.BORDER_SPACING;
		layout.marginTop = UIConstants.BORDER_SPACING;
		layout.marginBottom = UIConstants.BORDER_SPACING;
		content.setLayout(layout);

		// top
		Composite top = new Composite(content, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout topLayout = new GridLayout(3, false);
		topLayout.marginWidth = SWT.NONE;
		topLayout.marginHeight = SWT.NONE;
		topLayout.verticalSpacing = UIConstants.VERTICAL_SPACING;
		top.setLayout(topLayout);

		// title
		titleLabel = new Label(top, SWT.NONE);

		titleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		titleLabel.setText("Title");
		titleLabel.setToolTipText("Set the title.");

		titleText = new Text(top, SWT.BORDER | SWT.SINGLE);
		GridData titleTextGridData = new GridData(GridData.FILL, GridData.FILL,
				true, false);
		titleTextGridData.horizontalSpan = 2;
		titleText.setLayoutData(titleTextGridData);
		titleText.setEditable(true);
		if (recipe != null) {
			titleText.setText(recipe.getTitle());
		}
		titleText.addModifyListener(this);

		// cuisine selection
		cuisineLabel = new Label(top, SWT.NONE);

		cuisineLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		cuisineLabel.setText("Cuisine");
		cuisineLabel.setToolTipText("Select the desired cuisine.");

		cuisineCombo = new Combo(top, SWT.DROP_DOWN);
		for (String cuisine : recipeBook.getCuisines()) {
			cuisineCombo.add(cuisine);
		}
		GridData cuisineComboGridData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		cuisineComboGridData.horizontalSpan = 2;
		cuisineCombo.setLayoutData(cuisineComboGridData);
		setCuisine();
		cuisineCombo.addModifyListener(this);

		// category selection
		categoryLabel = new Label(top, SWT.NONE);

		categoryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		categoryLabel.setText("Categories");
		categoryLabel.setToolTipText("Select the desired category.");

		categoriesText = new Text(top, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		if (recipe != null) {
			updateCategoriesText(recipe.getCategories());
		}
		categoriesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		categoriesText.addModifyListener(this);

		Button selectButton = new Button(top, SWT.PUSH);
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				String[] initialSelection = null;
				if (recipe != null) {
					initialSelection = recipe.getCategories();
				}

				CategoriesDialog categoriesDialog = new CategoriesDialog(
						Display.getDefault().getActiveShell(), recipeBook
								.getCategories().toArray(new String[0]),
						initialSelection, "Select Recipe Categories");
				categoriesDialog
						.setMessage("Select the recipe categories. To select multiple categories CTRL + Click the mouse on multiple categories.");
				categoriesDialog.open();
				if (categoriesDialog.getReturnCode() == Window.OK) {
					updateCategoriesText(categoriesDialog.getCategories());
				}
			}
		});
		selectButton.setText("Select...");
		selectButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));

		imagePathLabel = new Label(top, SWT.NONE);

		imagePathLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		imagePathLabel.setText("Image");
		imagePathLabel.setToolTipText("Select an image.");

		imagePathText = new Text(top, SWT.BORDER);
		imagePathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		if (recipe != null) {
			imagePathText.setText(recipe.getImagePath());
		}
		imagePathText.addModifyListener(this);

		Button browseButton = new Button(top, SWT.PUSH);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getDefault()
						.getActiveShell(), SWT.OPEN);
				String path = dialog.open();
				if (path == null) {
					path = "";
				}
				imagePathText.setText(path);

			}
		});
		browseButton.setText("Browse...");
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));

		// bottom
		Composite bottom = new Composite(content, SWT.NONE);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout bottomLayout = new GridLayout(1, false);
		bottomLayout.marginWidth = SWT.NONE;
		bottomLayout.marginHeight = SWT.NONE;
		bottomLayout.verticalSpacing = UIConstants.VERTICAL_SPACING;
		bottom.setLayout(bottomLayout);

		// ingredients
		ingredientsLabel = new Label(bottom, SWT.NONE);

		ingredientsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false));
		ingredientsLabel.setText("Ingredients");
		ingredientsLabel.setToolTipText("Enter the ingredients below.");

		ingredientsText = new Text(bottom, SWT.BORDER | SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
		GridData ingredientsGridData = new GridData(GridData.FILL,
				GridData.FILL, true, true);
		ingredientsGridData.heightHint = ingredientsText.getLineHeight() * 2;
		ingredientsText.setLayoutData(ingredientsGridData);
		if (recipe != null) {
			ingredientsText.setText(recipe.getIngredients());
		}
		ingredientsText.addModifyListener(this);

		// directions
		directionsLabel = new Label(bottom, SWT.NONE);

		directionsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false));
		directionsLabel.setText("Directions");
		directionsLabel.setToolTipText("Enter the directions below.");

		directionsText = new Text(bottom, SWT.BORDER | SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
		GridData directionsGridData = new GridData(GridData.FILL,
				GridData.FILL, true, true);
		directionsGridData.heightHint = directionsText.getLineHeight() * 2;
		directionsText.setLayoutData(directionsGridData);
		if (recipe != null) {
			directionsText.setText(recipe.getDirections());
		}
		directionsText.addModifyListener(this);
	}

	private void updateCategoriesText(String[] categories) {
		if (categories != null && categoriesText != null
				&& !categoriesText.isDisposed()) {
			categoriesText.setText(StringUtilities.toString(categories));
		}
	}

	private void setCuisine() {
		if (cuisineCombo != null && recipe != null
				&& !cuisineCombo.isDisposed()) {
			int numItems = cuisineCombo.getItemCount();
			for (int i = 0; i < numItems; i++) {
				if (cuisineCombo.getItem(i).equals(recipe.getCuisine())) {
					cuisineCombo.select(i);
					break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		if (titleText != null && !titleText.isDisposed()) {
			titleText.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void modifyText(ModifyEvent e) {
		if (e.getSource() == titleText || e.getSource() == categoriesText
				|| e.getSource() == cuisineCombo
				|| e.getSource() == ingredientsText
				|| e.getSource() == directionsText
				|| e.getSource() == imagePathText) {
			setDirty();
		}
	}

	private boolean recipeDataIsInvalid() {
		return "".equals(titleText.getText().trim())
				|| "".equals(cuisineCombo.getText().trim())
				|| "".equals(categoriesText.getText().trim())
				|| "".equals(ingredientsText.getText().trim())
				|| "".equals(directionsText.getText().trim());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		// unused
	}

	/**
	 * {@inheritDoc}
	 */
	public int promptToSaveOnClose() {
		if (recipeDataIsInvalid()) {
			if (MessageDialog
					.openQuestion(
							getSite().getShell(),
							"Invalid Recipe",
							"The recipe is invalid. You must fill in all of the fields (except the Image). Do you want to fix the recipe?")) {
				return ISaveablePart2.CANCEL;

			}
			return ISaveablePart2.NO;
		}

		// delete temp files
		return ISaveablePart2.DEFAULT;
	}
}
