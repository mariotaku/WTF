package org.mariotaku.wtf.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.wtf.Constants;
import org.mariotaku.wtf.R;
import org.mariotaku.wtf.loader.TimelineLoader;
import org.mariotaku.wtf.util.DividerItemDecoration;
import org.mariotaku.wtf.util.UnitConvertUtils;
import org.mariotaku.wtf.util.Utils;
import org.mariotaku.wtf.view.NameView;
import org.mariotaku.wtf.view.ShortTimeView;

import java.util.List;
import java.util.Locale;

/**
 * Created by mariotaku on 16/4/26.
 */
public class UserInfoFragment extends Fragment implements Constants,
        LoaderManager.LoaderCallbacks<List<Status>> {

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadProgress;

    UserInfoAdapter mInfoAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Bundle args = getArguments();
        final User user = args.getParcelable(EXTRA_USER);
        final ParcelableCredentials account = args.getParcelable(EXTRA_ACCOUNT);
        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);
        mInfoAdapter = new UserInfoAdapter(getContext(), account);
        mRecyclerView.setAdapter(mInfoAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        mInfoAdapter.setUser(user);
        getLoaderManager().initLoader(0, args, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLoadProgress = (ProgressBar) view.findViewById(R.id.load_progress);
    }

    @Override
    public Loader<List<Status>> onCreateLoader(int id, Bundle args) {
        final User user = args.getParcelable(EXTRA_USER);
        final ParcelableCredentials account = args.getParcelable(EXTRA_ACCOUNT);
        return new TimelineLoader(getContext(), account, user);
    }

    @Override
    public void onLoadFinished(Loader<List<Status>> loader, List<Status> data) {
        mInfoAdapter.setStatuses(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Status>> loader) {
        mInfoAdapter.setStatuses(null);
    }

    static class UserInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_USER_INFO = 1;
        private static final int VIEW_TYPE_STATUS = 2;
        private final LayoutInflater mInflater;
        private final ParcelableCredentials mAccount;
        private User mUser;
        private List<Status> mStatuses;

        UserInfoAdapter(Context context, ParcelableCredentials account) {
            mInflater = LayoutInflater.from(context);
            mAccount = account;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_USER_INFO: {
                    final View view = mInflater.inflate(R.layout.header_item_user_profile, parent,
                            false);
                    return new UserInfoViewHolder(view);
                }
                case VIEW_TYPE_STATUS: {
                    final View view = mInflater.inflate(R.layout.list_item_status, parent, false);
                    return new StatusViewHolder(view, this);
                }
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_USER_INFO: {
                    ((UserInfoViewHolder) holder).displayUser(mUser);
                    break;
                }
                case VIEW_TYPE_STATUS: {
                    ((StatusViewHolder) holder).displayStatus(getStatus(position));
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_USER_INFO;
            } else {
                return VIEW_TYPE_STATUS;
            }
        }

        @Override
        public int getItemCount() {
            if (mUser == null) {
                return 0;
            }
            if (mStatuses == null) {
                return 1;
            }
            return mStatuses.size() + 1;
        }

        public void setUser(@Nullable User user) {
            mUser = user;
            notifyDataSetChanged();
        }

        public void setStatuses(List<Status> statuses) {
            mStatuses = statuses;
            notifyDataSetChanged();
        }

        public Status getStatus(int position) {
            if (position == 0) return null;
            return mStatuses.get(position - 1);
        }

        public ParcelableCredentials getAccount() {
            return mAccount;
        }
    }

    static class UserInfoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImageView;
        private final TextView nameView, screenNameView;
        private final TextView descriptionView;
        private final View locationContainer, urlContainer;
        private final TextView locationView, urlView, createdAtView;
        private final TextView friendsCountView, followersCountView, favoritesCountView,
                listedCountView, groupsCountView;
        private final TextView statusesCountView;
        private final View listedContainer, groupsContainer;

        public UserInfoViewHolder(View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            nameView = (TextView) itemView.findViewById(R.id.name);
            screenNameView = (TextView) itemView.findViewById(R.id.screen_name);
            descriptionView = (TextView) itemView.findViewById(R.id.description);
            locationContainer = itemView.findViewById(R.id.location_container);
            urlContainer = itemView.findViewById(R.id.url_container);
            locationView = (TextView) itemView.findViewById(R.id.location);
            urlView = (TextView) itemView.findViewById(R.id.url);
            createdAtView = (TextView) itemView.findViewById(R.id.created_at);
            listedContainer = itemView.findViewById(R.id.listed_container);
            groupsContainer = itemView.findViewById(R.id.groups_container);
            friendsCountView = (TextView) itemView.findViewById(R.id.friends_count);
            followersCountView = (TextView) itemView.findViewById(R.id.followers_count);
            favoritesCountView = (TextView) itemView.findViewById(R.id.favorites_count);
            listedCountView = (TextView) itemView.findViewById(R.id.listed_count);
            groupsCountView = (TextView) itemView.findViewById(R.id.groups_count);
            statusesCountView = (TextView) itemView.findViewById(R.id.status_count);
        }

        public void displayUser(User user) {
            final Context context = itemView.getContext();
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .dontAnimate()
                    .placeholder(R.color.bg_color_info_pane)
                    .into(profileImageView);
            nameView.setText(user.getName());
            screenNameView.setText(String.format(Locale.US, "@%s", user.getScreenName()));
            descriptionView.setText(user.getDescription());
            descriptionView.setVisibility(TextUtils.isEmpty(user.getDescription()) ? View.GONE : View.VISIBLE);
            locationView.setText(user.getLocation());
            urlView.setText(user.getUrl());
            final Resources resources = context.getResources();
            createdAtView.setText(Utils.formatSameDayTime(context, user.getCreatedAt().getTime()));

            locationContainer.setVisibility(TextUtils.isEmpty(user.getLocation()) ? View.GONE : View.VISIBLE);
            urlContainer.setVisibility(TextUtils.isEmpty(user.getUrl()) ? View.GONE : View.VISIBLE);

            listedContainer.setVisibility(user.getListedCount() < 0 ? View.GONE : View.VISIBLE);
            groupsContainer.setVisibility(user.getGroupsCount() < 0 ? View.GONE : View.VISIBLE);

            friendsCountView.setText(UnitConvertUtils.calculateProperCount(user.getFriendsCount()));
            followersCountView.setText(UnitConvertUtils.calculateProperCount(user.getFollowersCount()));
            favoritesCountView.setText(UnitConvertUtils.calculateProperCount(user.getFavouritesCount()));
            listedCountView.setText(UnitConvertUtils.calculateProperCount(user.getListedCount()));
            groupsCountView.setText(UnitConvertUtils.calculateProperCount(user.getGroupsCount()));

            statusesCountView.setText(resources.getQuantityString(R.plurals.Nstatuses,
                    (int) user.getStatusesCount(), UnitConvertUtils.calculateProperCount(user.getStatusesCount())));
        }

    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImageView;
        private final NameView nameView;
        private final ShortTimeView timeView;
        private final TextView textView;
        private final TextView retweetIndicator;

        public StatusViewHolder(final View itemView, final UserInfoAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ParcelableCredentials account = adapter.getAccount();
                    final Status status = adapter.getStatus(getLayoutPosition());
                    Uri link = Utils.getStatusWebLink(status, account.account_type);
                    final Context context = itemView.getContext();
                    final Intent intent = new Intent(Intent.ACTION_VIEW, link);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    ComponentName name = intent.resolveActivity(context.getPackageManager());
                    if (name != null && TextUtils.equals(Twidere.TWIDERE_PACKAGE_NAME, name.getPackageName())) {
                        intent.putExtra(Twidere.EXTRA_ACCOUNT_KEY, account.account_key);
                    }
                    context.startActivity(intent);
                }
            });
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            nameView = (NameView) itemView.findViewById(R.id.name);
            timeView = (ShortTimeView) itemView.findViewById(R.id.time);
            textView = (TextView) itemView.findViewById(R.id.text);
            retweetIndicator = (TextView) itemView.findViewById(R.id.retweet_indicator);
        }

        public void displayStatus(Status status) {
            final User user = status.getUser();

            Status displaying = status;
            if (status.isRetweet()) {
                displaying = status.getRetweetedStatus();
                retweetIndicator.setText(itemView.getContext().getString(R.string.retweeted_by_name,
                        user.getName()));
                retweetIndicator.setVisibility(View.VISIBLE);
            } else {
                retweetIndicator.setVisibility(View.GONE);
            }

            final Context context = itemView.getContext();
            Glide.with(context)
                    .load(displaying.getUser().getProfileImageUrl())
                    .dontAnimate()
                    .placeholder(R.color.bg_color_info_pane)
                    .into(profileImageView);
            nameView.setName(displaying.getUser().getName());
            nameView.setScreenName("@" + displaying.getUser().getScreenName());
            nameView.updateText();
            textView.setText(displaying.getText());
            timeView.setTime(displaying.getCreatedAt().getTime());

        }
    }
}
