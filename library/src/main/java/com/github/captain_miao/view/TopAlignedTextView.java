package com.github.captain_miao.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * It's from http://stackoverflow.com/a/32836547/703225
 *
 * @author YanLu
 * @since 16/10/17
 */
public class TopAlignedTextView extends TextView implements View.OnClickListener {


    private final TextPaint mPaint = new TextPaint();

    private final Rect mBounds = new Rect();

    /**
     * The end punctuation which will be removed when appending #ELLIPSIS.
     */
    private Pattern endPunctuationPattern;

    DisplayMetrics dm;
    private boolean mIsExpand = false;
    public TopAlignedTextView(Context context) {
        this(context, null);
    }

    public TopAlignedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopAlignedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        super.setEllipsize(null);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.maxLines });
        setMaxLines(a.getInt(0, Integer.MAX_VALUE));
        a.recycle();
        setEndPunctuationPattern(DEFAULT_END_PUNCTUATION);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TopAlignedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
        super.setEllipsize(null);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.maxLines });
        setMaxLines(a.getInt(0, -1));
        a.recycle();
        setEndPunctuationPattern(DEFAULT_END_PUNCTUATION);
        if(maxLines <= 0){
            throw new IllegalStateException("maxLines must >= 1");
        }
    }

    private void init(Context context, AttributeSet attrs) {
        dm = getResources().getDisplayMetrics();
        setOnClickListener(this);
    }

    public void setEndPunctuationPattern(Pattern pattern) {
      this.endPunctuationPattern = pattern;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final String text = calculateTextParams();
        Layout layout = getLayout();
        if(layout != null) {
            mBounds.offset(-mBounds.left + getPaddingLeft(), -mBounds.top + getPaddingTop());
            mPaint.setAntiAlias(true);
            mPaint.setColor(getCurrentTextColor());

            StaticLayout mTextLayout = new StaticLayout(text, mPaint,
                    layout.getWidth() - getPaddingLeft()- getPaddingRight(),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


            int linesCount = mTextLayout.getLineCount();
            CharSequence workingText = text;
            workingText = endPunctuationPattern.matcher(workingText).replaceFirst("");
            if (!mIsExpand && linesCount > maxLines) {
                int index = text.length() - ELLIPSIS.length();
                workingText = text.subSequence(0, index);
                while (createWorkingLayout(layout.getWidth() - getPaddingLeft()- getPaddingRight(), workingText + ELLIPSIS).getLineCount() > maxLines) {
                    index -= ELLIPSIS.length();
                    workingText = workingText.subSequence(0, index);
                }

                workingText = workingText + ELLIPSIS;

            }

            mTextLayout = new StaticLayout(workingText, mPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if(layout != null) {
            calculateTextParams();
            // lines
            int width = mBounds.width();
            float lines = (width + 1f) / layout.getWidth();
            if(mIsExpand) {
                // don't support getPaddingTop() and getPaddingBottom()
                int sumHeight = -mBounds.top + 1 + mBounds.bottom;
                if(lines > 1) {
                    sumHeight += (int) (layout.getLineBottom(0) * (Math.ceil(lines) - 1)) + 1;
                } else {
                    sumHeight = layout.getLineBottom(0);
                }
                setMeasuredDimension(Math.max(getMeasuredWidth(), layout.getWidth()), sumHeight);
            } else {
                int sumHeight = -mBounds.top + 1 + mBounds.bottom;
                if(lines > maxLines && maxLines > 1) {
                    sumHeight += layout.getLineBottom(0)  * (maxLines - 1) + 1;
                } else if(maxLines == 1 || lines <= 1){
                    sumHeight = layout.getLineBottom(0);
                }
                setMeasuredDimension(Math.max(getMeasuredWidth(), Math.min(width, dm.widthPixels)), sumHeight);
            }
        }
    }

    private String calculateTextParams() {
        final String text = TextUtils.isEmpty(getText()) ? "" : getText().toString();
        String workingText = endPunctuationPattern.matcher(text).replaceFirst("");
        final int textLength = workingText.length();
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(workingText, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }

    // add ELLIPSIS
    private static final String ELLIPSIS = "\u2026Mer\u25BC";
    private static final Pattern DEFAULT_END_PUNCTUATION = Pattern.compile("[\\.,\u2026Mer\u25BC;\\:\\s]*$", Pattern.DOTALL);
    private int maxLines;

    @Override
    public void onClick(View v) {
        mIsExpand = !mIsExpand;
        requestLayout();
    }


    @Override
    public void setMaxLines(int maxLines) {
      super.setMaxLines(maxLines);
      this.maxLines = maxLines;
    }

    @SuppressLint("Override")
    public int getMaxLines() {
      return maxLines;
    }


    private Layout createWorkingLayout(int width, CharSequence workingText) {
        return new StaticLayout(workingText, getPaint(),
                width,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }
}
