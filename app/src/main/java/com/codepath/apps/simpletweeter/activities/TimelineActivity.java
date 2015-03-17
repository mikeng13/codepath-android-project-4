package com.codepath.apps.simpletweeter.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletweeter.R;
import com.codepath.apps.simpletweeter.RestApplication;
import com.codepath.apps.simpletweeter.RestClient;
import com.codepath.apps.simpletweeter.adapters.TweeterPagerAdapter;
import com.codepath.apps.simpletweeter.adapters.TweetsAdapter;
import com.codepath.apps.simpletweeter.dialogs.PostTweetDialog;
import com.codepath.apps.simpletweeter.fragments.TweetListFragment;
import com.codepath.apps.simpletweeter.helpers.EndlessScrollListener;
import com.codepath.apps.simpletweeter.models.Tweet;
import com.codepath.apps.simpletweeter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TimelineActivity extends ActionBarActivity implements PostTweetDialog.PostTweetDialogListener, TweetListFragment.TweetListFragmentListener{
    private static final int POST_TWEET_REQUEST_CODE = 341;

    private TweetListFragment tweetListFragment;
    private User user;
    private RestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // add tweeter icon to actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.tweeter_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        client = RestApplication.getRestClient();
        getUserProfile();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweeterPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "user: " + jsonObject.toString());
                user = new User(jsonObject);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_tweet) {
            FragmentManager fm = getSupportFragmentManager();
            PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, null);
            postTweetDialog.show(fm, "fragment_post_tweet");
        } else if (id == R.id.action_profile) {
            showUserProfile(user);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPost(Tweet tweet) {
        if (tweet != null) {
            // pass tweet to tweet list fragment
            tweetListFragment.insertTweet(tweet);
        }
    }

    @Override
    public void onReplyTweet(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, tweet);
        postTweetDialog.show(fm, "fragment_reply_tweet");
    }

    @Override
    public void onViewUserProfile(User user) {
        if (user != null) {
            showUserProfile(user);
        }
    }

    private void showUserProfile(User user) {
        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        userProfileIntent.putExtra("user", user);
        startActivity(userProfileIntent);
    }
}
