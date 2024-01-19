/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface;

import com.microej.exercise.ui.style.ClassIdentifiers;
import com.microej.exercise.ui.style.Fonts;
import com.microej.exercise.ui.style.Images;
import com.microej.exercise.ui.util.Model;
import com.microej.exercise.ui.util.Page;
import com.microej.exercise.ui.util.TimeHelper;
import com.microej.exercise.ui.watchface.widget.BatteryLevel;
import com.microej.exercise.ui.watchface.widget.DigitalClock;
import com.microej.exercise.ui.watchface.widget.IconLabel;
import com.microej.exercise.ui.watchface.widget.WatchHands;
import ej.microui.display.Colors;
import ej.microui.display.Font;
import ej.microui.display.Image;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.ImageBackground;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.widget.container.Canvas;

/**
 * A page that represents a watchface.
 */
public class WatchfacePage extends Page {

	private static final float TEN = 10;

	private IconLabel heartRate;

	private IconLabel steps;

	private IconLabel distance;

	private BatteryLevel battery;

	@Override
	public Widget getWidget() {
		Widget digital = createDigital();
		digital.addClassSelector(ClassIdentifiers.DIGITAL_WATCHFACE);

		return digital;
	}

	/**
	 * Creates the widget that represents the digital watchface.
	 *
	 * <p>
	 * This digital watchface is composed of 5 widgets:
	 * <ul>
	 * <li>the current heart rate value,</li>
	 * <li>the daily step count,</li>
	 * <li>the daily distance,</li>
	 * <li>a digital clock,</li>
	 * <li>a battery level indicator.</li>
	 * </ul>
	 */
	private Widget createDigital() {
		Model model = Model.getInstance();

		// creates the widget for the heart rate
		this.heartRate = new IconLabel(Images.HEART_ICON, String.valueOf(model.getHeartRate()));
		this.heartRate.addClassSelector(ClassIdentifiers.HEART_RATE_VALUE);

		// creates the widget for the step count
		this.steps = new IconLabel(Images.SHOE_ICON, String.valueOf(model.getStepCount()));
		this.steps.addClassSelector(ClassIdentifiers.STEP_VALUE);

		// creates the widget for the distance
		this.distance = new IconLabel(Images.LOCALIZATION_ICON, formatDistance(model.getDistance()));
		this.distance.addClassSelector(ClassIdentifiers.DISTANCE_VALUE);

		// creates the widget for the digital clock
		DigitalClock clock = new DigitalClock(TimeHelper.getTimer());

		// creates the widget for the battery level indicator
		this.battery = new BatteryLevel(model.getBatteryLevel());

		Canvas canvas = new Canvas();
		canvas.addChild(this.heartRate, 110, 5, 170, 54);
		canvas.addChild(clock, 110, 70, 185, 65);
		canvas.addChild(this.steps, 53, 200, 125, 55);
		canvas.addChild(this.distance, 215, 200, 125, 55);
		canvas.addChild(this.battery, 183, 352, 32, 32);

		return canvas;
	}

	/**
	 * Creates the widget that represents the analog watchface.
	 *
	 * <p>
	 * This analog watchface is composed of one widget that draws the watch hands.
	 */
	private Widget createAnalog() {
		WatchHands watchHands = new WatchHands(TimeHelper.getTimer());
		watchHands.addClassSelector(ClassIdentifiers.ANALOG_WATCHFACE);
		return watchHands;
	}

	@Override
	public void populateStylesheet(CascadingStylesheet stylesheet) {
		// defines the style of the root container of the digital watchface
		EditableStyle style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DIGITAL_WATCHFACE));
		style.setBackground(new ImageBackground(Image.getImage("/step3/background.png")));

		// defines the style of the heart rate value
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.HEART_RATE_VALUE));
		style.setColor(Colors.BLACK);
		Font mediumFont = Fonts.getMediumFont();
		style.setFont(mediumFont);
		// sets the color to use for the icon with a custom extra field
		style.setExtraInt(IconLabel.EXTRA_FIELD_ICON_COLOR, 0xff3131);

		// defines the style of the step value
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.STEP_VALUE));
		style.setColor(Colors.BLACK);
		style.setFont(mediumFont);
		// sets the color to use for the icon with a custom extra field
		style.setExtraInt(IconLabel.EXTRA_FIELD_ICON_COLOR, Colors.BLACK);

		// defines the style of the distance value
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DISTANCE_VALUE));
		style.setColor(Colors.BLACK);
		style.setFont(mediumFont);
		// sets the color to use for the icon with a custom extra field
		style.setExtraInt(IconLabel.EXTRA_FIELD_ICON_COLOR, Colors.BLACK);

		// defines the style of the digital clock
		style = stylesheet.getSelectorStyle(new TypeSelector(DigitalClock.class));
		style.setColor(Colors.BLACK);
		style.setFont(Fonts.getLargeFont());
		// sets the font to use for the seconds with a custom extra field
		style.setExtraObject(DigitalClock.EXTRA_FIELD_SECONDS_FONT, Fonts.getSmallFont());

		// defines the style of the battery level indicator
		style = stylesheet.getSelectorStyle(new TypeSelector(BatteryLevel.class));
		style.setColor(Colors.WHITE);
	}

	/**
	 * Notifies that the business model changed and that the GUI might require an update of its content.
	 *
	 * <p>
	 * This method is called whenever the observed object (here <code>com.microej.exercise.ui.util.Model</code>) is
	 * changed.
	 */
	@Override
	public void update() {
		super.update();

		// retrieves the business model
		Model model = Model.getInstance();

		// updates the widgets
		this.heartRate.setText(String.valueOf(model.getHeartRate()));
		this.heartRate.requestRender();

		this.steps.setText(String.valueOf(model.getStepCount()));
		this.steps.requestRender();

		this.distance.setText(formatDistance(model.getDistance()));
		this.distance.requestRender();

		this.battery.setLevel(model.getBatteryLevel());
		this.battery.requestRender();
	}

	private static String formatDistance(float value) {
		int intPart = (int) value;
		int decimal = (int) ((value - intPart) * TEN);
		return new StringBuilder().append(intPart).append('.').append(decimal).append(" km").toString(); //$NON-NLS-1$
	}
}
