package com.example.trabalhofinal.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CategoryModel implements Parcelable {
    private String id;
    private String name;
    private String coverUrl;
    private List<String> songs; // List of song IDs or URLs

    // Constructors, getters, setters, and Parcelable implementation
    // Constructor, getters, setters
    public CategoryModel() {
    }

    public CategoryModel(String id, String name, String coverUrl, List<String> songs) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.songs = songs;
    }

    protected CategoryModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        coverUrl = in.readString();
        songs = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(coverUrl);
        dest.writeStringList(songs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
}
