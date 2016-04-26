package org.mariotaku.wtf.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedType;

import org.mariotaku.twidere.api.twitter.model.PageableResponseList;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.wtf.R;
import org.mariotaku.wtf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by mariotaku on 16/4/26.
 */
public class TestUsersLoader extends AsyncTaskLoader<List<User>> {
    public TestUsersLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        JsonMapper<PageableResponseList<User>> mapper = LoganSquare.mapperFor(new ParameterizedType<PageableResponseList<User>>() {
        });
        InputStream is = null;
        try {
            is = getContext().getResources().openRawResource(R.raw.followers);
            return mapper.parse(is);
        } catch (IOException e) {
            return null;
        } finally {
            Utils.closeSilently(is);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
