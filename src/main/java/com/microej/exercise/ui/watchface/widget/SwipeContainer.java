/*
 * Java
 *
 * Copyright 2019-2024  MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.exercise.ui.watchface.widget;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.Constants;
import ej.bon.Util;
import ej.microui.MicroUI;
import ej.microui.display.BufferedImage;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.motion.Motion;
import ej.motion.quad.QuadEaseOutFunction;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.event.DesktopEventGenerator;
import ej.mwt.event.PointerEventDispatcher;
import ej.mwt.util.Size;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * A swipe container holds several widget.
 * <p>
 * Each widget is the size of the content of the container.
 * <p>
 * Only one widget is visible at a time. It is possible to change the visible widget by swiping left or right.
 */
public class SwipeContainer extends Container {

	private static final int SCREEN_RATIO = 6;
	private static final float SPEED_THRESHOLD = 0.1f;
	private static final int TRANSITION_DURATION = 200;
	private static final String SCREENSHOTS = "com.microej.exercise.ui.swipecontainer.screenshots"; //$NON-NLS-1$
	private static final String COPY_DISPLAY = "com.microej.exercise.ui.swipecontainer.copydisplay"; //$NON-NLS-1$

	// Children indices.
	private static final int CURRENT = 0;
	private static final int OTHER = 1;

	@Nullable
	private MotionAnimation motionAnimation;

	// Pages management.
	private Widget[] pages;
	private int currentPageIndex;
	private int otherPageIndex;
	private int targetPageIndex;

	// Drag management.
	private boolean pressed;
	private int pressedX;
	private long pressedTime;
	private boolean moving;
	private int previousX;
	private int previousY;

	// Rendering management.
	private int previousPosition;
	private int position;

	// Transition snapshots.
	@Nullable
	private BufferedImage currentSnapshot;
	@Nullable
	private BufferedImage otherSnapshot;

	/**
	 * Creates a slide container.
	 */
	public SwipeContainer() {
		super(true);
		this.currentPageIndex = -1;
		this.pages = new Widget[0];
	}

	/**
	 * Adds a page.
	 *
	 * @param widget
	 *            the widget to add.
	 */
	@Override
	public void addChild(Widget widget) {
		this.pages = ArrayTools.add(this.pages, widget);
		if (this.currentPageIndex == -1) {
			super.addChild(widget);
			this.currentPageIndex = 0;
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int widthHint = size.getWidth();
		int heightHint = size.getHeight();

		int pageWidth;
		int pageHeight;
		if (getChildrenCount() > 0) {
			Widget pageChild = getChild(CURRENT);
			computeChildOptimalSize(pageChild, widthHint, heightHint);
			pageWidth = pageChild.getWidth();
			pageHeight = pageChild.getHeight();
		} else {
			pageWidth = 0;
			pageHeight = 0;
		}

		size.setSize(pageWidth, pageHeight);
	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		if (getChildrenCount() > 0) {
			Widget pageChild = getChild(CURRENT);
			layOutChild(pageChild, 0, 0, contentWidth, contentHeight);
		}
	}

	private void doAnimation(final Widget currentChild, final Widget otherChild, int startX, int endX,
			final int finalIndex, final boolean removeOther) {
		// Save initial position for rendering.
		this.position = startX;
		final int shift = otherChild.getX() - currentChild.getX();
		// Compute duration depending on the distance to walk.
		final int contentWidth = getContentWidth();
		long duration = TRANSITION_DURATION * Math.abs(endX - startX) / contentWidth;
		Motion motion = new Motion(QuadEaseOutFunction.INSTANCE, startX, endX, duration);
		this.targetPageIndex = finalIndex;
		this.motionAnimation = new MotionAnimation(getDesktop().getAnimator(), motion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				SwipeContainer.this.position = value;
				currentChild.setPosition(value, 0);
				otherChild.setPosition(value + shift, 0);
				requestRender();
				if (finished) {
					restore(removeOther);
				}
			}
		});
		this.motionAnimation.start();
	}

	private void interruptAnimation() {
		MotionAnimation animation = this.motionAnimation;
		if (animation != null) {
			animation.stop();
			this.targetPageIndex = this.currentPageIndex;
			restore(true);
		}
	}

	private void restore(boolean removeOther) {
		this.moving = false;
		this.motionAnimation = null;
		int childrenCount = getChildrenCount();
		if (childrenCount > 1) {
			this.currentPageIndex = this.targetPageIndex;

			Widget currentChild = getChild(CURRENT);
			Widget otherChild = getChild(OTHER);

			if (Constants.getBoolean(SCREENSHOTS)) {
				if (this.otherSnapshot != null) {
					this.otherSnapshot.close();
					this.otherSnapshot = null;
				}
				if (this.currentSnapshot != null) {
					this.currentSnapshot.close();
					this.currentSnapshot = null;
				}
			}

			// Restart any animation/refresh on the newly visible child.
			Widget newlyVisibleChild;
			if (removeOther) {
				removeChild(otherChild);
				newlyVisibleChild = currentChild;
			} else {
				removeChild(currentChild);
				newlyVisibleChild = otherChild;
			}
			newlyVisibleChild.setPosition(0, 0);
			setShownChild(newlyVisibleChild);
			requestRender();
		}
	}

	@Override
	public void render(GraphicsContext g) {
		if (Constants.getBoolean(COPY_DISPLAY)) {
			if (this.moving) {
				// The widget on top is moving.
				int contentX = getContentX();
				int contentY = getContentY();
				int contentWidth = getContentWidth();
				int contentHeight = getContentHeight();
				// "Move" the display from the previous position to the new one.
				int shift = this.position - this.previousPosition;
				g.setClip(contentX, contentY, contentWidth, contentHeight);
				Painter.drawDisplayRegion(g, contentX, contentY, contentWidth - shift, contentHeight, contentX + shift,
						contentY);
				// Draws only the modified part of the container and its children.
				if (shift > 0) {
					// The widgets are moved to the right.
					// Draw the part of the left widget previously outside of the container.
					g.setClip(contentX, contentY, shift, contentHeight);
				} else {
					// The widgets are moved to the left.
					// Draw the part of the right widget previously outside of the container.
					g.setClip(contentX + contentWidth + shift, contentY, -shift, contentHeight);
				}
				super.render(g);
				this.previousPosition = this.position;
			} else {
				super.render(g);
			}
		} else {
			super.render(g);
		}
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (Constants.getBoolean(SCREENSHOTS)) {
			if (this.moving) {
				BufferedImage current = this.currentSnapshot;
				if (current != null) {
					Widget currentChild = getChild(CURRENT);
					Painter.drawImage(g, current, currentChild.getX(), currentChild.getY());
				}
				BufferedImage other = this.otherSnapshot;
				if (other != null) {
					Widget otherChild = getChild(OTHER);
					Painter.drawImage(g, other, otherChild.getX(), otherChild.getY());
				}
			} else {
				super.renderContent(g, contentWidth, contentHeight);
			}
		} else {
			super.renderContent(g, contentWidth, contentHeight);
		}
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Pointer.EVENT_TYPE) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			int pointerX = pointer.getX();
			int pointerY = pointer.getY();
			int contentWidth = getContentWidth();
			int pagesCount = this.pages.length;
			switch (Buttons.getAction(event)) {
			case Buttons.PRESSED:
				onPointerPressed(pointerX, pointerY, pagesCount);
				break;
			case Pointer.DRAGGED:
				if (onPointerDragged(contentWidth, pagesCount, pointerX, pointerY)) {
					return true;
				}
				break;
			case Buttons.RELEASED:
				if (onPointerReleased(pointerX, contentWidth)) {
					return true;
				}
				break;
			default:
				break;
			}
		} else if (Event.getType(event) == DesktopEventGenerator.EVENT_TYPE) {
			int action = DesktopEventGenerator.getAction(event);
			if (action == PointerEventDispatcher.EXITED) {
				this.pressed = false;
			}
		}
		return super.handleEvent(event);
	}

	private void onPointerPressed(int pointerX, int pointerY, int pagesCount) {
		if (this.moving || pagesCount < 2) {
			this.pressed = false;
		} else {
			interruptAnimation();
			this.pressed = true;
			this.pressedX = pointerX;
			this.pressedTime = Util.platformTimeMillis();
			this.previousX = pointerX;
			this.previousY = pointerY;
			Widget currentChild = getChild(CURRENT);
			this.currentSnapshot = getSnapshot(currentChild);
		}
	}

	@Nullable
	private BufferedImage getSnapshot(Widget child) {
		if (Constants.getBoolean(SCREENSHOTS)) {
			return createSnapshot(child);
		} else {
			return null;
		}
	}

	private @Nullable BufferedImage createSnapshot(Widget child) {
		int contentWidth = getContentWidth();
		int contentHeight = getContentHeight();

		BufferedImage image = new BufferedImage(contentWidth, contentHeight);
		GraphicsContext g = image.getGraphicsContext();
		g.reset();

		// Apply the background of this container in case the child is transparent.
		getStyle().getBackground().apply(g, contentWidth, contentHeight);

		g.translate(-child.getX(), -child.getY());
		renderChild(child, g);

		return image;
	}

	private boolean onPointerDragged(int contentWidth, int pagesCount, int pointerX, int pointerY) {
		int shiftX = pointerX - this.previousX;
		if (this.pressed && shiftX != 0) {
			int shiftY = pointerY - this.previousY;
			Widget currentChild = getChild(CURRENT);
			if (!this.moving && Math.abs(shiftX) > Math.abs(shiftY)) {
				// Start to drag when moving horizontally.
				setHiddenChild(currentChild);
				this.moving = true;
				this.previousPosition = 0;
				loadOtherChild(contentWidth, pagesCount, shiftX);
			}
			int previousChildX = currentChild.getX();
			int childX = previousChildX + shiftX;
			if (previousChildX > 0 && childX < 0 || previousChildX < 0 && childX > 0) {
				// Change way.
				this.pressedX = pointerX;
				this.pressedTime = Util.platformTimeMillis();
				Widget otherChild = getChild(OTHER);
				removeChild(otherChild);
				if (this.otherSnapshot != null) {
					this.otherSnapshot.close();
					this.otherSnapshot = null;
				}
				loadOtherChild(contentWidth, pagesCount, shiftX);
				otherChild = getChild(OTHER);
				otherChild.setPosition(otherChild.getX() + previousChildX, 0);
			}
			if (this.moving) {
				this.position = childX;
				currentChild.setPosition(childX, 0);
				Widget otherChild = getChild(OTHER);
				otherChild.setPosition(otherChild.getX() + shiftX, 0);
				requestRender();

				this.previousX = pointerX;
				this.previousY = pointerY;
				return true;
			}
		}
		return false;
	}

	private void loadOtherChild(int contentWidth, int pagesCount, int shiftX) {
		int otherIndex;
		int otherShiftX;
		if (shiftX > 0) {
			// Load previous page.
			if (this.currentPageIndex == 0) {
				otherIndex = pagesCount - 1;
			} else {
				otherIndex = this.currentPageIndex - 1;
			}
			otherShiftX = -contentWidth;
		} else {
			// Load next page.
			if (this.currentPageIndex == pagesCount - 1) {
				otherIndex = 0;
			} else {
				otherIndex = this.currentPageIndex + 1;
			}
			otherShiftX = contentWidth;
		}
		Widget otherWidget = this.pages[otherIndex];
		this.otherPageIndex = otherIndex;
		super.addChild(otherWidget);
		int contentHeight = getContentHeight();
		computeChildOptimalSize(otherWidget, contentWidth, contentHeight);
		layOutChild(otherWidget, otherShiftX, 0, contentWidth, contentHeight);

		this.otherSnapshot = getSnapshot(otherWidget);
	}

	private boolean onPointerReleased(final int pointerX, final int contentWidth) {
		if (this.moving) {
			MicroUI.callSerially(new Runnable() {
				@Override
				public void run() {
					SwipeContainer.this.pressed = false;
					if (getChildrenCount() > 1) {
						final Widget currentChild = getChild(CURRENT);
						int childX = currentChild.getX();
						// Depending on the position of the child with the middle of the container, keep it or remove
						// it.
						Widget otherChild = getChild(OTHER);
						float speed = -(float) (pointerX - SwipeContainer.this.pressedX)
								/ (Util.platformTimeMillis() - SwipeContainer.this.pressedTime);
						if (childX < 0) {
							if (childX < -contentWidth / SCREEN_RATIO || speed > SPEED_THRESHOLD) {
								doAnimation(currentChild, otherChild, childX, -contentWidth,
										SwipeContainer.this.otherPageIndex, false);
							} else {
								doAnimation(currentChild, otherChild, childX, 0, SwipeContainer.this.currentPageIndex,
										true);
							}
						} else {
							if (childX > contentWidth / SCREEN_RATIO || speed < -SPEED_THRESHOLD) {
								doAnimation(currentChild, otherChild, childX, contentWidth,
										SwipeContainer.this.otherPageIndex, false);
							} else {
								doAnimation(currentChild, otherChild, childX, 0, SwipeContainer.this.currentPageIndex,
										true);
							}
						}
					}
				}
			});
			return true;
		} else {
			if (Constants.getBoolean(SCREENSHOTS)) {
				if (this.currentSnapshot != null) {
					this.currentSnapshot.close();
					this.currentSnapshot = null;
				}
			}

			this.pressed = false;
		}
		return false;
	}

}
