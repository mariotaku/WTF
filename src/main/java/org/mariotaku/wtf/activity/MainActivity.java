package org.mariotaku.wtf.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;

import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.wtf.Constants;
import org.mariotaku.wtf.R;
import org.mariotaku.wtf.fragment.UserInfoFragment;
import org.mariotaku.wtf.graphic.drawable.TriangleDrawable;
import org.mariotaku.wtf.loader.TestUsersLoader;
import org.mariotaku.wtf.util.ViewSupport;
import org.mariotaku.wtf.view.MainSlidingPane;

import java.io.IOException;
import java.util.List;

/**
 * Created by mariotaku on 16/4/24.
 */
public class MainActivity extends AppCompatActivity implements Constants,
        LoaderManager.LoaderCallbacks<List<User>> {
    private ViewPager mViewPager;
    private RecyclerView mUsersGrid;
    private MainSlidingPane mSlidingPane;
    private View mMenuFriendsContainer;
    private View mMenuBar;
    private View mTriangleIndicator;
    private ImageView mAccountProfileImageView;

    private UserPagesAdapter mPagerAdapter;
    private UsersAdapter mUsersAdapter;
    private ParcelableCredentials mAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            final String json = preferences.getString(KEY_ACCOUNT, null);
            if (!TextUtils.isEmpty(json)) {
                mAccount = LoganSquare.parse(json, ParcelableCredentials.class);
            }
        } catch (IOException e) {
            // Ignore
        }
        if (mAccount == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        mPagerAdapter = new UserPagesAdapter(this, mAccount, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        final GridLayoutManager layout = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        mUsersGrid.setLayoutManager(layout);
        mUsersAdapter = new UsersAdapter(this);
        mUsersAdapter.setItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mViewPager.setCurrentItem(position);
                mSlidingPane.scrollToDefault();
            }
        });
        mUsersGrid.setAdapter(mUsersAdapter);

        final Resources resources = getResources();
        final TriangleDrawable drawable = new TriangleDrawable(resources.getDimensionPixelOffset(R.dimen.element_spacing_normal), resources.getDimensionPixelOffset(R.dimen.element_spacing_small));
        ViewSupport.setBackground(mTriangleIndicator, drawable);
        drawable.setTriangleColor(ContextCompat.getColor(this, R.color.bg_color_info_pane));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final int spanCount = layout.getSpanCount();
                int colPosition = position % spanCount;
                final int gridsWidth = mUsersGrid.getWidth();
                int spanWidth = gridsWidth / spanCount;
                final float colOffset = colPosition + positionOffset;
                if (colOffset > spanCount - 1) {
                    mTriangleIndicator.setTranslationX(spanWidth / 2 + (gridsWidth - spanWidth) * (1 - positionOffset) - mTriangleIndicator.getWidth() / 2);
                } else {
                    mTriangleIndicator.setTranslationX(colOffset * spanWidth + spanWidth / 2 - mTriangleIndicator.getWidth() / 2);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSlidingPane.setScrollListener(new MainSlidingPane.ScrollListener() {
            @Override
            public void onScroll(int currentTop, int defaultTop) {
                if (currentTop < defaultTop) {
                    mTriangleIndicator.setTranslationY(Math.max((defaultTop - currentTop) / 2,
                            -drawable.getTriangleHeight()));
                } else {
                    mMenuFriendsContainer.setTranslationY(Math.min(currentTop - defaultTop - mMenuBar.getHeight(), 0));
                    mTriangleIndicator.setTranslationY(Math.max((currentTop - defaultTop) / 2,
                            -drawable.getTriangleHeight()));
                }
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);

        Glide.with(this)
                .load(mAccount.profile_image_url)
                .dontAnimate()
                .placeholder(R.color.bg_color_info_pane)
                .into(mAccountProfileImageView);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.info_pager);
        mUsersGrid = (RecyclerView) findViewById(R.id.users_grid);
        mSlidingPane = (MainSlidingPane) findViewById(R.id.sliding_pane);
        mMenuFriendsContainer = findViewById(R.id.menu_friends_container);
        mMenuBar = findViewById(R.id.menu_bar);
        mTriangleIndicator = findViewById(R.id.triangle_indicator);
        mAccountProfileImageView = (ImageView) mMenuBar.findViewById(R.id.profile_image);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new TestUsersLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        mUsersAdapter.setData(data);
        mPagerAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {
        mUsersAdapter.setData(null);
        mPagerAdapter.setData(null);
    }

    static class UserPagesAdapter extends FragmentPagerAdapter {

        private final Context mContext;
        private final ParcelableCredentials mAccount;
        private List<User> mData;

        public UserPagesAdapter(Context context, ParcelableCredentials account, FragmentManager fm) {
            super(fm);
            mContext = context;
            mAccount = account;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_USER, mData.get(position));
            args.putParcelable(EXTRA_ACCOUNT, mAccount);
            return Fragment.instantiate(mContext, UserInfoFragment.class.getName(), args);
        }

        @Override
        public int getCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        public void setData(List<User> data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    static class UsersAdapter extends RecyclerView.Adapter<UserGridViewHolder> {
        private final LayoutInflater mInflater;
        private final Context mContext;
        private List<User> mData;
        private OnItemClickListener mItemClickListener;

        UsersAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public UserGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserGridViewHolder(mContext, mItemClickListener,
                    mInflater.inflate(R.layout.grid_item_user, parent, false));
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

        public User getUser(int position) {
            if (mData == null) return null;
            return mData.get(position);
        }

        public void setItemClickListener(OnItemClickListener listener) {
            mItemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(int position);
        }
    }

    static class UserGridViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final ImageView profileImageView;

        public UserGridViewHolder(Context context, final UsersAdapter.OnItemClickListener listener,
                                  View itemView) {
            super(itemView);
            this.context = context;
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            if (listener != null) {
                profileImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getLayoutPosition());
                    }
                });
            }
        }

        public void displayUser(User user) {
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .dontAnimate()
                    .placeholder(R.color.bg_color_info_pane)
                    .into(profileImageView);
        }
    }
}
