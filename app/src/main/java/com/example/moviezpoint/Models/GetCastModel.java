package com.example.moviezpoint.Models;

public class GetCastModel {
    String cast_id, cast_name, cast_as, cast_image;

    public GetCastModel(String cast_id, String cast_name, String cast_as, String cast_image) {
        this.cast_id = cast_id;
        this.cast_name = cast_name;
        this.cast_as = cast_as;
        this.cast_image = cast_image;
    }

    public GetCastModel() {
    }

    public String getCast_id() {
        return cast_id;
    }

    public void setCast_id(String cast_id) {
        this.cast_id = cast_id;
    }

    public String getCast_name() {
        return cast_name;
    }

    public void setCast_name(String cast_name) {
        this.cast_name = cast_name;
    }

    public String getCast_as() {
        return cast_as;
    }

    public void setCast_as(String cast_as) {
        this.cast_as = cast_as;
    }

    public String getCast_image() {
        return cast_image;
    }

    public void setCast_image(String cast_image) {
        this.cast_image = cast_image;
    }
}
