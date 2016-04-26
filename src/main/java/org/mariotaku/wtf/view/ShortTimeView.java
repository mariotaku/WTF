/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.wtf.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import org.mariotaku.wtf.R;
import org.mariotaku.wtf.util.Utils;

import java.lang.ref.WeakReference;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class ShortTimeView extends AppCompatTextView {

    private static final long TICKER_DURATION = 5000L;

    private final Runnable mTicker;
    private boolean mShowAbsoluteTime;
    private long mTime;

    public ShortTimeView(final Context context) {
        this(context, null);
    }

    public ShortTimeView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ShortTimeView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mTicker = new TickerRunnable(this);
    }

    public void setShowAbsoluteTime(boolean showAbsoluteTime) {
        mShowAbsoluteTime = showAbsoluteTime;
        invalidateTime();
    }

    public void setTime(final long time) {
        mTime = time;
        invalidateTime();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mTicker);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mTicker);
        super.onDetachedFromWindow();
    }

    private void invalidateTime() {
        if (mShowAbsoluteTime) {
            setText(Utils.formatSameDayTime(getContext(), mTime));
        } else {
            final long current = System.currentTimeMillis();
            if (Math.abs(current - mTime) > 60 * 1000) {
                setText(getRelativeTimeSpanString(mTime, System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
            } else {
                setText(R.string.just_now);
            }
        }
    }

    private static class TickerRunnable implements Runnable {

        private final WeakReference<ShortTimeView> mViewRef;

        private TickerRunnable(final ShortTimeView view) {
            mViewRef = new WeakReference<>(view);
        }

        @Override
        public void run() {
            final ShortTimeView view = mViewRef.get();
            if (view == null) return;
            final Handler handler = view.getHandler();
            if (handler == null) return;
            view.invalidateTime();
            final long now = SystemClock.uptimeMillis();
            final long next = now + TICKER_DURATION - now % TICKER_DURATION;
            handler.postAtTime(this, next);
        }
    }

}
