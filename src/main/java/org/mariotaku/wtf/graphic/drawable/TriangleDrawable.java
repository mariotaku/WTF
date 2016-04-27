package org.mariotaku.wtf.graphic.drawable;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Created by mariotaku on 16/4/27.
 */
public class TriangleDrawable extends Drawable {
    private int mTriangleWidth, mTriangleHeight;
    private final Paint mPaint;
    private final Path mTrianglePath;

    public TriangleDrawable(int triangleWidth, int triangleHeight) {
        this.mTriangleWidth = triangleWidth;
        this.mTriangleHeight = triangleHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mTrianglePath = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mTrianglePath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateTriangle();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@NonNull Outline outline) {
        outline.setConvexPath(mTrianglePath);
    }

    @Override
    public int getIntrinsicWidth() {
        return mTriangleWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mTriangleHeight;
    }

    public void setTriangleColor(@ColorInt int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    private void updateTriangle() {
        Rect bounds = getBounds();
        float left = 0;
        float bottom = bounds.bottom;
        mTrianglePath.reset();
        mTrianglePath.moveTo(left, bottom);
        mTrianglePath.lineTo(left + mTriangleWidth / 2, bottom - mTriangleHeight);
        mTrianglePath.lineTo(left + mTriangleWidth, bottom);
        mTrianglePath.close();
    }

    public int getTriangleWidth() {
        return mTriangleWidth;
    }

    public int getTriangleHeight() {
        return mTriangleHeight;
    }
}
