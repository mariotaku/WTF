package org.mariotaku.wtf.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mariotaku on 16/4/26.
 */
public class ScrollAwareViewPager extends ViewPager implements NestedScrollingChild {
    public ScrollAwareViewPager(Context context) {
        super(context);
    }

    public ScrollAwareViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return view.canScrollVertically(direction);
        }
        return false;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            ViewCompat.setNestedScrollingEnabled(view, enabled);
        }
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.isNestedScrollingEnabled(view);
        }
        return false;
    }

    @Override
    public boolean startNestedScroll(int axes) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            ViewCompat.startNestedScroll(view, axes);
        }
        return false;
    }

    @Override
    public void stopNestedScroll() {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            ViewCompat.stopNestedScroll(view);
        }
    }

    @Override
    public boolean hasNestedScrollingParent() {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.hasNestedScrollingParent(view);
        }
        return false;
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.dispatchNestedScroll(view, dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed, offsetInWindow);
        }
        return false;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.dispatchNestedPreScroll(view, dx, dy, consumed, offsetInWindow);
        }
        return false;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.dispatchNestedFling(view, velocityX, velocityY, consumed);
        }
        return false;
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        View view = findScrollingChild(findVisibleChild());
        if (view != null) {
            return ViewCompat.dispatchNestedPreFling(view, velocityX, velocityY);
        }
        return false;
    }

    private View findVisibleChild() {
        final PagerAdapter adapter = getAdapter();
        if (adapter == null || adapter.getCount() == 0) return null;
        Object item = adapter.instantiateItem(this, getCurrentItem());
        if (item instanceof Fragment) {
            return ((Fragment) item).getView();
        }
        return null;
    }

    private View findScrollingChild(View view) {
        if (view instanceof NestedScrollingChild) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }
}
