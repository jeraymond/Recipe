package com.niceprograms.recipe.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.BackingStoreException;

import com.niceprograms.jface.action.ControlContribution;
import com.niceprograms.jface.action.ToolBarPullDownMenu;
import com.niceprograms.recipe.Application;
import com.niceprograms.recipe.RecipeLog;
import com.niceprograms.recipe.actions.ViewRecipeAction;
import com.niceprograms.recipe.data.IRecipeBookModel;
import com.niceprograms.recipe.data.IRecipeModel;
import com.niceprograms.recipe.data.RecipeBookModel;
import com.niceprograms.recipe.data.RecipeBookRegistry;
import com.niceprograms.recipe.persistence.SimpleXMLRecipeBookPersister;
import com.niceprograms.recipe.preferences.GeneralPreferencesPage;
import com.niceprograms.recipe.ui.IImageKeys;
import com.niceprograms.recipe.ui.RecipeBookCategoryContentProvider;
import com.niceprograms.recipe.ui.RecipeBookCategoryLabelProvider;
import com.niceprograms.recipe.ui.RecipeBookCategoryViewerSorter;
import com.niceprograms.recipe.ui.RecipeBookCuisineContentProvider;
import com.niceprograms.recipe.ui.RecipeBookCuisineLabelProvider;
import com.niceprograms.recipe.ui.RecipeBookCuisineViewerSorter;
import com.niceprograms.recipe.ui.RecipeBookModelContentProvider;
import com.niceprograms.recipe.ui.RecipeBookModelViewerFilter;
import com.niceprograms.recipe.ui.RecipeBookModelViewerSorter;
import com.niceprograms.recipe.ui.RecipeModelLabelProvider;
import com.niceprograms.recipe.ui.SearchFieldTypes;
import com.niceprograms.recipe.ui.UIConstants;

/**
 * The view to display the list of recipes.
 */
public class RecipesView extends ViewPart implements IRecipeBookModel.IListener, IPreferenceChangeListener {

    /** This class' ID. */
    public static final String ID = "com.niceprograms.recipe.views.recipes";

    private String recipeFilePath;

    private IRecipeBookModel recipeBook;

    private ComboViewer cuisineComboViewer;

    private ComboViewer categoryComboViewer;

    private ListViewer listViewer;

    private Text searchText;

    private boolean preferencesLoaded; // = false;

    private Timer timer;

    private SearchFieldTypes activeSearchField;

    private List<SearchCheckAction> searchCheckActions;

    private RecipeBookModelViewerFilter filter;

    /**
     * Creates a new RecipesView.
     */
    public RecipesView() {
        super();
        recipeBook = new RecipeBookModel();
        RecipeBookRegistry.registerRecipeBook(recipeBook);

        IContextService contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
        contextService.activateContext("com.niceprograms.recipe.context");

        IEclipsePreferences preferences = new ConfigurationScope().getNode(Application.PLUGIN_ID);
        preferences.addPreferenceChangeListener(this);
    }

    /**
     * Loads the recipes.
     * 
     * @param onStartUp true if loading on startup, false if loading at a time other than startup.
     */
    public void loadRecipes(boolean onStartUp) {
        IEclipsePreferences preferences = new ConfigurationScope().getNode(Application.PLUGIN_ID);

        if (!preferencesLoaded) {
            String filePath = preferences.get(GeneralPreferencesPage.RECIPE_FILE_PATH, "");

            recipeFilePath = filePath;
            preferencesLoaded = true;
        }

        if (recipeFilePath == null || "".equals(recipeFilePath)) {

            String errorMessage = null;

            // create default recipe path
            String userHome = System.getProperty("user.home");
            String recipeDirPath = userHome + File.separatorChar + "Recipes";
            File recipeDir = new File(recipeDirPath);
            if (!recipeDir.exists()) {
                if (!recipeDir.mkdir()) {
                    errorMessage = "Unable to create recipe file. The application will exit.";
                }
            }

            if (errorMessage != null) {
                MessageDialog.openError(getSite().getShell(), "Unable To Create Recipe File", errorMessage);
                System.exit(1);
            }

            recipeFilePath = recipeDirPath + File.separatorChar + "recipes.xml";

            if (recipeFilePath != null) {

                File file = new File(recipeFilePath);
                if (!file.exists()) {
                    if (!recipeFilePath.toLowerCase().endsWith(".xml")) {
                        recipeFilePath = recipeFilePath + ".xml";
                    }
                }

                preferences.put(GeneralPreferencesPage.RECIPE_FILE_PATH, recipeFilePath);
                try {
                    preferences.flush();
                } catch (BackingStoreException e) {
                    RecipeLog.logError(e);
                }
            }
        }

        if (recipeFilePath != null) {
            File file = new File(recipeFilePath);
            if (!file.exists()) {
                SimpleXMLRecipeBookPersister persister = new SimpleXMLRecipeBookPersister(recipeFilePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                persister.createEmptyRecipeFile();
                recipeBook.setPersister(persister);
                recipeBook.addListener(this);
                setInputs();
            } else {
                try {
                    recipeBook.clear();
                    if (!SimpleXMLRecipeBookPersister.recipeFileIsValid(recipeFilePath)) {
                        throw new Exception("Invalid recipe database file.");
                    }

                    cuisineComboViewer.setInput(null);
                    categoryComboViewer.setInput(null);
                    listViewer.setInput(null);

                    Job importJob = new Job("Loading Recipes...") {
                        @Override
                        protected IStatus run(@SuppressWarnings("unused")
                        IProgressMonitor monitor) {
                            recipeBook.removeListener(RecipesView.this);
                            recipeBook.setPersister(new SimpleXMLRecipeBookPersister(recipeFilePath));
                            recipeBook.load();
                            recipeBook.addListener(RecipesView.this);
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    setInputs();
                                }
                            });
                            return Status.OK_STATUS;
                        }
                    };
                    importJob.setUser(true);
                    importJob.schedule();
                } catch (Exception e) {
                    MessageDialog.openError(getSite().getShell(), "Error Loading Recipes", "The recipe file "
                            + recipeFilePath + " is not valid. Please choose a different file.");
                    recipeFilePath = null;
                    loadRecipes(onStartUp);
                }
            }
        } else {
            if (onStartUp) {
                System.exit(0);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite viewComposite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth = SWT.NONE;
        layout.marginHeight = SWT.NONE;
        layout.verticalSpacing = UIConstants.VERTICAL_SPACING;
        layout.marginLeft = UIConstants.BORDER_SPACING;
        layout.marginRight = UIConstants.BORDER_SPACING;
        layout.marginTop = UIConstants.BORDER_SPACING;
        layout.marginBottom = UIConstants.BORDER_SPACING;
        viewComposite.setLayout(layout);

        Composite top = new Composite(viewComposite, SWT.NONE);
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout topLayout = new GridLayout(3, false);
        topLayout.marginWidth = SWT.NONE;
        topLayout.marginHeight = SWT.NONE;
        topLayout.verticalSpacing = UIConstants.VERTICAL_SPACING;
        top.setLayout(topLayout);

        // cuisine selection
        Label cuisineLabel = new Label(top, SWT.NONE);

        cuisineLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cuisineLabel.setText("Cuisine");
        cuisineLabel.setToolTipText("Select the desired cuisine.");

        cuisineComboViewer = new ComboViewer(top, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.NONE);
        cuisineComboViewer.setContentProvider(new RecipeBookCuisineContentProvider(true));
        cuisineComboViewer.setLabelProvider(new RecipeBookCuisineLabelProvider());
        cuisineComboViewer.setSorter(new RecipeBookCuisineViewerSorter());
        final GridData cuisineGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        cuisineGridData.horizontalSpan = 2;
        cuisineComboViewer.getControl().setLayoutData(cuisineGridData);
        cuisineComboViewer.getCombo().setVisibleItemCount(UIConstants.COMBO_VISIBLE_ITEM_COUNT);

        // category selection
        Label categoryLabel = new Label(top, SWT.NONE);

        categoryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        categoryLabel.setText("Category");
        categoryLabel.setToolTipText("Select the desired category.");

        categoryComboViewer = new ComboViewer(top, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.NONE);
        categoryComboViewer.setContentProvider(new RecipeBookCategoryContentProvider(true));
        categoryComboViewer.setLabelProvider(new RecipeBookCategoryLabelProvider());
        categoryComboViewer.setSorter(new RecipeBookCategoryViewerSorter());
        final GridData categoryGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        categoryGridData.horizontalSpan = 2;
        categoryComboViewer.getControl().setLayoutData(categoryGridData);
        categoryComboViewer.getCombo().setVisibleItemCount(UIConstants.COMBO_VISIBLE_ITEM_COUNT);

        // search composite
        Composite searchComp = new Composite(viewComposite, SWT.NONE);
        searchComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout searhLayout = new GridLayout(2, false);
        searhLayout.marginWidth = SWT.NONE;
        searhLayout.marginHeight = SWT.NONE;
        searhLayout.verticalSpacing = UIConstants.VERTICAL_SPACING;
        searchComp.setLayout(searhLayout);

        // recipe list
        final ViewRecipeAction viewRecipeAction = new ViewRecipeAction(getSite().getWorkbenchWindow());

        listViewer = new ListViewer(viewComposite, SWT.BORDER | SWT.TOP | SWT.V_SCROLL);
        listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listViewer.setContentProvider(new RecipeBookModelContentProvider());
        listViewer.setSorter(new RecipeBookModelViewerSorter());
        listViewer.setLabelProvider(new RecipeModelLabelProvider());
        filter = new RecipeBookModelViewerFilter(listViewer, cuisineComboViewer, categoryComboViewer);
        listViewer.addFilter(filter);
        listViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(@SuppressWarnings("unused")
            DoubleClickEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        viewRecipeAction.run();

                    }
                });

            }
        });
        getSite().setSelectionProvider(listViewer);

        // search label
        Label searchLabel = new Label(searchComp, SWT.NONE);
        final GridData searchLabelLayoutData = new GridData();
        searchLabel.setLayoutData(searchLabelLayoutData);
        searchLabel.setText("Search");

        Label separator = new Label(searchComp, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData separatorLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        separator.setLayoutData(separatorLayoutData);

        createSearchToolBar(searchComp, filter);
    }

    @SuppressWarnings("unused")
    private void createSearchToolBar(Composite parent, final RecipeBookModelViewerFilter modelFilter) {

        // clear search tool bar
        final ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        final GridData toolBarGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        toolBarGridData.horizontalSpan = 2;
        toolBar.setLayoutData(toolBarGridData);

        ToolBarManager toolBarManager = new ToolBarManager(toolBar);

        // search type menu
        ToolBarPullDownMenu searchTypePullDownMenu = new ToolBarPullDownMenu();
        searchTypePullDownMenu.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID,
                IImageKeys.SEARCH_PULL_DOWN));
        addSearchOptions(searchTypePullDownMenu.getMenuManager());
        toolBarManager.add(searchTypePullDownMenu);

        // search text box
        class SearchTask extends TimerTask {
            @Override
            public void run() {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        doSearch();
                    }
                });
            }
        }

        // search text box
        ControlContribution searchTextContrib = new ControlContribution("searchText") {
            @Override
            protected Control createControl(Composite parentControl) {
                searchText = new Text(parentControl, SWT.BORDER);
                searchText.addModifyListener(new ModifyListener() {
                    public void modifyText(@SuppressWarnings("unused")
                    ModifyEvent e) {
                        if (timer != null) {
                            timer.cancel();
                        }
                        timer = new Timer();
                        timer.schedule(new SearchTask(), 400);
                    }
                });
                return searchText;
            }
        };
        searchTextContrib.setWidthItemToWatch(listViewer.getControl(), 46);
        toolBarManager.add(searchTextContrib);

        // clear search box
        Action clearSearchTextAction = new Action() {
            @Override
            public void run() {
                searchText.setText("");
            }
        };
        clearSearchTextAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID,
                IImageKeys.REMOVE_ENABLED));
        clearSearchTextAction.setToolTipText("Clears the search box.");
        clearSearchTextAction.setText("Clear");
        toolBarManager.add(clearSearchTextAction);

        toolBarManager.update(true);
    }

    private void doSearch() {
        if (filter != null) {
            filter.setSearchType(activeSearchField);
            filter.setSearchString(searchText.getText());
        }
    }

    private void addSearchOptions(IMenuManager menuManager) {
        searchCheckActions = new ArrayList<SearchCheckAction>();

        SearchCheckAction action = new SearchCheckAction("All", SearchFieldTypes.ALL);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(true);

        action = new SearchCheckAction("Title", SearchFieldTypes.TITLE);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(false);

        action = new SearchCheckAction("Cuisine", SearchFieldTypes.CUISINE);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(false);

        action = new SearchCheckAction("Category", SearchFieldTypes.CATEGORY);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(false);

        action = new SearchCheckAction("Ingredients", SearchFieldTypes.INGREDIENTS);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(false);

        action = new SearchCheckAction("Directions", SearchFieldTypes.DIRECTIONS);
        searchCheckActions.add(action);
        menuManager.add(action);
        action.setChecked(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        if (listViewer != null && !listViewer.getControl().isDisposed()) {
            listViewer.getControl().setFocus();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    public void itemChanged(final ChangeType type, final IRecipeModel data) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (type != IRecipeBookModel.IListener.ChangeType.SAVE) {
                    recipeBook.save();
                }
                listViewer.refresh();
                if (type == IRecipeBookModel.IListener.ChangeType.ADD) {
                    StructuredSelection selection = new StructuredSelection(data);
                    listViewer.setSelection(selection, true);
                    setFocus(); // request focus so that selection updates and actions are enabled properly for the correctly
                                // selected item (especially on editor save)
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    public void preferenceChange(PreferenceChangeEvent event) {
        IEclipsePreferences preferences = new ConfigurationScope().getNode(Application.PLUGIN_ID);
        String filePath = preferences.get(GeneralPreferencesPage.RECIPE_FILE_PATH, "");
        if (!filePath.equals(recipeFilePath)) {
            preferencesLoaded = false;
            loadRecipes(false);
        }
    }

    private void uncheckSearchActions() {
        if (searchCheckActions != null) {
            for (SearchCheckAction action : searchCheckActions) {
                action.setChecked(false);
            }
        }
    }

    private void setInputs() {
        cuisineComboViewer.setInput(recipeBook);
        categoryComboViewer.setInput(recipeBook);
        listViewer.setInput(recipeBook);
        listViewer.refresh();
        categoryComboViewer.refresh();
        categoryComboViewer.getCombo().select(0);
        cuisineComboViewer.refresh();
        cuisineComboViewer.getCombo().select(0);
    }

    /**
     * An action that represents a numbered handheld.
     */
    private class SearchCheckAction extends Action {
        private SearchFieldTypes searchField;

        /**
         * Creates a new instance of this class with the specified text, and number.
         * 
         * @param text the text for this action to display.
         * @param searchField the search field associated with this action.
         */
        public SearchCheckAction(String text, SearchFieldTypes searchField) {
            super(text, SWT.CHECK);
            this.searchField = searchField;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            uncheckSearchActions();
            setChecked(true);
            activeSearchField = searchField;
            doSearch();
        }
    }
}
