
package com.example.moviezpoint.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YoutubeVideoDataModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<YoutubeVideoDataResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<YoutubeVideoDataResult> getResults() {
        return results;
    }

    public void setResults(List<YoutubeVideoDataResult> results) {
        this.results = results;
    }

}
