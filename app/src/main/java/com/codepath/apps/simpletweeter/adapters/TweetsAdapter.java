package com.codepath.apps.simpletweeter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweeter.R;
import com.codepath.apps.simpletweeter.helpers.ParseDateHelper;
import com.codepath.apps.simpletweeter.models.Tweet;
import com.codepath.apps.simpletweeter.models.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mng on 3/6/15.
 */
public class TweetsAdapter extends ArrayAdapter<Tweet> implements View.OnClickListener{

    private TweetsAdapterListener listener;

    public interface TweetsAdapterListener {
        public void onReplyTweet(Tweet tweet);
        public void onRetweet(Tweet tweet);
        public void onFavoriteTweet(Tweet tweet);
        public void onViewUserProfile(User user);
    }

    private static class ViewHolder {
        ImageView ivUserImage;
        TextView tvUserName;
        TextView tvUserHandler;
        TextView tvDescription;
        TextView tvTimestamp;
        ImageButton ibReply;
        TextView tvRetweetCount;
        TextView tvFavoriteCount;
    }

    public TweetsAdapter(Context context, List<Tweet> tweets, TweetsAdapterListener listener) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        this.listener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Tweet tweet = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivUserImage = (ImageView)convertView.findViewById(R.id.ivUserImage);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvUserHandler = (TextView)convertView.findViewById(R.id.tvUserHandle);
            viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
            viewHolder.tvTimestamp = (TextView)convertView.findViewById(R.id.tvTimestamp);
            viewHolder.ibReply = (ImageButton)convertView.findViewById(R.id.ibReply);
            viewHolder.tvRetweetCount = (TextView)convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.tvFavoriteCount = (TextView)convertView.findViewById(R.id.tvFavoriteCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvUserName.setText(tweet.user.name);
        viewHolder.tvUserHandler.setText(tweet.user.screenName);
        viewHolder.tvDescription.setText(tweet.text);
        viewHolder.tvTimestamp.setText(ParseDateHelper.getRelativeTimeAgo(tweet.timeCreated));
        viewHolder.ibReply.setTag(position);
        viewHolder.ibReply.setOnClickListener(this);
        viewHolder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        viewHolder.tvRetweetCount.setTag(position);
        viewHolder.tvRetweetCount.setOnClickListener(this);
        viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        viewHolder.tvFavoriteCount.setTag(position);
        viewHolder.tvFavoriteCount.setOnClickListener(this);
        Picasso.with(getContext())
                .load(tweet.user.profileImageUrl)
                .into(viewHolder.ivUserImage);
        viewHolder.ivUserImage.setTag(position);
        viewHolder.ivUserImage.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        int position = (int)v.getTag();
        Tweet tweet = getItem(position);
        if (v.getId() == R.id.ibReply) {
            listener.onReplyTweet(tweet);
        } else if (v.getId() == R.id.tvRetweetCount) {
            listener.onRetweet(tweet);
        } else if (v.getId() == R.id.tvFavoriteCount) {
            listener.onFavoriteTweet(tweet);
        } else if (v.getId() == R.id.ivUserImage) {
            listener.onViewUserProfile(tweet.user);
        }
    }

    public void setFavoritedStatus(boolean favorited, int position) {

    }
}
