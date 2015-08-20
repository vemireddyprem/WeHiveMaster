package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import uk.co.wehive.hive.entities.response.SearchArtistResponse;

public interface IArtistsApi {

    final static String urlSearchArtist = "/Users/searchArtists.json";

    @GET(urlSearchArtist)
    void searchArtist(@Query("start") String start, @Query("amount") String amount,
                      @Query("criteria") String criteria, @Query("id_user") String userId,
                      Callback<SearchArtistResponse> response);

}