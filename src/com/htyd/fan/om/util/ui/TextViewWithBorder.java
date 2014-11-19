package com.htyd.fan.om.util.ui;

import com.htyd.fan.om.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithBorder extends TextView {

	private Paint paint;
	private Resources r;

	public TextViewWithBorder(Context context) {
		super(context);
	}

	public TextViewWithBorder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextViewWithBorder(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(r.getColor(R.color.activity_bg_color));
		canvas.drawLine(0, getMeasuredHeight()-1, getMeasuredWidth(), getMeasuredHeight()-1, paint);
		canvas.drawLine(getMeasuredWidth()-1, 0, getMeasuredWidth()-1,
				getMeasuredHeight(), paint);
		super.onDraw(canvas);
	}

	private void init() {
		r = getResources();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(r.getColor(R.color.black));
		paint.setStrokeWidth(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(widthMeasureSpec>heightMeasureSpec){
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		}else{
			super.onMeasure(heightMeasureSpec, heightMeasureSpec);
		}
	}

}
