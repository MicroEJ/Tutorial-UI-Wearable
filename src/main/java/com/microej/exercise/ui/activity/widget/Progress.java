/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.activity.widget;

import ej.microui.MicroUI;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.animation.Animator;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * A widget that shows progress.
 *
 * <p>
 * The widget shows a value and the progress towards a specified goal.
 */
public class Progress extends Widget implements MotionAnimationListener {

	/** The extra style field value for defining the accent color of the progress bar. */
	public static final int EXTRA_FIELD_BAR_ACCENT_COLOR = 0;

	/** The extra style field value for defining the secondary color of the progress bar. */
	public static final int EXTRA_FIELD_BAR_SECONDARY_COLOR = 1;

	/** The extra style field value for defining the thickness of the progress bar. */
	public static final int EXTRA_FIELD_BAR_THICKNESS = 2;

	private static final int ANIMATION_DURATION = 1000;
	private static final int DEFAULT_THICKNESS = 5;
	private static final int MAX_PERCENT = 100;
	private static final int MAX_GOAL = 99999;

	private int value;

	private final int goal;

	private final boolean showValue;

	private final Animator animator;

	private MotionAnimation animation;

	/**
	 * Creates the progress widget, given the specified initial value and goal.
	 *
	 * <p>
	 * The given {@link Animator} is used for the animations of this widget.
	 *
	 * @param initialValue
	 *            the initial value to use.
	 * @param goal
	 *            the goal value.
	 * @param animator
	 *            the animator to use for animations.
	 */
	public Progress(int initialValue, int goal, Animator animator) {
		super(true);

		if (initialValue < 0 || goal <= 0) {
			throw new IllegalArgumentException();
		}

		this.value = Math.min(initialValue, MAX_GOAL);
		this.goal = Math.min(goal, MAX_GOAL);
		this.showValue = true;
		this.animator = animator;
	}

	/**
	 * Sets the value to display.
	 *
	 * @param newValue
	 *            the new value.
	 */
	public void setValue(int newValue) {
		if (newValue < 0) {
			throw new IllegalArgumentException();
		}
		this.value = Math.min(newValue, MAX_GOAL);
	}

	@Override
	public void tick(int value, boolean finished) {
		// does nothing for now
	}

	private void startAnimation(final MotionAnimation animation) {
		MicroUI.callSerially(new Runnable() {

			@Override
			public void run() {
				stopAnimation();
				Progress.this.animation = animation;
				animation.start();
			}
		});
	}

	private void stopAnimation() {
		if (this.animation != null) {
			this.animation.stop();
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		Style style = getStyle();

		Font font = style.getFont();
		int thickness = style.getExtraInt(EXTRA_FIELD_BAR_THICKNESS, DEFAULT_THICKNESS);

		// sets the optimal width to be the text width
		int width = font.stringWidth(String.valueOf(this.value));

		// sets the optimal height to be the sum of the font height and the progress bar thickness
		int height = font.getHeight() + thickness;

		size.setSize(width, height);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		// retrieves the style for this widget
		Style style = getStyle();
		Font font = style.getFont();

		// draws the text
		int textColor = style.getColor();
		// sets the color to use for the text
		g.setColor(textColor);
		String text = getText();
		int textWidth = font.stringWidth(text);
		int textHeight = font.getHeight();
		int textX = Alignment.computeLeftX(textWidth, 0, contentWidth, style.getHorizontalAlignment());
		int textY = Alignment.computeTopY(textHeight, 0, contentHeight, style.getVerticalAlignment());
		Painter.drawString(g, text, font, textX, textY);

		int accentColor = style.getExtraInt(EXTRA_FIELD_BAR_ACCENT_COLOR, textColor);
		int secondaryColor = style.getExtraInt(EXTRA_FIELD_BAR_SECONDARY_COLOR, 0xcccccc);
		int barThickness = style.getExtraInt(EXTRA_FIELD_BAR_THICKNESS, DEFAULT_THICKNESS);
		float progress = getProgress();
		int progressWidth = (int) (progress * contentWidth);
		int barY = textY + textHeight;

		g.setColor(accentColor);
		Painter.fillRectangle(g, 0, barY, progressWidth, barThickness);

		g.setColor(secondaryColor);
		Painter.fillRectangle(g, progressWidth, barY, contentWidth - progressWidth, barThickness);
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		// stops the animation when the widget is hidden
		stopAnimation();
	}

	/**
	 * Gets the text to display.
	 *
	 * @return the text to display.
	 */
	private String getText() {
		if (this.showValue) {
			return String.valueOf(this.value);
		} else {
			return getProgressInPercent() + " %"; //$NON-NLS-1$
		}
	}

	/**
	 * Gets the progress towards the goal.
	 *
	 * @return the progress value, between 0f and 1f.
	 */
	private float getProgress() {
		return (float) this.value / this.goal;
	}

	/**
	 * Gets the progress towards the goal, in percent.
	 *
	 * @return the progress value, in percent, between 0 and 100.
	 */
	private int getProgressInPercent() {
		return (int) (getProgress() * MAX_PERCENT);
	}

}
