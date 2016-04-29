package org.mariotaku.wtf.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelperAccessor;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.mariotaku.wtf.R;

/**
 * Created by mariotaku on 16/4/27.
 */
public class MainSlidingPane extends ViewGroup {

    int mScrollOffset = 0;
    int mPrevScrollOffset = 0;
    PointF mTouchCoord = new PointF(Float.NaN, Float.NaN);
    PointF mFirstDownCoord = new PointF(Float.NaN, Float.NaN);
    private int mMaxSlidingUpLength;
    private int mMaxSlidingDownLength;
    ViewDragHelper mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            final ViewGroup.LayoutParams lp = child.getLayoutParams();
            if (lp instanceof LayoutParams) {
                return ((LayoutParams) lp).isContent();
            }
            return false;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mScrollOffset += dy;
            requestLayout();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            if (capturedChild instanceof ViewGroup) {
                ((ViewGroup) capturedChild).requestDisallowInterceptTouchEvent(true);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mScrollListener != null) {
                mScrollListener.onDragStateChanged(state);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int defTop = ((LayoutParams) releasedChild.getLayoutParams()).getDefaultTop();
            int top = releasedChild.getTop();
            if (top < defTop) { // Up position
                if (yvel > 0) { // Fling down
                    mDragHelper.settleCapturedViewAt(0, defTop);
                } else if (yvel < 0) { // Fling up
                    mDragHelper.settleCapturedViewAt(0, defTop - mMaxSlidingUpLength);
                } else { // Stopped, detect by position
                    if (defTop - top > mMaxSlidingUpLength / 2) { // Scroll up
                        mDragHelper.settleCapturedViewAt(0, defTop - mMaxSlidingUpLength);
                    } else {
                        mDragHelper.settleCapturedViewAt(0, defTop);
                    }
                }
            } else if (top > defTop) { // Down position
                if (yvel > 0) { // Fling down
                    final int finalTop = defTop + mMaxSlidingDownLength;
                    if (finalTop > top) {
                        mDragHelper.settleCapturedViewAt(0, finalTop);
                    }
                } else if (yvel < 0) { // Fling up
                    mDragHelper.settleCapturedViewAt(0, defTop);
                } else { // Stopped, detect by position
                    if (top - defTop > mMaxSlidingDownLength / 2) { // Scroll down
                        mDragHelper.settleCapturedViewAt(0, defTop + mMaxSlidingDownLength);
                    } else {
                        mDragHelper.settleCapturedViewAt(0, defTop);
                    }
                }
            } else {
                if (yvel > 0) { // Fling down
                    mDragHelper.settleCapturedViewAt(0, defTop + mMaxSlidingDownLength);
                } else if (yvel < 0) { // Fling up
                    mDragHelper.settleCapturedViewAt(0, defTop - mMaxSlidingUpLength);
                }
            }
            ViewCompat.postInvalidateOnAnimation(MainSlidingPane.this);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mMaxSlidingUpLength + mMaxSlidingDownLength;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int defTop = ((LayoutParams) child.getLayoutParams()).getDefaultTop();
            if (dy < 0) { // Sliding up
                return Math.max(top, defTop - mMaxSlidingUpLength);
            } else if (dy > 0) { // Sliding down
                return Math.min(top, defTop + mMaxSlidingDownLength);
            } else {
                return top;
            }
        }
    });
    private ScrollListener mScrollListener;
    private boolean mScrollingContent;

    public MainSlidingPane(Context context) {
        super(context);
    }

    public MainSlidingPane(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MainSlidingPane(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MainSlidingPane(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final View content = getContent();
        final float x = ev.getX(), y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mScrollingContent = false;
                mDragHelper.cancel();
                ViewDragHelperAccessor.setDragState(mDragHelper, ViewDragHelper.STATE_IDLE);
                mDragHelper.shouldInterceptTouchEvent(ev);
                mFirstDownCoord.set(x, y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float dx = x - mFirstDownCoord.x, dy = y - mFirstDownCoord.y;
                if (getDragState() == ViewDragHelper.STATE_SETTLING) {
                    if (mDragHelper.shouldInterceptTouchEvent(ev)) {
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                } else if (Math.abs(dx) > Math.abs(dy)) { // Scrolling horizontally
                    if (isScrolledDown()) {
                        return true;
                    } else {
                        mScrollingContent = true;
                        requestDisallowInterceptTouchEvent(true);
                    }
                } else if (isScrolledUp()) {
                    // Can scroll content down
                    int directionY = 0;
                    if (dy > 0) {
                        directionY = -1;
                    } else if (dy < 0) {
                        directionY = 1;
                    }
                    if (directionY != 0) {
                        if (ViewCompat.canScrollVertically(content, directionY)) {
                            mScrollingContent = true;
                            requestDisallowInterceptTouchEvent(true);
                        } else {
                            if (mDragHelper.shouldInterceptTouchEvent(ev)) {
                                return true;
                            }
                            return super.onInterceptTouchEvent(ev);
                        }
                    }
                } else {
                    // Can only move content panel
                    if (mDragHelper.shouldInterceptTouchEvent(ev)) {
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mTouchCoord.set(Float.NaN, Float.NaN);
                mFirstDownCoord.set(Float.NaN, Float.NaN);
                requestDisallowInterceptTouchEvent(false);
                break;
            }
            case MotionEvent.ACTION_UP: {
                mTouchCoord.set(Float.NaN, Float.NaN);
                mFirstDownCoord.set(Float.NaN, Float.NaN);
                requestDisallowInterceptTouchEvent(false);
                break;
            }
        }
        mTouchCoord.set(x, y);
        if (!mScrollingContent && mDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int noScrollTop = getPaddingTop(), nextTop = noScrollTop + mScrollOffset;
        if (mScrollListener != null) {
            mScrollListener.onScroll(nextTop, noScrollTop, mScrollOffset - mPrevScrollOffset);
            mPrevScrollOffset = mScrollOffset;
        }
        int left = getPaddingLeft();
        for (int i = 0, j = getChildCount(); i < j; i++) {
            final View child = getChildAt(i);
            final int bottom = nextTop + child.getMeasuredHeight();
            final int noScrollBottom = noScrollTop + child.getMeasuredHeight();
            child.layout(left, nextTop, left + child.getMeasuredWidth(), bottom);
            if (changed) {
                ((LayoutParams) child.getLayoutParams()).setDefaultTop(noScrollTop);
            }
            nextTop = bottom;
            noScrollTop = noScrollBottom;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainSlidingPane);
        setMaxSlidingUpLength(a.getDimensionPixelSize(R.styleable.MainSlidingPane_maxSlidingUpLength, 0));
        setMaxSlidingDownLength(a.getDimensionPixelSize(R.styleable.MainSlidingPane_maxSlidingDownLength, 0));
        a.recycle();
    }

    public int getDragState() {
        return mDragHelper.getViewDragState();
    }

    public void setMaxSlidingUpLength(int maxSlidingUpLength) {
        mMaxSlidingUpLength = maxSlidingUpLength;
    }

    public void setMaxSlidingDownLength(int maxSlidingDownLength) {
        mMaxSlidingDownLength = maxSlidingDownLength;
    }

    public int getMaxSlidingUpLength() {
        return mMaxSlidingUpLength;
    }

    public int getMaxSlidingDownLength() {
        return mMaxSlidingDownLength;
    }

    public boolean isScrolledUp() {
        View content = getContent();
        return content.getTop() <= ((LayoutParams) content.getLayoutParams()).getDefaultTop() - mMaxSlidingUpLength;
    }

    public boolean isScrolledDown() {
        View content = getContent();
        return content.getTop() >= ((LayoutParams) content.getLayoutParams()).getDefaultTop() + mMaxSlidingDownLength;
    }

    public View getContent() {
        for (int i = 0, j = getChildCount(); i < j; i++) {
            final View child = getChildAt(i);
            if (((LayoutParams) child.getLayoutParams()).isContent()) {
                return child;
            }
        }
        return null;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    public void scrollToDefault() {
        final View content = getContent();
        LayoutParams lp = (LayoutParams) content.getLayoutParams();
        mDragHelper.smoothSlideViewTo(content, 0, lp.getDefaultTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public interface ScrollListener {
        void onScroll(int currentTop, int defaultTop, int delta);

        void onDragStateChanged(int state);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        private boolean mIsContent;
        private int mDefaultTop;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.MainSlidingPane_LayoutParams);
            mIsContent = a.getBoolean(R.styleable.MainSlidingPane_LayoutParams_layout_isContent, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                mIsContent = ((LayoutParams) source).isContent();
            }
        }

        public boolean isContent() {
            return mIsContent;
        }

        public void setContent(boolean content) {
            mIsContent = content;
        }

        public int getDefaultTop() {
            return mDefaultTop;
        }

        public void setDefaultTop(int defaultTop) {
            mDefaultTop = defaultTop;
        }
    }
}
