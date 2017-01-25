package com.github.captain_miao.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It's from http://stackoverflow.com/a/32836547/703225
 *
 * @author YanLu
 * @since 16/10/17
 */
public class TopAlignedTextView extends TextView {


    private final TextPaint mPaint = new TextPaint();

    private final Rect mBounds = new Rect();

    /**
     * The end punctuation which will be removed when appending #ELLIPSIS.
     */
    private Pattern endPunctuationPattern;

    DisplayMetrics dm;
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
        setMaxLines(a.getInt(0, Integer.MAX_VALUE));
        a.recycle();
        setEndPunctuationPattern(DEFAULT_END_PUNCTUATION);
    }

    private void init(Context context, AttributeSet attrs) {
        dm = getResources().getDisplayMetrics();
    }

    public void setEndPunctuationPattern(Pattern pattern) {
      this.endPunctuationPattern = pattern;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final String text = calculateTextParams();

        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left + getPaddingLeft(), -mBounds.top + getPaddingTop());
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());


        StaticLayout mTextLayout = new StaticLayout(text, mPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);



        int linesCount = getLinesCount();
        CharSequence workingText = text;
        if (mTextLayout.getLineCount() > linesCount) {
            // We have more lines of text than we are allowed to display.
            workingText = text.subSequence(0, mTextLayout.getLineEnd(linesCount - 1));
            while (createWorkingLayout(workingText + ELLIPSIS).getLineCount() > linesCount) {
                int lastSpace = workingText.toString().lastIndexOf(' ');
                if (lastSpace == -1) {
                    break;
                }
                workingText = workingText.subSequence(0, lastSpace);
            }
            // We should do this in the loop above, but it's cheaper this way.
            if (workingText instanceof Spannable) {
                SpannableStringBuilder builder = new SpannableStringBuilder(workingText);
                Matcher matcher = endPunctuationPattern.matcher(workingText);
                if (matcher.find()) {
                    builder.replace(matcher.start(), workingText.length(), ELLIPSIS);
                }
                workingText = builder;
            } else {
                workingText = endPunctuationPattern.matcher(workingText).replaceFirst("");
                workingText = workingText + ELLIPSIS;
            }
        }

        mTextLayout = new StaticLayout(workingText, mPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        canvas.save();
        // calculate x and y position where your text will be placed

        mTextLayout.draw(canvas);
        canvas.restore();

        //canvas.drawText(text, -left + getPaddingLeft(), mBounds.bottom - bottom, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateTextParams();
        // lines
        int width = mBounds.width() + 1 + getPaddingLeft() + getPaddingRight();
        float lines = (width + 1f) / dm.widthPixels;
        // don't support getPaddingTop() and getPaddingBottom()
        int height = -mBounds.top + 1 + mBounds.bottom;
        int sumHeight = height + (int)(mBounds.height() * Math.ceil(lines)) + 1;
        setMeasuredDimension(Math.min(getMeasuredWidth(), Math.min(width, dm.widthPixels)), sumHeight);
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

    // add ELLIPSIS
    private static final String ELLIPSIS = "\u2026";
    private static final Pattern DEFAULT_END_PUNCTUATION = Pattern.compile("[\\.,\u2026;\\:\\s]*$", Pattern.DOTALL);
    private final List<EllipsizeListener> ellipsizeListeners = new ArrayList<EllipsizeListener>();
    private boolean isEllipsized;
    private boolean isStale;
    private boolean programmaticChange;
    private CharSequence fullText;
    private int maxLines;

    public interface EllipsizeListener {
      void ellipsizeStateChanged(boolean ellipsized);
    }

    public void addEllipsizeListener(EllipsizeListener listener) {
      if (listener == null) {
        throw new NullPointerException();
      }
      ellipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(EllipsizeListener listener) {
      ellipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
      return isEllipsized;
    }

    @Override
    public void setMaxLines(int maxLines) {
      super.setMaxLines(maxLines);
      this.maxLines = maxLines;
      isStale = true;
    }

    @SuppressLint("Override")
    public int getMaxLines() {
      return maxLines;
    }

    public boolean ellipsizingLastFullyVisibleLine() {
      return maxLines == Integer.MAX_VALUE;
    }


    /**
     * Get how many lines of text we are allowed to display.
     */
    private int getLinesCount() {
        if (ellipsizingLastFullyVisibleLine()) {
            int fullyVisibleLinesCount = getFullyVisibleLinesCount();
            if (fullyVisibleLinesCount == -1) {
                return 1;
            } else {
                return fullyVisibleLinesCount;
            }
        } else {
            return maxLines;
        }
    }

    /**
     * Get how many lines of text we can display so their full height is visible.
     */
    private int getFullyVisibleLinesCount() {
      Layout layout = createWorkingLayout("");
      int height = getHeight() - getPaddingTop() - getPaddingBottom();
      int lineHeight = layout.getLineBottom(0);
      return height / lineHeight;
    }

    private Layout createWorkingLayout(CharSequence workingText) {
        return new StaticLayout(workingText, getPaint(),
                getWidth() - getPaddingLeft() - getPaddingRight(),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }


    private void resetText() {
        CharSequence workingText = fullText;
        boolean ellipsized = false;
        Layout layout = createWorkingLayout(workingText);
        int linesCount = getLinesCount();
        if (layout.getLineCount() > linesCount) {
            // We have more lines of text than we are allowed to display.
            workingText = fullText.subSequence(0, layout.getLineEnd(linesCount - 1));
            while (createWorkingLayout(workingText + ELLIPSIS).getLineCount() > linesCount) {
                int lastSpace = workingText.toString().lastIndexOf(' ');
                if (lastSpace == -1) {
                    break;
                }
                workingText = workingText.subSequence(0, lastSpace);
            }
            // We should do this in the loop above, but it's cheaper this way.
            if (workingText instanceof Spannable) {
                SpannableStringBuilder builder = new SpannableStringBuilder(workingText);
                Matcher matcher = endPunctuationPattern.matcher(workingText);
                if (matcher.find()) {
                    builder.replace(matcher.start(), workingText.length(), ELLIPSIS);
                }
                workingText = builder;
            } else {
                workingText = endPunctuationPattern.matcher(workingText).replaceFirst("");
                workingText = workingText + ELLIPSIS;
            }

            ellipsized = true;
        }
        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setText(workingText);
            } finally {
                programmaticChange = false;
            }
        }
        isStale = false;
        if (ellipsized != isEllipsized) {
            isEllipsized = ellipsized;
            for (EllipsizeListener listener : ellipsizeListeners) {
                listener.ellipsizeStateChanged(ellipsized);
            }
        }
    }
}
