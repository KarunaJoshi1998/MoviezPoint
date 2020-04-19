
package com.example.moviezpoint.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComingSoonData {

    @SerializedName("results")
    @Expose
    private List<ComingSoonResult> results = null;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("dates")
    @Expose
    private ComingSoonDates dates;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    public List<ComingSoonResult> getResults() {
        return results;
    }

    public void setResults(List<ComingSoonResult> results) {
        this.results = results;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public ComingSoonDates getDates() {
        return dates;
    }

    public void setDates(ComingSoonDates dates) {
        this.dates = dates;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
