package com.niceprograms.recipe.ui;

import java.util.ArrayList;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog for getting categories.
 */
public class CategoriesDialog extends Dialog {

	private TreeSet<String> categories;

	private List categoryList;

	private Label messageLabel;

	private String message;

	private Text addCategoryText;

	private Button addCategoryButton;

	private String title;

	private String[] selectedCategories;

	private String[] initialSelection;

	/**
	 * Creates a CategoriesDialog.
	 * 
	 * @param parent the parent shell or <code>null</code>.
	 * @param categories the list of initial categories to display in the
	 * category list.
	 * @param initialSelection the initial selection, or null for no selection.
	 * @param title the dialog's title.
	 */
	public CategoriesDialog(Shell parent, String[] categories,
			String[] initialSelection, String title) {
		super(parent);
		this.categories = new TreeSet<String>();
		this.title = title;
		this.initialSelection = initialSelection;
		if (categories != null) {
			for (String category : categories) {
				this.categories.add(category);
			}
		}
		if (this.initialSelection != null) {
			for (String category : initialSelection) {
				this.categories.add(category);
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Composite content = new Composite(composite, SWT.NONE);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = SWT.NONE;
		layout.marginHeight = SWT.NONE;
		layout.verticalSpacing = UIConstants.VERTICAL_SPACING;
		layout.marginLeft = SWT.NONE;
		layout.marginRight = SWT.NONE;
		layout.marginTop = SWT.NONE;
		layout.marginBottom = SWT.NONE;
		content.setLayout(layout);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		messageLabel = new Label(content, SWT.WRAP);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.horizontalSpan = 3;
		messageLabel.setText(message);
		messageLabel.setLayoutData(layoutData);

		categoryList = new List(content, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		categoryList.setLayoutData(new GridData(GridData.FILL_BOTH));
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 3;
		categoryList.setLayoutData(layoutData);

		Label addCategoryLabel = new Label(content, SWT.NONE);
		addCategoryLabel.setText("New Category");
		addCategoryLabel.setLayoutData(new GridData());
		
		addCategoryText = new Text(content, SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		addCategoryText.setLayoutData(layoutData);

		addCategoryButton = new Button(content, SWT.PUSH);
		layoutData = new GridData();
		addCategoryButton.setLayoutData(layoutData);
		addCategoryButton.setText("Add");
		addCategoryButton.setToolTipText("Adds a new category to the list.");
		addCategoryButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unused")
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (addCategory(addCategoryText.getText())) {
					rebuildCategoryList();
					setSelection();
				}
			}
		});

		rebuildCategoryList();
		setSelection();
		return composite;
	}

	private void setSelection() {
		if (initialSelection != null && categoryList != null
				&& !categoryList.isDisposed()) {
			int numCategories = categoryList.getItemCount();
			ArrayList<Integer> selection = new ArrayList<Integer>();
			for (int i = 0; i < numCategories; i++) {
				int numInitialSelection = initialSelection.length;
				for (int j = 0; j < numInitialSelection; j++) {

					if (categoryList.getItem(i).equals(initialSelection[j])) {
						selection.add(i);
					}
				}
			}
			int numItems = selection.size();
			int[] sel = new int[numItems];
			for (int i = 0; i < numItems; i++) {
				sel[i] = selection.get(i);
			}
			categoryList.select(sel);
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 300);
	}

	/**
	 * Sets the instructional message displayed to the user.
	 * 
	 * @param message the message.
	 */
	public void setMessage(String message) {
		this.message = message;
		if (messageLabel != null && !messageLabel.isDisposed()) {
			messageLabel.setText(this.message);
		}
	}

	/**
	 * Gets the selected categories.
	 * 
	 * @return the selected categories.
	 */
	public String[] getCategories() {
		return selectedCategories;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (title != null) {
			newShell.setText(title);
		}
	}

	private boolean addCategory(String category) {
		boolean added = false;
		if (category != null && categoryList != null
				&& !categoryList.isDisposed()) {
			boolean alreadyExists = false;
			String newCategory = category.trim();
			if (!"".equals(newCategory)) {
				for (String existingCategory : categories) {
					if (newCategory.toUpperCase().equals(
							existingCategory.trim().toUpperCase())) {
						alreadyExists = true;
						break;
					}
				}

				if (!alreadyExists) {
					added = categories.add(newCategory);
					if (initialSelection != null) {
						String[] oldSelection = initialSelection;
						initialSelection = new String[initialSelection.length + 1];
						System.arraycopy(oldSelection, 0, initialSelection, 0,
								oldSelection.length);
						initialSelection[initialSelection.length - 1] = newCategory;
					} else {
						initialSelection = new String[1];
						initialSelection[0] = newCategory;
					}
				}
			}
		}
		return added;
	}

	@Override
	protected void okPressed() {
		if (categoryList != null && !categoryList.isDisposed()) {
			selectedCategories = categoryList.getSelection();
		}
		super.okPressed();
	}

	private void rebuildCategoryList() {
		if (categoryList != null && !categoryList.isDisposed()) {
			categoryList.removeAll();
			for (String category : categories) {
				categoryList.add(category);
			}
		}
	}
}
