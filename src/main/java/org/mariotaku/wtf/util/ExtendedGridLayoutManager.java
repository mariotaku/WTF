package org.mariotaku.wtf.util;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by mariotaku on 16/4/28.
 */
public class ExtendedGridLayoutManager extends GridLayoutManager {
    private boolean mVerticalScrollEnabled, mHorizontalScrollEnabled;

    public ExtendedGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ExtendedGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public ExtendedGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setVerticalScrollEnabled(boolean verticalScrollEnabled) {
        mVerticalScrollEnabled = verticalScrollEnabled;
    }

    public void setHorizontalScrollEnabled(boolean horizontalScrollEnabled) {
        mHorizontalScrollEnabled = horizontalScrollEnabled;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mHorizontalScrollEnabled && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return mVerticalScrollEnabled && super.canScrollVertically();
    }
}
