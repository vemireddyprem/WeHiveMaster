package uk.co.wehive.hive.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RectangularImageView extends ImageView {

    public RectangularImageView(Context context) {
        super(context);
    }

    public RectangularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * (0.7))); //Snap to width
    }
}
