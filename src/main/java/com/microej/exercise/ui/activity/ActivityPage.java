/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.activity;

import com.microej.exercise.ui.Main;
import com.microej.exercise.ui.activity.widget.Progress;
import com.microej.exercise.ui.style.ClassIdentifiers;
import com.microej.exercise.ui.style.Fonts;
import com.microej.exercise.ui.util.Model;
import com.microej.exercise.ui.util.Page;
import ej.microui.display.Colors;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.widget.basic.Label;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.SimpleDock;

/**
 * A page that represents the Activity application.
 */
public class ActivityPage extends Page {

	private static final int BONDI_DARKER = 0x253d43;
	private static final int BONDI = 0x008eaa;

	private Progress activityProgress;

	@Override
	public Widget getWidget() {
		SimpleDock dock = new SimpleDock(LayoutOrientation.VERTICAL);
		dock.addClassSelector(ClassIdentifiers.ACTIVITY);

		// adds a label that represents the title of the page at the top
		Label title = new Label("Steps"); //$NON-NLS-1$
		title.addClassSelector(ClassIdentifiers.APPLICATION_TITLE);
		dock.setFirstChild(title);

		// adds a widget that shows the activity progress (step count and goal completion) at the center
		Model model = Model.getInstance();
		this.activityProgress = new Progress(model.getStepCount(), model.getStepGoal(), Main.getAnimator());
		dock.setCenterChild(this.activityProgress);

		return dock;
	}

	@Override
	public void populateStylesheet(CascadingStylesheet stylesheet) {
		// defines the style of the top container of the activity application
		EditableStyle style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.ACTIVITY));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// defines the style of the activity application title
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.APPLICATION_TITLE));
		style.setMargin(new FlexibleOutline(20, 0, 0, 0));

		// defines the style of the activity progress widget
		style = stylesheet.getSelectorStyle(new TypeSelector(Progress.class));
		style.setFont(Fonts.getLargeFont());
		style.setExtraInt(Progress.EXTRA_FIELD_BAR_ACCENT_COLOR, BONDI);
		style.setExtraInt(Progress.EXTRA_FIELD_BAR_SECONDARY_COLOR, BONDI_DARKER);
		style.setExtraInt(Progress.EXTRA_FIELD_BAR_THICKNESS, 25);
	}

	@Override
	public void update() {
		final int stepCount = Model.getInstance().getStepCount();
		ActivityPage.this.activityProgress.setValue(stepCount);
		ActivityPage.this.activityProgress.requestRender();
	}

}
