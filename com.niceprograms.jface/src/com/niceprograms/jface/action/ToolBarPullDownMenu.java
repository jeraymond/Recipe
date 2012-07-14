package com.niceprograms.jface.action;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A pull down menu for a tool bar.
 */
public class ToolBarPullDownMenu extends ContributionItem {

	private MenuManager menuManager;

	private ToolItem item;

	private String text;

	private ImageDescriptor imageDescriptor;

	/**
	 * Creates a new instance of PullDownMenu.
	 */
	public ToolBarPullDownMenu() {
		menuManager = new MenuManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unused")
	public void fill(final ToolBar parent, int index) {
		final Menu menu = menuManager.createContextMenu(parent);

		item = new ToolItem(parent, SWT.PUSH);
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = parent.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}
		});
		if (text != null) {
			item.setText(text);
		}
		if (imageDescriptor != null) {
			item.setImage(imageDescriptor.createImage());
		} else {
			item.setImage(null);
		}
	}

	/**
	 * Gets this pull down menu's menu manager.
	 * 
	 * @return the pull down menu's menu manager.
	 */
	public IMenuManager getMenuManager() {
		return menuManager;
	}

	/**
	 * Gets the image descriptor for this pull down menu.
	 * 
	 * @return the image descriptor for this pull down menu.
	 */
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	/**
	 * Sets the image descriptor for this pull down menu.
	 * 
	 * @param imageDescriptor the image descriptor for this pull down menu.
	 */
	public void setImageDescriptor(ImageDescriptor imageDescriptor) {
		this.imageDescriptor = imageDescriptor;
	}

	/**
	 * Gets the text for this pull down menu.
	 * 
	 * @return the text for this pull down menu.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text for this pull down menu.
	 * 
	 * @param text the text for this pull down menu.
	 */
	public void setText(String text) {
		this.text = text;
	}
}
