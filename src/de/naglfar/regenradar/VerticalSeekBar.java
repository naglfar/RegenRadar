package de.naglfar.regenradar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {
	private OnSeekBarChangeListener changeListener;
	private int x,y,z,w;

	public VerticalSeekBar(Context context) {
		super(context);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
		this.x=w;
		this.y=h;
		this.z=oldw;
		this.w=oldh;
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		onSizeChanged(x, y, z, w);
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener){
		this.changeListener = mListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(changeListener!=null) {
					changeListener.onStartTrackingTouch(this);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
				onSizeChanged(getWidth(), getHeight(), 0, 0);
				changeListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
				break;
			case MotionEvent.ACTION_UP:
				changeListener.onStopTrackingTouch(this);
				break;

			case MotionEvent.ACTION_CANCEL:
				break;
		}
		return true;
	}
}