package org.mariotaku.wtf.util;

import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mariotaku on 16/4/28.
 */
public class RecyclerViewUtils {

    public static void smoothScrollToPositionWithOffset(RecyclerView recyclerView, int adapterPosition) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {

            LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    final PointF scrollVectorForPosition = ((LinearLayoutManager) layoutManager).computeScrollVectorForPosition(targetPosition);
                    return scrollVectorForPosition;
                }

            };

            scroller.setTargetPosition(adapterPosition);
            layoutManager.startSmoothScroll(scroller);

        }
    }
}
