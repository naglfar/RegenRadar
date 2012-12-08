package de.naglfar.regenradar;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class OneFingerViewPager extends ViewPager {

	int touchCounter = 0;
	int touchCounter2 = 0;

	public OneFingerViewPager(Context context) {
		super(context);
	}

	public OneFingerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// FIXME:

/*
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		Log.v("VP1", e.getAction()+" / c:"+e.getPointerCount());
		if (e.getPointerCount() > 1 && touchCounter2 < 3) {
			Log.v("VP2", "!");
			return false;
		} else if (touchCounter2 > 2) {
			Log.v("VP3", "!");
			return true;
		} else {
			touchCounter2 += 1;
			return false;
		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		//Log.v("VP1", e.getAction()+" / c:"+e.getPointerCount() + " / c2:" + touchCounter);

		if (e.getPointerCount() > 1 && touchCounter <2) {
			if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) { touchCounter = 0; touchCounter2 = 0; }
			return false;
		} else if (touchCounter ==2) {
			e.setAction(MotionEvent.ACTION_DOWN);
			touchCounter += 1;
			return super.onTouchEvent(e);
		} else if (touchCounter >2) {
			if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) { touchCounter = 0; touchCounter2 = 0; }
			return super.onTouchEvent(e);
		} else {
			touchCounter += 1;
		}
		if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) { touchCounter = 0; touchCounter2 = 0; }

		Log.v("VP1", "Eaten!");

		return true;
	}
	*/
}
