package com.example.metbit.favorite;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.metbit.art.Artifact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static final String PREF_NAME = "favorites";
    private static final String KEY_FAVORITE_LIST = "favorite_list";

    public static void saveFavorite(Context context, Artifact artifact) {
        List<Artifact> list = loadFavorites(context);
        if (!list.contains(artifact)) {
            list.add(artifact);
            saveList(context, list);
        }
    }

    public static void removeFavorite(Context context, Artifact artifact) {
        List<Artifact> list = loadFavorites(context);
        list.remove(artifact);
        saveList(context, list);
    }

    public static List<Artifact> loadFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITE_LIST, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Artifact>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static boolean isFavorite(Context context, Artifact artifact) {
        return loadFavorites(context).contains(artifact);
    }

    private static void saveList(Context context, List<Artifact> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(list);
        prefs.edit().putString(KEY_FAVORITE_LIST, json).apply();
    }
}
