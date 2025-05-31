package com.example.metbit.article;

import java.util.List;

public class ArticleDTO {
    public Long id;
    public String title;
    public String author;
    public String date;
    public String imageUrl;
    public List<String> imageUrls;
    public List<String> paragraphs;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public Long getId() {
        return id;
    }
}
