package com.example.metbit.article;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Article implements Parcelable {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("date")
    private String date;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("paragraphs")
    private List<String> paragraphs;

    public Long getId() {
        return id;
    }

    public String getTitle(String lang) {
        return title != null ? title : "";
    }

    public String getAuthor(String lang) {
        return author != null ? author : "";
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public List<String> getParagraphs() {
        return paragraphs != null ? paragraphs : new ArrayList<>();
    }

    // 构造函数（可选）
    public Article(String title, String author, String date, String imageUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    // Parcelable 实现
    protected Article(Parcel in) {
        title = in.readString();
        author = in.readString();
        date = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(date);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}