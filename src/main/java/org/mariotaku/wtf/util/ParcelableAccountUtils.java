package org.mariotaku.wtf.util;

import android.support.annotation.NonNull;

import org.mariotaku.twidere.model.ParcelableAccount;

/**
 * Created by mariotaku on 16/4/28.
 */
public class ParcelableAccountUtils {

    @NonNull
    @ParcelableAccount.Type
    public static String getAccountType(@NonNull ParcelableAccount account) {
        if (account.account_type == null) return ParcelableAccount.Type.TWITTER;
        return account.account_type;
    }
}
