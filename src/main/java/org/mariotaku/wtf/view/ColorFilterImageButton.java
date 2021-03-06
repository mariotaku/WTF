package org.mariotaku.wtf.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import org.mariotaku.wtf.R;

public class ColorFilterImageButton extends ImageButton {

    private ColorStateList colorFilter;

    public ColorFilterImageButton(Context context) {
        super(context);
    }

    public ColorFilterImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, android.R.attr.imageButtonStyle);
    }

    public ColorFilterImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorFilterImageView, defStyle, 0);
        colorFilter = a.getColorStateList(R.styleable.ColorFilterImageView_colorFilter);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (colorFilter != null)
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.colorFilter = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        final int color = colorFilter.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }

}