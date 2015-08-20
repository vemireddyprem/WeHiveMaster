package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import uk.co.wehive.hive.entities.response.CitiesResponse;
import uk.co.wehive.hive.entities.response.CountriesResponse;
import uk.co.wehive.hive.entities.response.LocationResponse;

public interface ILocationsApi {

    final static String urlCountries = "/Countries/countries.json";
    final static String urlCities = "/Cities/cities.json";
    final static String urlLocations = "/Cities/locations.json";

    @GET(urlCountries)
    void getCountries(Callback<CountriesResponse> countriesResponse);

    @GET(urlCities)
    void getCities(@Query("code") String code, Callback<CitiesResponse> citiesResponse);

    @GET(urlLocations)
    void getLocations(@Query("criteria") String criteria, @Query("start") String start,
                      @Query("amount") String amount, Callback<LocationResponse> locationResponse);
}