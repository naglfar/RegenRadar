package de.naglfar.regenradar;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class OneFingerViewPager extends ViewPager {

	float startX = 0;
	float startY = 0;
	float distX = 0;
	float distY = 0;
	boolean ignoreEvent = false;
	boolean firstTouch = false;


	public OneFingerViewPager(Context context) {
		super(context);
	}

	/**
	 * Constructor that gets called when using the class in XML layouts
	 *
	 * @param context
	 * @param attrs
	 */
	public OneFingerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * return false if the viewpager should ignore this event, will get called with every new step
	 * in the event until it's over or true is returned, at which point the viewpager will eat the event up
	 * until it's done
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
			clearEvent();
			if (ignoreEvent) { return false; }
			else { return true; }
		}

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			startX = e.getX();
			startY = e.getY();
		}
		distX = Math.abs(Math.abs(startX) - Math.abs(e.getX()));
		distY = Math.abs(Math.abs(startY) - Math.abs(e.getY()));

		if (e.getPointerCount() > 1 && !distanceOverThreshold()) {
			ignoreEvent = true;
		}
		if (!ignoreEvent && distanceOverThreshold() && e.getPointerCount() == 1) {
			Log.v("PAGER", "T: "+distanceOverThreshold());

			return true;
		}

		return false;

	}
	/**
	 * gets called with every event step as soon as onInterceptTouchEvent returned true
	 * The very first event we handle here _has_ to be ACTION_DOWN or the super method will crash
	 * FIXME: seems a bit hacky
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (firstTouch == false) {
			e.setAction(MotionEvent.ACTION_DOWN);
			firstTouch = true;
		}
		if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
			clearEvent();
		}
		return super.onTouchEvent(e);
	}

	/**
	 * clear variables for the next event, called on ACTION_UP / ACTION_CANCEL
	 */
	public void clearEvent() {
		startX = 0f;
		startY = 0f;
		distX = 0f;
		distY = 0f;
		ignoreEvent = false;
		firstTouch = false;
	}

	/**
	 * function to decide if the pointer traveled far enough to consider this a gesture
	 */
	public boolean distanceOverThreshold() {
		return distX > 5 || distY > 5;
	}
}
