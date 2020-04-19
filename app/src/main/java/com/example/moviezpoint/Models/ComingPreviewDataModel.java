package com.example.moviezpoint.Models;

public class ComingPreviewDataModel {
    String movie_id, movie_title, movie_date, movie_description, movie_trailer;

    public ComingPreviewDataModel(String movie_id, String movie_title, String movie_date, String movie_description, String movie_trailer) {
        this.movie_id = movie_id;
        this.movie_title = movie_title;
        this.movie_date = movie_date;
        this.movie_description = movie_description;
        this.movie_trailer = movie_trailer;
    }

    public ComingPreviewDataModel() {
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

    public String getMovie_date() {
        return movie_date;
    }

    public void setMovie_date(String movie_date) {
        this.movie_date = movie_date;
    }

    public String getMovie_description() {
        return movie_description;
    }

    public void setMovie_description(String movie_description) {
        this.movie_description = movie_description;
    }

    public String getMovie_trailer() {
        return movie_trailer;
    }

    public void setMovie_trailer(String movie_trailer) {
        this.movie_trailer = movie_trailer;
    }

    @Override
    public String toString() {
        return "ComingPreviewDataModel{" +
                "movie_id='" + movie_id + '\'' +
                ", movie_title='" + movie_title + '\'' +
                ", movie_date='" + movie_date + '\'' +
                ", movie_description='" + movie_description + '\'' +
                ", movie_trailer='" + movie_trailer + '\'' +
                '}';
    }
}
