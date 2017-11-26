package com.github.locke.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is a collapsible TextView, when the text exceeds the display area,
 * it will automatically collapse the text, add append a clickable extend text in end of the last line.
 *
 * @author 1072174696@qq.com on 11/18/17.
 */
public class CollapsibleTextView extends AppCompatTextView {

    private static final String ELLIPSIS = "...\t";
    private static final String EMPTY_CHAR = " ";
    private static final String DOUBLE_LINE = "\n\n";
    private static final int ELLIPSIS_PADDING = 3;

    private CharSequence mOriginText;
    private String mExtendText = "";
    private float mExtendTextSize;
    private OnClickListener mOnExtendClickListener;

    public CollapsibleTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleTextView);
            try {
                mExtendText = ta.getString(R.styleable.CollapsibleTextView_extendText);
                mExtendTextSize = ta.getDimension(R.styleable.CollapsibleTextView_extendTextSize, getTextSize());
            } finally {
                ta.recycle();
            }
        }
    }

    public void setOnExtendClickListener(OnClickListener onExtendClickListener) {
        mOnExtendClickListener = onExtendClickListener;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mOriginText = text;

        resetText(getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
    }

    public CharSequence getOriginText() {
        return mOriginText;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        super.setText(mOriginText, BufferType.NORMAL);
        resetText(height);
    }

    private void resetText(int height) {
        Layout layout = getLayout();
        if (layout == null || height <= 0) {
            return;
        }

        int lineCount = layout.getLineCount();
        int textHeight = layout.getHeight();
        if (lineCount > 0 && textHeight > 0) {
            int lineHeight = textHeight / lineCount;
            int maxLines = height / lineHeight;

            if (maxLines > 0 && maxLines < lineCount) {
                CharSequence text = preHandleCollapsedText(layout, maxLines);
                super.setText(buildCollapsedText(text, maxLines), BufferType.NORMAL);
            }
        }
    }

    /**
     * Cut the origin text to max display line, and if end with empty line, fill it with next line.
     *
     * @param layout
     * @param maxLines
     * @return
     */
    private CharSequence preHandleCollapsedText(Layout layout, int maxLines) {
        CharSequence text = mOriginText.subSequence(0, layout.getLineEnd(maxLines - 1));
        StringBuilder stringBuilder = new StringBuilder(text);
        while (stringBuilder.toString().endsWith(DOUBLE_LINE) && layout.getLineCount() > maxLines) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            CharSequence nextLine = mOriginText.subSequence(layout.getLineStart(maxLines), layout.getLineEnd(maxLines));
            stringBuilder.append(nextLine);
            maxLines += 1;
        }
        return stringBuilder.toString();
    }

    /**
     * Append expend text to the display text, and make sure not exceed the limit of lines.
     * By detail, if overflow the limit lines, will cut a number of chars at end of display text,
     * Else if it's not enough to fill the limit of lines, will append a number of empty chars at end of display text.
     *
     * @param text
     * @param maxLines
     * @return
     */
    private Spannable buildCollapsedText(CharSequence text, int maxLines) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        appendExtendText(spannableStringBuilder);

        spannableStringBuilder.insert(0, text);
        int insertEnd = text.length();

        int overflow;
        while ((overflow = measureLineOverflowChars(spannableStringBuilder, maxLines)) != 0) {
            if (insertEnd - overflow < 0) {
                break;
            }

            if (overflow > 0) {
                int start = insertEnd - overflow;
                spannableStringBuilder.delete(start, insertEnd);
                insertEnd = start;
            }

            if (overflow < 0) {
                insertEmptyChars(spannableStringBuilder, insertEnd + ELLIPSIS.length(), -overflow);
            }
        }

        float overflowLength = measureExpendTextOverflowLength();
        if (overflowLength > 0) {
            deleteEndOfLength(spannableStringBuilder, insertEnd, overflowLength);
        }
        return spannableStringBuilder;
    }

    /**
     * Measure the number of overflow characters within the limit line.
     * When text displayed exceeds the max limit lines, the return value is greater than 0,
     * Else if text displayed not enough to fill the limit of lines, the return value is less than 0.
     *
     * @param text
     * @param maxLines
     * @return
     */
    private int measureLineOverflowChars(Spannable text, int maxLines) {
        super.setText(text, BufferType.NORMAL);
        Layout layout = getLayout();
        if (layout.getLineCount() > maxLines) {
            return 1;
        } else if (layout.getLineCount() == maxLines) {
            CharSequence lastLine = text.subSequence(layout.getLineStart(maxLines - 1), text.length());
            if (lastLine.length() > ELLIPSIS.length() + ELLIPSIS_PADDING + mExtendText.length()) {
                float lastLineLength = getPaint().measureText(lastLine.toString());
                float emptyWidth = lastLineLength - layout.getWidth();
                return (int) (emptyWidth / getPaint().measureText(EMPTY_CHAR));
            }
        }
        return 0;
    }

    /**
     * Measure the length of overflow due to the different text size of expend text.
     *
     * @return
     */
    private float measureExpendTextOverflowLength() {
        TextPaint paint = getPaint();
        float originalTextSize = paint.getTextSize();
        float defaultLength = paint.measureText(mExtendText);
        paint.setTextSize(mExtendTextSize);
        float actualLength = paint.measureText(mExtendText);

        float overflowLength = actualLength - defaultLength;
        paint.setTextSize(originalTextSize);
        return overflowLength;
    }

    /**
     * Delete the specified length from the end of the text.
     *
     * @param spannableStringBuilder
     * @param end
     * @param length
     */
    private void deleteEndOfLength(SpannableStringBuilder spannableStringBuilder, int end, float length) {
        int deleteEnd = end - 1;
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(spannableStringBuilder.charAt(deleteEnd)));
        while (getPaint().measureText(stringBuilder.toString()) < length) {
            stringBuilder.append(spannableStringBuilder.charAt(--deleteEnd));
        }
        spannableStringBuilder.delete(end - stringBuilder.length(), end);
    }

    private void appendExtendText(SpannableStringBuilder spannableStringBuilder) {
        spannableStringBuilder.append(ELLIPSIS);
        appendEmptyChars(spannableStringBuilder, ELLIPSIS_PADDING);
        spannableStringBuilder.append(mExtendText);
        spannableStringBuilder.setSpan(new ViewMoreSpan(), spannableStringBuilder.length() - mExtendText.length(), spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void appendEmptyChars(SpannableStringBuilder spannableStringBuilder, int count) {
        insertEmptyChars(spannableStringBuilder, spannableStringBuilder.length(), count);
    }

    private void insertEmptyChars(SpannableStringBuilder spannableStringBuilder, int where, int count) {
        for (int i = 0; i < count; i++) {
            spannableStringBuilder.insert(where, EMPTY_CHAR);
        }
    }

    private class ViewMoreSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setTextSize(mExtendTextSize);
        }

        @Override
        public void onClick(View widget) {
            if (mOnExtendClickListener != null) {
                mOnExtendClickListener.onClick(CollapsibleTextView.this);
            }
        }
    }
}

