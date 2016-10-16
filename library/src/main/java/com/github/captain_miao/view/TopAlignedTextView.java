package com.github.captain_miao.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * It's from http://stackoverflow.com/a/32836547/703225
 *
 * @author YanLu
 * @since 16/10/17
 */
public class TopAlignedTextView extends TextView {


    private final Paint mPaint = new Paint();

    private final Rect mBounds = new Rect();

    public TopAlignedTextView(Context context) {
        this(context, null);
    }

    public TopAlignedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopAlignedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TopAlignedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final String text = calculateTextParams();

        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left + getPaddingLeft(), -mBounds.top + getPaddingTop());
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        canvas.drawText(text, -left + getPaddingLeft(), mBounds.bottom - bottom, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateTextParams();
        setMeasuredDimension(mBounds.width() + 1 + getPaddingLeft() + getPaddingRight(),
                -mBounds.top + 1 + mBounds.bottom+ getPaddingTop() + getPaddingBottom());
    }

    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }
}
