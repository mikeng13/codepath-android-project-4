package com.codepath.apps.simpletweeter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.apps.simpletweeter.R;
import com.codepath.apps.simpletweeter.RestApplication;
import com.codepath.apps.simpletweeter.RestClient;
import com.codepath.apps.simpletweeter.activities.TweetDetailActivity;
import com.codepath.apps.simpletweeter.adapters.TweetsAdapter;
import com.codepath.apps.simpletweeter.enums.TweetType;
import com.codepath.apps.simpletweeter.helpers.EndlessScrollListener;
import com.codepath.apps.simpletweeter.models.Tweet;
import com.codepath.apps.simpletweeter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mng on 3/11/15.
 */
public class TweetListFragment extends Fragment implements AdapterView.OnItemClickListener, TweetsAdapter.TweetsAdapterListener {
    private TweetsAdapter tweetsAdapter;
    private ListView lvTweets;
    private RestClient restClient;
    private ProgressBar progressBarFooter;
    private SwipeRefreshLayout swipeContainer;
    private int type;
    private User user;

    private static final String ARG_TYPE = "ARG_TYPE";
    private static final String ARG_USER = "ARG_USER";

    public static TweetListFragment newInstance(int type, User user) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putParcelable(ARG_USER, user);
        TweetListFragment fragment = new TweetListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface TweetListFragmentListener {
        public void onReplyTweet(Tweet tweet);
        public void onViewUserProfile(User user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_TYPE);
        user = getArguments().getParcelable(ARG_USER);

        restClient = RestApplication.getRestClient();

        List<Tweet> tweets;
        if (type == TweetType.USER.ordinal() && user != null) {
            tweets = Tweet.getUserTweets(user.id);
        } else {
            tweets = Tweet.getAll(type);
        }
        if (tweets.size() == 0) {
            populateTimeline(1);
        }
        tweetsAdapter = new TweetsAdapter(getActivity(), tweets, this);
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inf.inflate(R.layout.fragment_tweet_list, parent, false);

        // setup our list view
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        // Inflate the footer
        View footer = inf.inflate(
                R.layout.footer_tweet_list_progress, null);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)
                footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvTweets.addFooterView(footer);
        // Set the adapter AFTER adding footer
        lvTweets.setAdapter(tweetsAdapter);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page);
            }
        });
        lvTweets.setOnItemClickListener(this);

        // setup our swipe to refresh container
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    private void populateTimeline(final int page) {
        String userId = (type == TweetType.USER.ordinal() && user != null) ? user.id : null;
        restClient.getTimeline(page, type, userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                if (page == 0) {
                    tweetsAdapter.clear();
                }
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // Load json array into model classes
                tweetsAdapter.addAll(Tweet.fromJSON(jsonArray, type));
                tweetsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                if (progressBarFooter != null) {
                    progressBarFooter.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                if (progressBarFooter != null) {
                    progressBarFooter.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (progressBarFooter != null) {
            progressBarFooter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tweet tweet = tweetsAdapter.getItem(position);
        Intent i = new Intent(getActivity(), TweetDetailActivity.class);
        i.putExtra("tweet", tweet);
        startActivity(i);
    }

    @Override
    public void onReplyTweet(Tweet tweet) {
        TweetListFragmentListener tweetListFragmentListener = (TweetListFragmentListener)getActivity();
        tweetListFragmentListener.onReplyTweet(tweet);
    }

    @Override
    public void onRetweet(Tweet tweet) {
        restClient.postRetweet(tweet.id, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {

                Log.d("DEBUG", "retweet: " + jsonObject.toString());
                // Load json array into model classes
                Tweet retweetedTweet = new Tweet(jsonObject);
                retweetedTweet.save();

                tweetsAdapter.insert(retweetedTweet, 0);
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void onFavoriteTweet(final Tweet tweet) {
        restClient.postFavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "favorited: " + jsonObject.toString());

                Tweet retweetedTweet = new Tweet(jsonObject);
                retweetedTweet.save();
                // update the favorited status
                tweet.favorited = retweetedTweet.favorited;
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void onViewUserProfile(User user) {
        TweetListFragmentListener tweetListFragmentListener = (TweetListFragmentListener)getActivity();
        tweetListFragmentListener.onViewUserProfile(user);
    }

    public void insertTweet(Tweet tweet) {
        tweetsAdapter.insert(tweet, 0);
        tweetsAdapter.notifyDataSetChanged();
    }
}
