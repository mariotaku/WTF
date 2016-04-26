package org.mariotaku.wtf.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by mariotaku on 16/4/26.
 */
public class NestedScrollingFrameLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild {
    public NestedScrollingFrameLayout(Context context) {
        super(context);
    }

    public NestedScrollingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NestedScrollingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
