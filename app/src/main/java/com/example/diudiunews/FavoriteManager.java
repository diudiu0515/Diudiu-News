package com.example.diudiunews;
import java.util.ArrayList;
import java.util.List;
public class FavoriteManager {
    private List<Newscontent> loveList=new ArrayList<>();
    private static FavoriteManager instance;
    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }
    public synchronized void addFavorite(Newscontent news){
        loveList.add(news);
    }
    public List<Newscontent> getFavoriteNewsList() {
        return new ArrayList<>(loveList);
    }
    public synchronized void removeFavorite(Newscontent news){
        loveList.remove(news);
    }
    public boolean isFavorite(Newscontent news) {
        return loveList.contains(news);
    }

}
