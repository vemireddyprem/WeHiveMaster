/*******************************************************************************
 PROJECT:       Hive
 FILE:          CustomFontTextView.java
 DESCRIPTION:   Custom font for Edit Text. Set the property fontName in the layout
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        08/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import uk.co.wehive.hive.R;

public class CustomFontTextView extends TextView {

    /**
     * Constructor.
     */
    public CustomFontTextView(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor.
     */
    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor.
     */
    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Set the font.
     */
    private void init(AttributeSet attrs) {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFont);
            String fontName = a.getString(R.styleable.CustomFont_fontName);
            if (fontName != null) {
                font = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
            }
            a.recycle();
        }
        if (!this.isInEditMode()) {
            this.setTypeface(font, Typeface.NORMAL);
        }
    }
}
