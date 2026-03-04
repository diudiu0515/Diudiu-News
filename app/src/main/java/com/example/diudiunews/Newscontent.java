package com.example.diudiunews;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Newscontent implements Serializable {
    public String title;
    public String publisher;
    public String imageUrl;
    public String date;
    public String time;
    public String videoUrl;
    public List<String> keyword;
    public String category;
    public String content;
    public boolean isRead=false;
    public Newscontent(String a_title, String a_publisher, String imageurl, String a_date, String a_time, List<String> a_keyword){
        this.title=a_title;
        this.publisher=a_publisher;
        this.date=a_date;
        this.time=a_time;
        this.imageUrl=imageurl;
        this.keyword=a_keyword;
    }
    public Newscontent(String a_title, String a_publisher,String imageurl, String a_date, String a_time, String category,String content,String videoUrl) {
        this.title=a_title;
        this.publisher=a_publisher;
        this.date=a_date;
        this.time=a_time;
        this.imageUrl=imageurl;
        this.category=category;
        this.content=content;
        this.videoUrl=videoUrl;
    }
    public Newscontent(String a_title, String a_publisher, String a_imageurl,String a_date, String a_time, String a_content,String a_videourl){
        this.title=a_title;
        this.publisher=a_publisher;
        this.imageUrl=a_imageurl;
        this.date=a_date;
        this.time=a_time;
        this.content=a_content;
        this.videoUrl=a_videourl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Newscontent that = (Newscontent) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(publisher, that.publisher) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, publisher, date, time);
    }
}
