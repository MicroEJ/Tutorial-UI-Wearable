/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface.widget;

import com.microej.exercise.ui.style.Images;
import ej.bon.XMath;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Image;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * A widget that displays the battery level.
 *
 * <p>
 * It uses an image of an empty battery and draws the current level with a
 * {@link Painter#fillRectangle(GraphicsContext, int, int, int, int)}.
 */
public class BatteryLevel extends Widget {

	/** The relative x-position of the bar within the battery image (between 0 and 1). **/
	private static final float BAR_X_ANCHOR = 6f / 32;

	/** The relative y-position of the bar within the battery image (between 0 and 1). **/
	private static final float BAR_Y_ANCHOR = 11f / 32;

	/** The width of the bar, relative to the battery image (between 0 and 1). **/
	private static final float BAR_MAX_WIDTH = 18f / 32;

	/** The height of the bar, relative to the battery image (between 0 and 1). **/
	private static final float BAR_MAX_HEIGHT = 9f / 32;

	/** The maximum battery value. **/
	private static final int MAX_VALUE = 100;

	/** The minimum battery value. **/
	private static final int MIN_VALUE = 0;

	private int value;

	private final Image image;

	/**
	 * Creates the battery indicator widget, given a level value.
	 *
	 * @param value
	 *            the battery level.
	 */
	public BatteryLevel(int value) {
		this.value = XMath.limit(value, MIN_VALUE, MAX_VALUE);
		this.image = Image.getImage(Images.BATTERY_ICON);
	}

	/**
	 * Sets the battery level.
	 *
	 * @param value
	 *            the battery level to set.
	 */
	public void setLevel(int value) {
		this.value = XMath.limit(value, MIN_VALUE, MAX_VALUE);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		// retrieves the style for this widget
		Style style = getStyle();
		g.setColor(style.getColor());

		int imageWidth = this.image.getWidth();
		int imageHeight = this.image.getHeight();

		// computes the position of the image anchor point
		int x = Alignment.computeLeftX(imageWidth, 0, contentWidth, style.getHorizontalAlignment());
		int y = Alignment.computeTopY(imageHeight, 0, contentHeight, style.getVerticalAlignment());
		Painter.drawImage(g, this.image, x, y);

		// computes the position and size of the battery bar
		x += BAR_X_ANCHOR * imageWidth;
		y += BAR_Y_ANCHOR * imageHeight;
		int barWidth = (int) (BAR_MAX_WIDTH * imageWidth * this.value / MAX_VALUE);
		int barHeight = (int) (BAR_MAX_HEIGHT * imageHeight);
		Painter.fillRectangle(g, x, y, barWidth, barHeight);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// sets the optimal content size as the size of the battery image
		size.setSize(this.image.getWidth(), this.image.getWidth());
	}

}
