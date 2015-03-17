package com.codepath.apps.simpletweeter.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.simpletweeter.R;
import com.codepath.apps.simpletweeter.helpers.ParseDateHelper;
import com.codepath.apps.simpletweeter.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends ActionBarActivity implements View.OnClickListener{

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tweet = getIntent().getParcelableExtra("tweet");
        TextView tvUserName = (TextView)findViewById(R.id.tvUserName);
        tvUserName.setText(tweet.user.name);
        TextView tvScreenName = (TextView)findViewById(R.id.tvScreenName);
        tvScreenName.setText(tweet.user.screenName);
        TextView tvBody = (TextView)findViewById(R.id.tvBody);
        tvBody.setText(tweet.text);
        TextView tvTimestamp = (TextView)findViewById(R.id.tvTimestamp);
        tvTimestamp.setText(ParseDateHelper.getPrettyTimeStamp(tweet.timeCreated));

        // default to invisible, only show if there are retweets and/or favorites
        View vLine2 = (View)findViewById(R.id.vLine2);
        vLine2.setVisibility(View.INVISIBLE);

        if (tweet.retweetCount > 0) {
            String retweetCountHtml = "<b>" + String.valueOf(tweet.retweetCount) +
                    "</b> <font color='#74363636'>RETWEETS</font>";
            TextView tvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
            tvRetweetCount.setText(Html.fromHtml(retweetCountHtml));
            vLine2.setVisibility(View.VISIBLE);
        }

        if (tweet.favoriteCount > 0) {
            String favoriteCountHtml = "<b>" + String.valueOf(tweet.favoriteCount) +
                    "</b> <font color='#74363636'>FAVORITES</font>";
            TextView tvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);
            tvFavoriteCount.setText(Html.fromHtml(favoriteCountHtml));
            vLine2.setVisibility(View.VISIBLE);
        }

        ImageView ivUserImage = (ImageView)findViewById(R.id.ivUserImage);
        ivUserImage.setOnClickListener(this);
        Picasso.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivUserImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivUserImage) {
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("user", tweet.user);
            startActivity(i);
        }
    }
}
