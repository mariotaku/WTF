package org.mariotaku.wtf.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableAccount;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.wtf.util.ParcelableAccountUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariotaku on 16/4/28.
 */
public class FollowersLoader extends AbsUsersLoader {
    public FollowersLoader(Context context, ParcelableCredentials credentials) {
        super(context, credentials);
    }

    @Override
    protected List<User> getUsers(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials) throws TwitterException {
        Paging paging = new Paging();
        paging.count(200);
        List<User> users = new ArrayList<>();
//        List<User> current;
//        while (true) {
//            current = getFollowers(twitter, credentials, paging);
//            users.addAll(current);
//        }
        return users;
    }

    private List<User> getFollowers(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials,
                                    Paging paging) throws TwitterException {
        switch (ParcelableAccountUtils.getAccountType(credentials)) {
            case ParcelableAccount.Type.STATUSNET: {
                return twitter.getStatusesFollowersList(credentials.account_key.getId(), paging);
            }
            case ParcelableAccount.Type.FANFOU: {
                return twitter.getUsersFollowers(credentials.account_key.getId(), paging);
            }
            default: {
                return twitter.getFollowersList(credentials.account_key.getId(), paging);
            }
        }
    }

    @NonNull
    @Override
    protected String getCacheKey() {
        return "followers_" + getCredentials().account_key;
    }
}
