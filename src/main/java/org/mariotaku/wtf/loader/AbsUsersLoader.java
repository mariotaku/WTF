package org.mariotaku.wtf.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.bluelinelabs.logansquare.LoganSquare;

import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.wtf.util.TwitterAPIFactory;
import org.mariotaku.wtf.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okio.ByteString;

/**
 * Created by mariotaku on 16/4/28.
 */
public abstract class AbsUsersLoader extends AsyncTaskLoader<List<User>> {
    private final ParcelableCredentials mCredentials;

    public AbsUsersLoader(Context context, ParcelableCredentials credentials) {
        super(context);
        mCredentials = credentials;
    }

    @Override
    public List<User> loadInBackground() {
        if (mCredentials == null) return null;
        Twitter twitter = TwitterAPIFactory.getTwitterInstance(getContext(), mCredentials);
        if (readCache()) {
            FileInputStream is = null;
            try {
                final File cacheFile = getCacheFile();
                is = new FileInputStream(cacheFile);
                List<User> cached = LoganSquare.parseList(is, User.class);
                if (cached != null) return cached;
            } catch (IOException e) {
                // Ignore
            } finally {
                Utils.closeSilently(is);
            }
        }
        try {
            List<User> fetched = getUsers(twitter, mCredentials);
            if (writeCache()) {
                FileOutputStream os = null;
                try {
                    final File cacheFile = getCacheFile();
                    os = new FileOutputStream(cacheFile);
                    LoganSquare.serialize(fetched, os);
                } catch (IOException e) {
                    // Ignore
                } finally {
                    Utils.closeSilently(os);
                }
            }
        } catch (TwitterException e) {
            // Ignore
        }
        return null;
    }

    protected abstract List<User> getUsers(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials)
            throws TwitterException;

    protected boolean readCache() {
        return true;
    }

    protected boolean writeCache() {
        return true;
    }

    public ParcelableCredentials getCredentials() {
        return mCredentials;
    }

    @NonNull
    protected abstract String getCacheKey();

    private File getCacheFile() throws IOException {
        File cacheDir = getContext().getCacheDir();
        return new File(cacheDir, ByteString.encodeUtf8(getCacheKey()).sha256().hex());
    }
}
