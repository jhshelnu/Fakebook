package com.restclient.containers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Container class to store User info
 */
public class User implements Parcelable {

    private final int userId;
    private final String username;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String website;
    private final LatLng location;

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(int userId, String username, String name, String email, String phoneNumber, String website, LatLng location) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.location = location;
    }

    private User(Parcel in) {
        userId = in.readInt();
        username = in.readString();
        name = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        website = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public LatLng getLocation() {
        return location;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeString(username);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phoneNumber);
        parcel.writeString(website);
        parcel.writeParcelable(location, i);
    }
}
