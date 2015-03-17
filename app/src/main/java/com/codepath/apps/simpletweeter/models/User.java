package com.codepath.apps.simpletweeter.models;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mng on 3/7/15.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable {
    @Column(name = "remoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String id;
    @Column(name = "name")
    public String name;
    @Column(name = "screenName")
    public String screenName;
    @Column(name = "profileImageUrl")
    public String profileImageUrl;
    @Column(name = "profileBackgroundImageUrl")
    public String profileBackgroundImageUrl;
    @Column(name = "description")
    public String description;
    @Column(name = "statusesCount")
    public int statusesCount;
    @Column(name = "followersCount")
    public int followersCount;
    @Column(name = "followingCount")
    public int followingCount;

    // Make sure to always define this constructor with no arguments
    public User() {
        super();
    }

    // Add a constructor that creates an object from the JSON response
    public User(JSONObject object) {
        super();

        try {
            this.id = object.getString("id_str");
            this.name = object.getString("name");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
            this.profileBackgroundImageUrl = object.getString("profile_background_image_url");
            this.description = object.getString("description");
            this.statusesCount = object.getInt("statuses_count");
            this.followersCount = object.getInt("followers_count");
            this.followingCount = object.getInt("listed_count");
            this.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeString(profileImageUrl);
        dest.writeString(profileBackgroundImageUrl);
        dest.writeString(description);
        dest.writeInt(statusesCount);
        dest.writeInt(followersCount);
        dest.writeInt(followingCount);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User getById(String userId) {
        return new Select()
                .from(User.class)
                .where("id = ?", userId)
                .executeSingle();
    }

    private User(Parcel in) {
        id = in.readString();
        name = in.readString();
        screenName = in.readString();
        profileImageUrl = in.readString();
        profileBackgroundImageUrl = in.readString();
        description = in.readString();
        statusesCount = in.readInt();
        followersCount = in.readInt();
        followingCount = in.readInt();
    }
}
