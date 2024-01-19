/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.applicationmenu;

import com.microej.exercise.ui.Main;
import com.microej.exercise.ui.activity.ActivityPage;
import com.microej.exercise.ui.applicationmenu.widget.ApplicationList;
import com.microej.exercise.ui.applicationmenu.widget.ApplicationListItem;
import com.microej.exercise.ui.stubapplication.StubApplicationPage;
import com.microej.exercise.ui.util.Model;
import com.microej.exercise.ui.util.Page;
import ej.bon.Immutables;
import ej.microui.display.Colors;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.basic.OnClickListener;

/**
 * A page that represents the application menu, where the user can launch applications.
 */
public class ApplicationMenuPage extends Page {

	/** An array containing the names of the watch applications, loaded from file <code>application_names.xml</code>. */
	private static final String[] APPLICATIONS_NAMES = (String[]) Immutables.get("applicationNames"); //$NON-NLS-1$

	/**
	 * An array containing the path to the icons for the watch applications, loaded from file
	 * <code>application_icons.xml</code>.
	 */
	private static final String[] APPLICATIONS_ICONS = (String[]) Immutables.get("applicationIcons"); //$NON-NLS-1$

	@Override
	public Widget getWidget() {
		// retrieves the business model
		final Model model = Model.getInstance();

		// creates the widget that represents the list of applications
		ApplicationList list = new ApplicationList(model.getSelectedApplicationIndex()) {
			@Override
			protected void onHidden() {
				super.onHidden();
				// save the last selected application index when quitting the list
				model.setSelectedApplicationIndex(getSelectedIndex());
			}
		};

		// adds the activity page as the first one in the list
		addActivityApplication(list);

		// populates the rest of the list with stub applications
		int applicationsCount = APPLICATIONS_NAMES.length;
		for (int i = 1; i < applicationsCount; i++) {
			final String iconPath = APPLICATIONS_ICONS[i];
			final String name = APPLICATIONS_NAMES[i];
			ApplicationListItem applicationListItem = new ApplicationListItem(iconPath, name, new OnClickListener() {

				@Override
				public void onClick() {
					// shows a stub application page when the user clicks on the item
					Main.showPage(new StubApplicationPage(iconPath, name));
				}
			});
			list.addListItem(applicationListItem);
		}

		return list;
	}

	private void addActivityApplication(ApplicationList list) {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick() {
				// shows the activity page when the user clicks on the first item
				Main.showPage(new ActivityPage());
			}
		};
		ApplicationListItem activityItem = new ApplicationListItem(APPLICATIONS_ICONS[0], APPLICATIONS_NAMES[0],
				listener);
		list.addListItem(activityItem);
	}

	@Override
	public void populateStylesheet(CascadingStylesheet stylesheet) {
		// defines the style of the application list
		EditableStyle style = stylesheet.getSelectorStyle(new TypeSelector(ApplicationList.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// defines the style of the items of the application list
		style = stylesheet.getSelectorStyle(new TypeSelector(ApplicationListItem.class));
		style.setColor(0xbbbbbb);
		style.setPadding(new FlexibleOutline(0, 0, 0, 30));
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setHorizontalAlignment(Alignment.LEFT);
		style.setVerticalAlignment(Alignment.VCENTER);
		style.setExtraFloat(ApplicationListItem.SPLIT_RATIO_STYLE, 0.425f);
		style.setExtraInt(ApplicationListItem.SPACING_STYLE, 20);
	}

}
