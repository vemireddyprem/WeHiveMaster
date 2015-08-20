package uk.co.wehive.hive.utils.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ListViewSwipeGesture implements View.OnTouchListener {

    Activity activity;

    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private ListView mListView;

    //private DismissCallbacks mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
    static TouchCallbacks tCallbacks;
    private int mSmallWidth = 1;
    private int mTextWidth = 1;
    private int mFlagWidth = 200;
    private int mTextHeight = 1;

    // Transient properties
    private List<PendingDismissData> mPendingDismisses = new ArrayList<PendingDismissData>();
    private float mDownX;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;
    private ViewGroup mDownView, old_mDownView;
    private ViewGroup mDownView_parent;
    private TextView mDownView_parent_txt1;
    private int temp_position;
    private int opened_position;
    private int stagged_position;
    private int mDismissAnimationRefCount = 0;
    private boolean mPaused;
    public boolean mOptionsDisplay = false;

    private int mViewContainerRef;
    private int mImgRef;
    private boolean mHasHeader;

    //Intermediate Usages
    String RangeOneColor = "#db5554";   //"#FFD060"

    //Functional  Usages
    public String HalfColor;          //Green
    public Drawable HalfDrawable;

    public ListViewSwipeGesture(ListView listView, TouchCallbacks Callbacks, Activity context,
                                int viewContainerRef, int imgRef, boolean hasHeader) {

        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mListView = listView;
        activity = context;
        tCallbacks = Callbacks;
        mViewContainerRef = viewContainerRef;
        mImgRef = imgRef;
        mHasHeader = hasHeader;
        GetResourcesValues();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {            //Invokes OnClick Functionality
                if (!mOptionsDisplay) {
                    tCallbacks.OnClickListView(temp_position);
                }
            }
        });
    }

    public interface TouchCallbacks {                                           //Callback functions

        void HalfSwipeListView(int position);

        void OnClickListView(int position);

        void LoadDataForScroll(int count);

        void onDismiss(ListView listView, int[] reverseSortedPositions);
    }

    private void GetResourcesValues() {
        mAnimationTime = mListView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        HalfColor = RangeOneColor;          //Green
        HalfDrawable = activity.getResources().getDrawable(mImgRef);
    }


    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    public GestureScroll makeScrollListener() {
        return new GestureScroll();
    }


    class GestureScroll implements AbsListView.OnScrollListener {
        //Scroll Usages
        private int visibleThreshold = 2;
        private int previousTotal = 0;
        private boolean loading = true;
        private int previousFirstVisibleItem = 0;
        private long previousEventTime = 0, currTime, timeToScrollOneElement;
        private double speed = 0;

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                tCallbacks.LoadDataForScroll(totalItemCount);
                loading = true;
            }

            if (previousFirstVisibleItem != firstVisibleItem) {
                currTime = System.currentTimeMillis();
                timeToScrollOneElement = currTime - previousEventTime;
                speed = ((double) 1 / timeToScrollOneElement) * 1000;

                previousFirstVisibleItem = firstVisibleItem;
                previousEventTime = currTime;
            }
        }

        public double getSpeed() {
            return speed;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean onTouch(final View view, MotionEvent event) {

        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
            mSmallWidth = mViewWidth / 7;
            mTextWidth = mFlagWidth;
        }

        int tempWidth;
        tempWidth = mSmallWidth;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                temp_position = mListView.pointToPosition((int) event.getX(), (int) event.getY());

                if (temp_position == 0) {
                    mPaused = true;
                } else {
                    SwipeListViewItemRow row = null;
                    try {
                        row = (SwipeListViewItemRow) mListView.getAdapter().getItem(temp_position);
                    } catch (Exception e) {
                        mPaused = true;
                    }

                    mPaused = row != null && !row.isSwipe();
                }

                if (mPaused) {
                    return false;
                }

                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) event.getRawX() - listViewCoords[0];
                int y = (int) event.getRawY() - listViewCoords[1];
                ViewGroup child;
                for (int i = 0; i < childCount; i++) {
                    child = (ViewGroup) mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mDownView_parent = child;
                        mDownView = (ViewGroup) child.findViewById(mViewContainerRef);
                        if (mDownView_parent.getChildCount() == 1) {
                            mTextHeight = mDownView_parent.getHeight();

                            SetBackGroundforList();
                        }

                        if (old_mDownView != null && mDownView != old_mDownView) {
                            ResetListItem(old_mDownView);
                            old_mDownView = null;
                            return false;
                        }
                        break;
                    }
                }

                if (mDownView != null) {
                    mDownX = event.getRawX();
                    mVelocityTracker = VelocityTracker.obtain();

                    mVelocityTracker.addMovement(event);
                } else {
                    mDownView = null;
                }

                view.onTouchEvent(event);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }
                float deltaX = event.getRawX() - mDownX;
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000); // 1000 by default but
                float velocityX = mVelocityTracker.getXVelocity();                                            // it was too much
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean swipe = false;
                boolean swipeRight = false;

                if (Math.abs(deltaX) > tempWidth) {
                    swipe = true;
                    swipeRight = deltaX > 0;

                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity && absVelocityY < absVelocityX) {
                    // dismiss only if flinging in the same direction as dragging
                    swipe = (velocityX < 0) == (deltaX < 0);
                    swipeRight = mVelocityTracker.getXVelocity() > 0;
                }

                if (deltaX < 0 && swipe) {
                    mListView.setDrawSelectorOnTop(false);

                    if (swipe && !swipeRight && deltaX <= -tempWidth) {
                        FullSwipeTrigger();
                    } else {
                        ResetListItem(mDownView);
                    }
                } else {
                    ResetListItem(mDownView);
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownView = null;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                SwipeListViewItemRow row;
                try {
                    row = (SwipeListViewItemRow) mListView.getAdapter().getItem(temp_position);
                } catch (Exception e) {
                    break;
                }

                if (row == null) {
                    break;
                }

                float deltaX = event.getRawX() - mDownX;
                if (mVelocityTracker == null || mPaused || deltaX > 0 || !row.isSwipe()) {
                    break;
                }

                mVelocityTracker.addMovement(event);

                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping && deltaX < 0) {

                    int width = mTextWidth;

                    if (-deltaX < width) {
                        mDownView.setTranslationX(deltaX);
                        return false;
                    }
                    return false;
                } else if (mSwiping) {
                    ResetListItem(mDownView);
                }
                break;
            }
        }
        return false;
    }

    private void SetBackGroundforList() {
        mDownView_parent_txt1 = new TextView(activity.getApplicationContext());
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mDownView_parent_txt1.setLayoutParams(lp1);
        mDownView_parent_txt1.setGravity(Gravity.END);
        mDownView_parent_txt1.setWidth(mFlagWidth);
        mDownView_parent_txt1.setPadding(0, mTextHeight / 4, 0, 0);
        mDownView_parent_txt1.setHeight(mTextHeight);
        mDownView_parent_txt1.setBackgroundColor(Color.parseColor(HalfColor));
        mDownView_parent_txt1.setCompoundDrawablesWithIntrinsicBounds(null, HalfDrawable, null, null);
        mDownView_parent.addView(mDownView_parent_txt1, 0);
    }

    private void ResetListItem(View tempView) {
        tempView.animate().translationX(0).alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int count = mDownView_parent.getChildCount() - 1;
                for (int i = 0; i < count; i++) {
                    mDownView_parent.getChildAt(i);
                    mDownView_parent.removeViewAt(0);
                }
                mOptionsDisplay = false;
            }
        });
        stagged_position = -1;
        opened_position = -1;
    }

    private void FullSwipeTrigger() {
        old_mDownView = mDownView;
        int width = mTextWidth;

        mDownView.animate().translationX(-width).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mOptionsDisplay = true;
                        stagged_position = temp_position;
                        mDownView_parent_txt1.setOnTouchListener(new touchClass());
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }
                });
    }

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;

        public PendingDismissData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingDismissData other) {
            // Sort by descending position
            return other.position - position;
        }
    }

    private void performDismiss(final View dismissView, final int dismissPosition) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();

        ((ViewGroup) dismissView).getChildAt(1).animate().translationX(0).alpha(1f).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ((ViewGroup) dismissView).removeViewAt(0);
                ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(mAnimationTime);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        --mDismissAnimationRefCount;
                        if (mDismissAnimationRefCount == 0) {
                            // No active animations, process all pending dismisses.
                            // Sort by descending position
                            Collections.sort(mPendingDismisses);

                            int[] dismissPositions = new int[mPendingDismisses.size()];
                            for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
                                dismissPositions[i] = mPendingDismisses.get(i).position;
                            }
                            tCallbacks.onDismiss(mListView, dismissPositions);
                            mPendingDismisses.clear();
                        }
                    }
                });

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        lp.height = (Integer) valueAnimator.getAnimatedValue();
                        dismissView.setLayoutParams(lp);
                    }
                });

                mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
                animator.start();
            }
        });
    }

    class touchClass implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            opened_position = mListView.getPositionForView((View) v.getParent());
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN: {
                    if (opened_position == stagged_position && mOptionsDisplay) {
                        tCallbacks.HalfSwipeListView(temp_position);
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
}
