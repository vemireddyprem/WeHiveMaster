package uk.co.wehive.hive.utils.camera;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class ViewPagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {

    boolean mNeedsRedraw = false;

    public ViewPagerContainer(Context context) {
        super(context);
        init();
    }

    public ViewPagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClipChildren(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private ViewPager mPager;

    @Override
    protected void onFinishInflate() {
        mPager = (ViewPager) getChildAt(0);
        mPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mPager.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        if (mNeedsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }
}
