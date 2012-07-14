package com.niceprograms.recipe.editors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;

import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.ui.UIConstants;
import com.niceprograms.utilities.ImageUtilities;
import com.niceprograms.utility.StringUtilities;

/**
 * An editor that displays a read-only view of a recipe.
 */
public class ViewRecipeEditor extends EditorPart implements ISaveablePart2 {

    private static final int LARGE_IMAGE_SIZE = 450;

    private static final int SMALL_IMAGE_SIZE = 250;

    /** This class' ID. */
    public static String ID = "com.niceprograms.recipe.editors.readonlyrecipeeditor";

    private IRecipeModel recipe;

    private IAction copyAction;

    private Browser browser;

    private String tempRecipeHmlPath;

    private String tempLargeImageHtmlPath;

    private StyledText recipeStyledText;

    /**
     * Creates a new ViewRecipeEditor.
     */
    public ViewRecipeEditor() {
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
        // unused
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {
        // unused
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        recipe = ((RecipeEditorInput) input).getRecipe();
        setPartName(recipe.getTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        try {
            browser = new Browser(parent, SWT.NONE);
            browser.dispose();
            createHtmlView(parent);
        } catch (SWTError error) {
            RecipeLog.logError("Browser is not available.", error);
            createSWTView(parent);
        }
    }

    private void createSWTView(Composite parent) {
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

        //
        String separator = System.getProperty("line.separator");
        String title = recipe.getTitle();
        String cuisine = recipe.getCuisine();
        String category = StringUtilities.toString(recipe.getCategories());
        String ingredientsTitle = "Ingredients";
        String ingredients = recipe.getIngredients();
        String directionsTitle = "Directions";
        String directions = recipe.getDirections();

        String[] lines = new String[] { title + separator, cuisine + " - " + category + separator,
                separator + ingredientsTitle + separator, ingredients + separator, separator + directionsTitle + separator,
                directions };

        recipeStyledText = new StyledText(content, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        recipeStyledText.setEditable(false);
        String text = "";
        for (String line : lines) {
            text += line;
        }
        recipeStyledText.setText(text);

        Display display = recipeStyledText.getDisplay();
        FontData data = recipeStyledText.getFont().getFontData()[0];

        Font titleFont = new Font(display, data.getName(), 18, data.getStyle() | SWT.BOLD);
        Font cuisineAndCategoryFont = new Font(display, data.getName(), 12, data.getStyle() | SWT.BOLD);
        Font ingredientsAndDirectionsTitleFont = new Font(display, data.getName(), 12, data.getStyle() | SWT.BOLD);
        Font ingredientsAndDirectionsFont = new Font(display, data.getName(), 12, data.getStyle());

        // create styles
        StyleRange[] styles = new StyleRange[6];

        styles[0] = new StyleRange();
        styles[0].font = titleFont;
        styles[1] = new StyleRange();
        styles[1].font = cuisineAndCategoryFont;
        styles[2] = new StyleRange();
        styles[2].font = ingredientsAndDirectionsTitleFont;
        styles[3] = new StyleRange();
        styles[3].font = ingredientsAndDirectionsFont;
        styles[4] = new StyleRange();
        styles[4].font = ingredientsAndDirectionsTitleFont;
        styles[5] = new StyleRange();
        styles[5].font = ingredientsAndDirectionsFont;

        // create ranges
        int[] ends = new int[6];
        ends[0] = lines[0].length();
        ends[1] = ends[0] + lines[1].length();
        ends[2] = ends[1] + lines[2].length();
        ends[3] = ends[2] + lines[3].length();
        ends[4] = ends[3] + lines[4].length();
        ends[5] = ends[4] + lines[5].length();

        int[] ranges = new int[] { 0, ends[0], ends[0], ends[1] - ends[0], ends[1], ends[2] - ends[1], ends[2],
                ends[3] - ends[2], ends[3], ends[4] - ends[3], ends[4], ends[5] - ends[4] };

        recipeStyledText.setStyleRanges(ranges, styles);
        recipeStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the copy action
        copyAction = new Action("Copy") {
            @Override
            public void run() {
                recipeStyledText.copy();
            }
        };

        IAction tempAction = ActionFactory.COPY.create(getSite().getWorkbenchWindow());
        copyAction.setToolTipText(tempAction.getToolTipText());
        copyAction.setImageDescriptor(tempAction.getImageDescriptor());

        // create the context menu
        MenuManager manager = new MenuManager("viewRecipeEditorPopup");
        manager.add(copyAction);
        Menu menu = manager.createContextMenu(content);
        recipeStyledText.setMenu(menu);
    }

    private void createHtmlView(Composite parent) {
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

        long timeStamp = System.currentTimeMillis();
        tempRecipeHmlPath = "tempRecipe" + timeStamp;

        File tempRecipeHtmlFile = new File(tempRecipeHmlPath);
        tempRecipeHtmlFile = tempRecipeHtmlFile.getAbsoluteFile();
        tempRecipeHmlPath = tempRecipeHtmlFile.getAbsolutePath();

        tempLargeImageHtmlPath = "tempRecipeLarge" + timeStamp;

        File tempLargeImageHtmlFile = new File(tempLargeImageHtmlPath);
        tempLargeImageHtmlFile = tempLargeImageHtmlFile.getAbsoluteFile();
        tempLargeImageHtmlPath = tempLargeImageHtmlFile.getAbsolutePath();

        int[] size = new int[2];

        try {
            if (recipe.getImagePath() != null) {
                File imageFile = new File(recipe.getImagePath());
                if (imageFile.exists()) {
                    Image recipeImage = new Image(parent.getDisplay(), recipe.getImagePath());
                    Rectangle bounds = recipeImage.getBounds();
                    size = ImageUtilities.scaleImage(bounds.width, bounds.height, SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE);
                    recipeImage.dispose();
                }
            }
        } catch (Exception e) {
            RecipeLog.logError("Error scaling image.", e);
        }

        String recipeHtml = getRecipeText(tempLargeImageHtmlPath, size[0], size[1]);
        writeFile(tempRecipeHtmlFile, recipeHtml);

        size = new int[2];

        try {
            if (recipe.getImagePath() != null) {
                File imageFile = new File(recipe.getImagePath());
                if (imageFile.exists()) {
                    Image largeImage = new Image(parent.getDisplay(), recipe.getImagePath());
                    Rectangle bounds = largeImage.getBounds();
                    size = ImageUtilities.scaleImage(bounds.width, bounds.height, LARGE_IMAGE_SIZE, LARGE_IMAGE_SIZE);
                    largeImage.dispose();
                }
            }
        } catch (Exception e) {
            RecipeLog.logError("Error scaling image.", e);
        }

        String largeImageHtml = getLargeImageText(tempRecipeHmlPath, size[0], size[1]);
        writeFile(tempLargeImageHtmlFile, largeImageHtml);
        browser = new Browser(content, SWT.NONE);
        browser.setUrl(tempRecipeHmlPath);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private void writeFile(File file, String text) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(text);
            out.close();
        } catch (IOException e) {
            RecipeLog.logError("Error writing temp recipe file.", e);
        }
    }

    private String getLargeImageText(String back, int imageX, int imageY) {
        String html = "<html><head><title>Recipe</title><style type=\"text/css\"><!-- body { font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; -->}</style></head><body>";

        String imagePath = recipe.getImagePath();
        File imageFile = new File(imagePath);

        html += "<table align=\"center\"><tr><td>";
        html += "<h1>" + recipe.getTitle() + "</h1>";
        html += "</td></tr></table>";

        if (imageFile.exists()) {
            html += "<table width=\"80\" border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"0\"><tr><td align=\"center\" valign=\"top\">"
                    + "<a href=\""
                    + back
                    + "\">"
                    + "<img src=\""
                    + recipe.getImagePath()
                    + "\" "
                    + "width=\""
                    + imageX
                    + "\" height=\"" + imageY + "\">" + "</a>" + "</td></tr></table>";
        }
        html += "<table align=\"center\"><tr><td>";
        html += "<a href=\"" + back + "\">Back to Recipe</a>";
        html += "</td></tr></table>";
        html += "</body></html>";
        return html;
    }

    private String getRecipeText(String largeImage, int imageX, int imageY) {

        String html = "<html><head><title>Recipe</title><style type=\"text/css\"><!-- body { font-family: Tahoma, Verdana, Arial, Helvetica, sans-serif; -->}</style></head><body>";

        String imagePath = recipe.getImagePath();
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            html += "<table width=\"80\" border=\"0\" align=\"right\" cellpadding=\"5\" cellspacing=\"0\"><tr><td align=\"center\" valign=\"top\">"
                    + "<a href=\""
                    + largeImage
                    + "\">"
                    + "<img src=\""
                    + recipe.getImagePath()
                    + "\" "
                    + "width=\""
                    + imageX
                    + "\" height=\""
                    + imageY
                    + "\">"
                    + "</a>"
                    + "<br>"
                    + "<a href=\""
                    + largeImage
                    + "\">See a larger image.</a>" + "</td></tr></table>";
        }

        html += "<h1>" + recipe.getTitle() + "</h1>";

        html += "<p>";
        html += "<h2>" + recipe.getCuisine() + " - " + StringUtilities.toString(recipe.getCategories()) + "</h2>";

        html += "<p>";
        html += "<h3>Ingredients</h3>";
        html += recipe.getIngredients().replace("\n", "<br>");

        html += "<p>";
        html += "<h3>Directions</h3>";
        html += recipe.getDirections().replace("\n", "<br>");

        html += "</body></html>";
        return html;
    }

    /**
     * Gets the copy action for this editor.
     * 
     * @return the copy action.
     */
    public IAction getCopyAction() {
        return copyAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        if (browser != null && !browser.isDisposed()) {
            browser.setFocus();
        } else if (recipeStyledText != null && !recipeStyledText.isDisposed()) {
            recipeStyledText.setFocus();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int promptToSaveOnClose() {
        if (tempRecipeHmlPath != null) {
            File del = new File(tempRecipeHmlPath);
            del.delete();
        }

        if (tempLargeImageHtmlPath != null) {
            File del = new File(tempLargeImageHtmlPath);
            del.delete();
        }
        return ISaveablePart2.NO;
    }
}
