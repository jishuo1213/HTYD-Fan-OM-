package com.htyd.fan.om.util.ui;

import com.htyd.fan.om.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithBottomLine extends TextView {

	private Paint paint;
	private Resources r;
	
	public TextViewWithBottomLine(Context context) {
		super(context);
	}

	public TextViewWithBottomLine(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextViewWithBottomLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawLine(0, getMeasuredHeight()-1, getMeasuredWidth(), getMeasuredHeight()-1, paint);
		super.onDraw(canvas);
	}
	
	
	private void init() {
		r = getResources();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(r.getColor(R.color.gray_half));
		paint.setStrokeWidth(1);
	}
}
