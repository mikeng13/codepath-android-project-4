package com.codepath.apps.simpletweeter.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.codepath.apps.simpletweeter.RestClient;
import com.codepath.apps.simpletweeter.enums.TweetType;
import com.codepath.apps.simpletweeter.helpers.ParseDateHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mng on 3/6/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {
    // Define database columns and associated fields
    @Column(name = "remoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String id;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;
    @Column(name = "isMention")
    public int isMention;
    @Column(name = "timeCreated")
    public String timeCreated;
    @Column(name = "timestamp", index = true)
    private Date timestamp;
    @Column(name = "text")
    public String text;
    @Column(name = "retweetCount")
    public int retweetCount;
    @Column(name = "favoriteCount")
    public int favoriteCount;
    public boolean favorited;

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }

    // Add a constructor that creates an object from the JSON response
    public Tweet(JSONObject object){
        super();

        try {
            this.id = object.getString("id_str");
            JSONObject userJSONObject = object.getJSONObject("user");
            this.user = new User(userJSONObject);
            this.timeCreated = object.getString("created_at");
            this.timestamp = ParseDateHelper.parseDate(this.timeCreated);
            this.text = object.getString("text");
            this.retweetCount = object.getInt("retweet_count");
            this.favoriteCount = object.getInt("favorite_count");
            this.favorited = object.getBoolean("favorited");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray, int type) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.isMention = type == TweetType.MENTIONS.ordinal() ? 1 : 0;
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }

    public static List<Tweet> getAll(int type) {
        if (type == TweetType.MENTIONS.ordinal()) {
            return new Select()
                    .from(Tweet.class)
                    .where("isMention = ?", 1)
                    .orderBy("timestamp DESC")
                    .execute();
        } else {
            return new Select()
                    .from(Tweet.class)
                    .orderBy("timestamp DESC")
                    .execute();
        }
    }

    public static List<Tweet> getUserTweets(String userId) {
        return new Select()
                .from(Tweet.class)
                .where("User = ?", userId)
                .orderBy("timestamp DESC")
                .execute();
    }

    public static void deleteAll() {
        new Delete()
                .from(Tweet.class)
                .execute();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(timeCreated);
        dest.writeString(text);
        dest.writeLong(timestamp.getTime());
        dest.writeInt(retweetCount);
        dest.writeInt(favoriteCount);
        dest.writeInt(favorited ? 1 : 0);
    }

    public static final Parcelable.Creator<Tweet> CREATOR
            = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    private Tweet(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        timeCreated = in.readString();
        text = in.readString();
        timestamp = new Date(in.readLong());
        retweetCount = in.readInt();
        favoriteCount = in.readInt();
        favorited = in.readInt() == 1;
    }
}
