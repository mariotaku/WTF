package org.mariotaku.wtf.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.wtf.R;
import org.mariotaku.wtf.fragment.UserInfoFragment;
import org.mariotaku.wtf.loader.TestUsersLoader;

import java.util.List;

/**
 * Created by mariotaku on 16/4/24.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<User>> {
    private UserInfoAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private RecyclerView mUsersGrid;
    private UsersAdapter mUsersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPagerAdapter = new UserInfoAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mUsersGrid.setLayoutManager(new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false));
        mUsersAdapter = new UsersAdapter(this);
        mUsersGrid.setAdapter(mUsersAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.info_pager);
        mUsersGrid = (RecyclerView) findViewById(R.id.users_grid);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new TestUsersLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        mUsersAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {
        mUsersAdapter.setData(null);
    }

    static class UserInfoAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        public UserInfoAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            return Fragment.instantiate(mContext, UserInfoFragment.class.getName(), args);
        }

        @Override
        public int getCount() {
            return 100;
        }
    }

    static class UsersAdapter extends RecyclerView.Adapter<UserGridViewHolder> {
        private final LayoutInflater mInflater;
        private final Context mContext;
        private List<User> mData;

        UsersAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public UserGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserGridViewHolder(mContext, mInflater.inflate(R.layout.grid_item_user, parent, false));
        }

        @Override
        public void onBindViewHolder(UserGridViewHolder holder, int position) {
            holder.displayUser(mData.get(position));
        }

        public void setData(List<User> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }
    }

    static class UserGridViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final ImageView profileImageView;

        public UserGridViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        }

        public void displayUser(User user) {
            Glide.with(context).load(user.getProfileImageUrl()).dontAnimate().placeholder(R.color.bg_color_info_pane).into(profileImageView);
        }
    }
}
