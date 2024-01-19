/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.applicationmenu.widget;

import com.microej.exercise.ui.Main;
import ej.annotation.Nullable;
import ej.bon.XMath;
import ej.drawing.ShapePainter;
import ej.drawing.ShapePainter.Cap;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Command;
import ej.microui.event.generator.Pointer;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.util.Size;
import ej.widget.swipe.SwipeEventHandler;
import ej.widget.swipe.Swipeable;

/**
 * A container that contains a list of widgets that can be scrolled vertically.
 */
public class ApplicationList extends Container implements Swipeable {

	private static final int POSITION_OVERFLOW_MULTIPLIER = 4;
	private static final float MAGNIFY_RATIO = 1.8f;
	private static final float MINIMAL_RATIO = 0.1f;

	private static final int ARC_SPACING = 20;
	private static final int ARC_THICKNESS = 6;
	private static final int ARC_FADE = 1;
	private static final Cap ARC_CAPS = Cap.ROUNDED;
	private static final float ARC_BACKGROUND_ANGLE = 30.0f;
	private static final float ARC_CURSOR_ANGLE = 5.0f;
	private static final int ARC_BACKGROUND_COLOR = 0x777777;
	private static final int ARC_CURSOR_COLOR = Colors.WHITE;

	@Nullable
	private SwipeEventHandler swipeEventHandler;

	private int selectedIndex;
	private int widgetHeight;

	private int position;

	/**
	 * Creates a list with the item at given index as the selected item.
	 *
	 * @param selectedIndex
	 *            the index of the selected item (0-based) when opening the carousel.
	 */
	public ApplicationList(int selectedIndex) {
		super(true);
		this.selectedIndex = selectedIndex;
	}

	/**
	 * Adds a list item.
	 *
	 * @param item
	 *            the list item to add.
	 * @see #addChild(Widget)
	 */
	public void addListItem(ApplicationListItem item) {
		super.addChild(item);
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		SwipeEventHandler eventHandler = this.swipeEventHandler;
		if (eventHandler != null) {
			eventHandler.stop();
		}
	}

	/**
	 * Gets the currently selected index.
	 *
	 * @return the selected index in the list.
	 */
	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int boundsWidth = size.getWidth();
		int boundsHeight = size.getHeight();

		boolean computeWidth = boundsWidth == Widget.NO_CONSTRAINT;
		boolean computeHeight = boundsHeight == Widget.NO_CONSTRAINT;

		int width = boundsWidth;
		int height = 0;

		// Each child takes the full height and the width it needs when horizontal, the opposite when vertical.
		for (Widget widget : getChildren()) {
			computeChildOptimalSize(widget, width, Widget.NO_CONSTRAINT);
			width = Math.max(width, widget.getWidth());
			height = Math.max(height, widget.getHeight());
		}
		this.widgetHeight = height;
		height *= getChildrenCount();

		if (computeWidth) {
			size.setWidth(width);
		}
		if (computeHeight) {
			size.setHeight(height);
		}

	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		int widgetsCount = getChildrenCount();
		this.selectedIndex = Math.min(this.selectedIndex, widgetsCount - 1);

		if (widgetsCount > 0) {
			int height = this.widgetHeight;
			onMoveInternal(height * this.selectedIndex);

			this.swipeEventHandler = new SwipeEventHandler(this, widgetsCount, height, false, true, false, this,
					getDesktop().getAnimator());
			if (this.selectedIndex != -1) {
				goTo(this.selectedIndex);
			}
		} else {
			this.swipeEventHandler = null;
		}
	}

	@Override
	public boolean handleEvent(int event) {
		SwipeEventHandler eventHandler = this.swipeEventHandler;
		if (eventHandler != null && eventHandler.handleEvent(event)) {
			return true;
		}
		if (Event.getType(event) == Pointer.EVENT_TYPE && Pointer.isDragged(event)) {
			return true;
		}
		if (Event.getType(event) == Command.EVENT_TYPE) {
			// the physical button has been pressed, show the watchface
			Main.showWatchface();
			return true;
		}
		return super.handleEvent(event);
	}

	/**
	 * Selects a widget from its index.
	 *
	 * @param index
	 *            the widget index.
	 * @throws IllegalArgumentException
	 *             if the given index is not valid (between <code>0</code> and the number of items in the carousel).
	 */
	private void goTo(int index) {
		goToInternal(index, 0);
	}

	private void goToInternal(int index, long duration) {
		int widgetsCount = getChildrenCount();
		if (index < 0 || index >= widgetsCount) {
			throw new IllegalArgumentException();
		}

		SwipeEventHandler eventHandler = this.swipeEventHandler;
		if (eventHandler == null) {
			this.selectedIndex = index;
			return;
		}

		int size = 0;
		int currentIndex = 0;
		Widget[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			if (currentIndex == index) {
				if (duration == 0) {
					eventHandler.moveTo(size);
				} else {
					eventHandler.moveTo(size, duration);
				}
				break;
			}
			currentIndex++;

			size += this.widgetHeight;
		}
	}

	@Override
	public synchronized void onMove(final int position) {
		onMoveInternal(position);
		requestRender();
	}

	private void onMoveInternal(int position) {
		int widgetsCount = getChildrenCount();
		int widgetHeight = this.widgetHeight;
		int totalHeight = widgetsCount * widgetHeight;
		position = computePosition(position, totalHeight);

		int width = getContentWidth();
		int height = getContentHeight();
		int center = Math.min(width, height) / 2;
		int widgetHalfHeight = widgetHeight / 2;

		int selectedIndex = Math.round((float) position / widgetHeight);
		selectedIndex = XMath.limit(selectedIndex, 0, widgetsCount - 1);
		int remaining = (selectedIndex * widgetHeight) - position;
		int selectedY = center - widgetHalfHeight + remaining;
		Widget[] children = getChildren();

		int y = selectedY;
		for (int i = selectedIndex; i < widgetsCount; i++) {
			ApplicationListItem widget = (ApplicationListItem) children[i];
			int distanceToCenter = Math.abs((y + widgetHalfHeight) - center);
			float percent = getPercent(distanceToCenter, height);
			widget.setPercent(percent);
			int x = (int) ((1f - percent) * width * (1f - percent));
			layOutChild(widget, x, y, width, widgetHeight);
			y += widgetHeight;
		}
		y = selectedY;
		for (int i = selectedIndex - 1; i >= 0; i--) {
			ApplicationListItem widget = (ApplicationListItem) children[i];
			int distanceToCenter = Math.abs((y - widgetHalfHeight) - center);
			float percent = getPercent(distanceToCenter, height);
			widget.setPercent(percent);
			int x = (int) ((1f - percent) * width * (1f - percent));
			y -= widgetHeight;
			layOutChild(widget, x, y, width, widgetHeight);
		}
		this.selectedIndex = selectedIndex;
		this.position = position;
	}

	private int computePosition(int position, int totalHeight) {
		if (position < 0) {
			position /= POSITION_OVERFLOW_MULTIPLIER;
		} else if (position > totalHeight) {
			int diff = position - totalHeight;
			position = totalHeight + diff / POSITION_OVERFLOW_MULTIPLIER;
		}
		return position;
	}

	private float getPercent(int distanceToCenter, int height) {
		float percent = (float) Math.cos(distanceToCenter * Math.PI / (MAGNIFY_RATIO * height));
		percent = XMath.limit(percent, 0, 1);
		if (percent < MINIMAL_RATIO) {
			percent = 0;
		}
		return percent;
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		drawScrollBar(g, contentWidth);

		// Draw the children of this container (the items)
		super.renderContent(g, contentWidth, contentHeight);
	}

	private void drawScrollBar(GraphicsContext g, int contentWidth) {
		int position = this.position;
		int widgetsCount = getChildrenCount();
		int height = this.widgetHeight;
		int totalHeight = (widgetsCount - 1) * height;

		position = computePosition(position, totalHeight);

		// draw the bar background
		g.setColor(ARC_BACKGROUND_COLOR);
		g.setBackgroundColor(Colors.BLACK);
		ShapePainter.drawThickFadedCircleArc(g, 0, ARC_SPACING, contentWidth - 2 * ARC_SPACING, -ARC_BACKGROUND_ANGLE,
				2 * ARC_BACKGROUND_ANGLE, ARC_THICKNESS, ARC_FADE, ARC_CAPS, ARC_CAPS);

		// draw the bar cursor
		g.setColor(ARC_CURSOR_COLOR);
		g.setBackgroundColor(ARC_BACKGROUND_COLOR);
		float positionAngle = (2 * ARC_BACKGROUND_ANGLE - ARC_CURSOR_ANGLE) * position / totalHeight
				- (ARC_BACKGROUND_ANGLE - ARC_CURSOR_ANGLE);
		ShapePainter.drawThickFadedCircleArc(g, 0, ARC_SPACING, contentWidth - 2 * ARC_SPACING, -positionAngle,
				ARC_CURSOR_ANGLE, ARC_THICKNESS, ARC_FADE, ARC_CAPS, ARC_CAPS);
		g.removeBackgroundColor();
	}
}
