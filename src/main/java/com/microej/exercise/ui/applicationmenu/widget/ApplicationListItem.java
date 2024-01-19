/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.applicationmenu.widget;

import ej.annotation.Nullable;
import ej.microui.display.*;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.basic.OnClickListener;

/**
 * Represents an item of the watch application list.
 */
public class ApplicationListItem extends Widget {

	/** The extra style field ID for the split factor . */
	public static final int SPLIT_RATIO_STYLE = 0;

	/** The extra style field ID for the spacing. */
	public static final int SPACING_STYLE = 1;

	private static final float DEFAULT_SPLIT_FACTOR = 0.5f;
	private static final int IMAGE_TEXT_SPACING = 10;

	private final String text;

	@Nullable
	private BufferedImage textImage;

	private final Image iconImage;

	private float percent;

	private final OnClickListener onClickListener;

	/**
	 * Creates a {@link ApplicationListItem} with the given icon and text.
	 *
	 * @param iconPath
	 *            the path to the icon resource.
	 * @param name
	 *            the name of the application.
	 * @param listener
	 *            the listener to notify when the user clicks on the item.
	 */
	public ApplicationListItem(final String iconPath, final String name, OnClickListener listener) {
		super(true);
		this.iconImage = Image.getImage(iconPath);
		this.text = name;
		this.onClickListener = listener;
	}

	/**
	 * Sets the percent of the position of the item.
	 *
	 * @param percent
	 *            the percent to set.
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();
		Font font = style.getFont();
		int optimalWidth = this.iconImage.getWidth() + IMAGE_TEXT_SPACING + font.stringWidth(this.text);
		int optimalHeight = this.iconImage.getHeight() + style.getExtraInt(SPACING_STYLE, 0);
		size.setSize(optimalWidth, optimalHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		float percent = this.percent;
		Style style = getStyle();

		float splitFactor = style.getExtraFloat(SPLIT_RATIO_STYLE, DEFAULT_SPLIT_FACTOR);
		int imageAreaWidth = (int) (contentWidth * splitFactor);

		// draw the icon
		Image image = this.iconImage;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		int imageX = (int) ((1 - percent) * (imageAreaWidth - imageWidth) + (imageWidth - (imageWidth * percent)));
		int verticalAlignment = style.getVerticalAlignment();
		int imageY = Alignment.computeTopY((int) (imageHeight * percent), 0, contentHeight, verticalAlignment);
		Painter.drawImage(g, image, imageX, imageY);

		// draw the name
		g.setColor(style.getColor());
		Font font = style.getFont();
		int textX = imageX + imageWidth + IMAGE_TEXT_SPACING;
		int textY = Alignment.computeTopY(font.getHeight(), 0, contentHeight, verticalAlignment);
		Painter.drawImage(g, this.textImage, textX, textY);
	}

	@Override
	public boolean handleEvent(int event) {
		int type = Event.getType(event);
		if (type == Pointer.EVENT_TYPE) {
			int action = Buttons.getAction(event);
			if (action == Buttons.RELEASED) {
				// the item has been clicked, notify the click listener
				this.onClickListener.onClick();
				return true;
			}
		}

		return super.handleEvent(event);
	}

	@Override
	protected void onLaidOut() {
		super.onLaidOut();
		if (this.textImage == null) {
			// create an image and draw the text into it
			createTextImage();
		}
	}

	/**
	 * Creates a {@link BufferedImage} that represents the text to draw (i.e., the application name).
	 *
	 * <p>
	 * Drawing an image of the text rather than the text itself is more efficient in this case because drawing a text
	 * takes longer than drawing an image.
	 *
	 * <p>
	 * The image is created once when the widget is first laid out. The image is closed and released when the widget is
	 * detached from the widget hierarchy, to prevent the image to stay in memory.
	 */
	private void createTextImage() {
		Style style = getStyle();
		Font font = style.getFont();
		int width = font.stringWidth(this.text);
		int height = font.getHeight();
		BufferedImage image = new BufferedImage(width, height);
		GraphicsContext graphicsContext = image.getGraphicsContext();
		graphicsContext.setColor(Colors.BLACK);
		Painter.fillRectangle(graphicsContext, 0, 0, width, height);
		graphicsContext.setColor(style.getColor());
		Painter.drawString(graphicsContext, this.text, font, 0, 0);
		this.textImage = image;
	}

	@Override
	protected void onDetached() {
		super.onDetached();
		BufferedImage image = this.textImage;
		if (image != null) {
			// the widget has been detached from the widget hierarchy, release the image in memory.
			image.close();
			this.textImage = null;
		}
	}

}
