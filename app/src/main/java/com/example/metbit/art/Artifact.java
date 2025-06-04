package com.example.metbit.art;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Artifact implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("titleEn") private String titleEn;
    @SerializedName("titleZh") private String titleZh;

    @SerializedName("periodEn") private String periodEn;
    @SerializedName("periodZh") private String periodZh;

    @SerializedName("cultureEn") private String cultureEn;
    @SerializedName("cultureZh") private String cultureZh;

    @SerializedName("descriptionEn") private String descriptionEn;
    @SerializedName("descriptionZh") private String descriptionZh;

    @SerializedName("imageUrl") private String imageUrl;
    @SerializedName("thumbnailUrl") private String thumbnailUrl;


    // 构造函数
    public Artifact(long id,
                    String titleEn, String titleZh,
                    String periodEn, String periodZh,
                    String cultureEn, String cultureZh,
                    String descriptionEn, String descriptionZh,
                    String imageUrl, String thumbnailUrl) {
        this.id = id;
        this.titleEn = titleEn;
        this.titleZh = titleZh;
        this.periodEn = periodEn;
        this.periodZh = periodZh;
        this.cultureEn = cultureEn;
        this.cultureZh = cultureZh;
        this.descriptionEn = descriptionEn;
        this.descriptionZh = descriptionZh;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    // 多语言 Getter

    public long getId() {
        return id;
    }
    public String getTitle(String lang) {
        return lang.equals("en") ? titleEn : titleZh;
    }

    public String getPeriod(String lang) {
        return lang.equals("en") ? periodEn : periodZh;
    }

    public String getCulture(String lang) {
        return lang.equals("en") ? cultureEn : cultureZh;
    }

    public String getDescription(String lang) {
        return lang.equals("en") ? descriptionEn : descriptionZh;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    // Parcelable实现
    protected Artifact(Parcel in) {
        id = in.readLong();
        titleEn = in.readString();
        titleZh = in.readString();
        periodEn = in.readString();
        periodZh = in.readString();
        cultureEn = in.readString();
        cultureZh = in.readString();
        descriptionEn = in.readString();
        descriptionZh = in.readString();
        imageUrl = in.readString();
        thumbnailUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titleEn);
        dest.writeString(titleZh);
        dest.writeString(periodEn);
        dest.writeString(periodZh);
        dest.writeString(cultureEn);
        dest.writeString(cultureZh);
        dest.writeString(descriptionEn);
        dest.writeString(descriptionZh);
        dest.writeString(imageUrl);
        dest.writeString(thumbnailUrl);
    }

    @Override
    public int describeContents() {
        return 0; // 默认返回0即可（除非包含文件描述符）
    }

    public static final Creator<Artifact> CREATOR = new Creator<Artifact>() {
        @Override
        public Artifact createFromParcel(Parcel in) {
            return new Artifact(in);
        }

        @Override
        public Artifact[] newArray(int size) {
            return new Artifact[size];
        }
    };

    @Override
    public String toString() {
        return "Artifact{" +
                "titleEn='" + titleEn + '\'' +
                ", titleZh='" + titleZh + '\'' +
                ", periodEn='" + periodEn + '\'' +
                ", periodZh='" + periodZh + '\'' +
                ", cultureEn='" + cultureEn + '\'' +
                ", cultureZh='" + cultureZh + '\'' +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", descriptionZh='" + descriptionZh + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Artifact)) return false;
        Artifact other = (Artifact) obj;
        return this.getId() == other.getId(); // 假设 ID 是唯一标识
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode(); // 或 getId().hashCode() 如果是 String
    }
}