package com.example.moviezpoint.Models;

public class MoviesHomeAdapter {
    String movie_id, movie_title, movie_poster_url;

    public MoviesHomeAdapter(String movie_id, String movie_title, String movie_poster_url) {
        this.movie_id = movie_id;
        this.movie_title = movie_title;
        this.movie_poster_url = movie_poster_url;
    }

    public MoviesHomeAdapter() {
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMovie_poster_url() {
        return movie_poster_url;
    }

    public void setMovie_poster_url(String movie_poster_url) {
        this.movie_poster_url = movie_poster_url;
    }

    @Override
    public String toString() {
        return "MoviesHomeAdapter{" +
                "movie_id='" + movie_id + '\'' +
                ", movie_title='" + movie_title + '\'' +
                ", movie_poster_url='" + movie_poster_url + '\'' +
                '}';
    }
}
