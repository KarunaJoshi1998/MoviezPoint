package com.example.moviezpoint.Models;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("now_playing?api_key=fac94c6191cd9ab4bcf2a654448caab6&with_original_language=hi")
    Call<NowPlayingData> NOW_PLAYING_DATA_CALL();

    @GET("upcoming?api_key=fac94c6191cd9ab4bcf2a654448caab6&with_original_language=hi")
    Call<ComingSoonData> COMING_SOON_DATA_CALL();

    @GET("{movie_id}?api_key=fac94c6191cd9ab4bcf2a654448caab6")
    Call<MovieDetailsModel> MOVIE_DETAILS_MODEL_CALL(@Path("movie_id") Long movie_id);

    @GET("{movie_id}/videos?api_key=fac94c6191cd9ab4bcf2a654448caab6")
    Call<YoutubeVideoDataModel> YOUTUBE_VIDEO_DATA_MODEL_CALL(@Path("movie_id") Long movie_id);

}
