package com.codepath.apps.simpletweeter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweeter.R;
import com.codepath.apps.simpletweeter.models.User;
import com.squareup.picasso.Picasso;

/**
 * Created by mng on 3/14/15.
 */
public class UserDetailFragment extends Fragment {
    private User user;
    private static final String ARG_USER = "ARG_USER";

    public static UserDetailFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        UserDetailFragment fragment = new UserDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(ARG_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inf.inflate(R.layout.fragment_profile_detail, parent, false);

        ImageView ivUserProfileBackgroundImage = (ImageView)v.findViewById(R.id.ivUserProfileBackgroundImage);
        Picasso.with(getActivity())
               .load(user.profileBackgroundImageUrl)
               .into(ivUserProfileBackgroundImage);
        TextView tvUserDescription = (TextView)v.findViewById(R.id.tvUserDescription);
        tvUserDescription.setText(user.description);
        TextView tvScreenName= (TextView)v.findViewById(R.id.tvScreenName);
        tvScreenName.setText("@" + user.screenName);
        ImageView ivUserProfileImage = (ImageView)v.findViewById(R.id.ivUserProfileImage);
        Picasso.with(getActivity())
                .load(user.profileImageUrl)
                .into(ivUserProfileImage);
        TextView tvTweets = (TextView)v.findViewById(R.id.tvTweets);
        tvTweets.setText(String.valueOf(user.statusesCount) + "\nTWEETS");
        TextView tvFollowerCount = (TextView)v.findViewById(R.id.tvFollowerCount);
        tvFollowerCount.setText(String.valueOf(user.followersCount) + "\nFOLLOWERS");
        TextView tvFollowingCount = (TextView)v.findViewById(R.id.tvFollowingCount);
        tvFollowingCount.setText(String.valueOf(user.followingCount) + "\nFOLLOWING");
        return v;
    }
}
