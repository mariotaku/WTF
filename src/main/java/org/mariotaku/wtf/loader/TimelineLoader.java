package org.mariotaku.wtf.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.wtf.util.TwitterAPIFactory;

import java.util.List;

/**
 * Created by mariotaku on 16/4/28.
 */
public class TimelineLoader extends AsyncTaskLoader<List<Status>> {
    private final ParcelableCredentials mCredentials;
    private final User mUser;
    private List<Status> mData;

    public TimelineLoader(Context context, ParcelableCredentials credentials, User user) {
        super(context);
        mCredentials = credentials;
        mUser = user;
    }

    @Override
    public List<Status> loadInBackground() {
        Twitter twitter = TwitterAPIFactory.getTwitterInstance(getContext(), mCredentials);
        Paging paging = new Paging();
        paging.setCount(30);
        try {
            return twitter.getUserTimeline(mUser.getId(), paging);
        } catch (TwitterException e) {
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Status> data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
