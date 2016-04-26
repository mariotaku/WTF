package org.mariotaku.wtf.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;

import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.wtf.R;
import org.mariotaku.wtf.util.DividerItemDecoration;
import org.mariotaku.wtf.util.UnitConvertUtils;
import org.mariotaku.wtf.util.Utils;
import org.mariotaku.wtf.view.NameView;
import org.mariotaku.wtf.view.ShortTimeView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by mariotaku on 16/4/26.
 */
public class UserInfoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadProgress;

    UserInfoAdapter mInfoAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);
        mInfoAdapter = new UserInfoAdapter(getContext());
        mRecyclerView.setAdapter(mInfoAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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

    static class UserInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_USER_INFO = 1;
        private static final int VIEW_TYPE_STATUS = 2;
        private final LayoutInflater mInflater;

        UserInfoAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
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
                    return new StatusViewHolder(view);
                }
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_USER_INFO: {
                    ((UserInfoViewHolder) holder).displaySample();
                    break;
                }
                case VIEW_TYPE_STATUS: {
                    ((StatusViewHolder) holder).displaySample();
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
            return 5;
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
            profileImageView.setImageResource(R.drawable.ic_profile_image_sample);
            nameView.setText(user.getName());
            screenNameView.setText(String.format(Locale.US, "@%s", user.getScreenName()));
            descriptionView.setText(user.getDescription());
            locationView.setText(user.getLocation());
            urlView.setText(user.getUrl());
            final Context context = itemView.getContext();
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

        public void displaySample() {
            final InputStream is = itemView.getResources().openRawResource(R.raw.user_bearkani);
            try {
                User user = LoganSquare.parse(is, User.class);
                displayUser(user);
            } catch (IOException e) {
            } finally {
                Utils.closeSilently(is);
            }
        }
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImageView;
        private final NameView nameView;
        private final ShortTimeView timeView;
        private final TextView textView;

        public StatusViewHolder(View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            nameView = (NameView) itemView.findViewById(R.id.name);
            timeView = (ShortTimeView) itemView.findViewById(R.id.time);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        public void displaySample() {
            profileImageView.setImageResource(R.drawable.ic_profile_image_sample);
            nameView.setName("kani 11/100");
            nameView.setScreenName("@baerkani");
            nameView.updateText();
            textView.setText(R.string.sample_status_text);
            timeView.setTime(System.currentTimeMillis());
        }
    }
}
