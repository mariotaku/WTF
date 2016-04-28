package org.mariotaku.wtf.util;

/**
 * Created by mariotaku on 16/4/28.
 */
public class MathUtils {

    public static float clamp(final float num, final float bound1, final float bound2) {
        final float max = Math.max(bound1, bound2), min = Math.min(bound1, bound2);
        return Math.max(Math.min(num, max), min);
    }

}
