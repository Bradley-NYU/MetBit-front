package com.example.metbit.Retrofit;

import com.example.metbit.art.Artifact;
import com.example.metbit.article.Article;
import com.example.metbit.article.ArticleDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //告诉 Retrofit 去请求哪个接口
    @GET("/api/artifacts")
    Call<List<Artifact>> getArtifacts();

    // 新加：根据关键词搜索 artifact
    @GET("artifacts/search")
    Call<List<Artifact>> searchArtifacts(
            @Query("q") String keyword,
            @Query("language") String language
    );

    //获取最新的图片
    @GET("artifacts/latest")
    Call<Artifact> getLatestArtifact();

    //获取所有的文章
    @GET("/api/articles")
    Call<List<Article>> getAllArticles(@Query("lang") String lang);

    @GET("articles/{id}")
    Call<ArticleDTO> getArticleById(@Path("id") Long id, @Query("lang") String lang);

}
