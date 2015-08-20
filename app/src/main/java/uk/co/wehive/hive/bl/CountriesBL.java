package uk.co.wehive.hive.bl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.CountriesResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.ILocationsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class CountriesBL {
    private IHiveResponse hiveListener;
    private ILocationsApi mLocationsService;

    public void setHiveListener(IHiveResponse hiveListener) {
        this.hiveListener = hiveListener;
    }

    public CountriesBL() {
        this.mLocationsService = ApplicationHive.getRestAdapter().create(ILocationsApi.class);
    }

    public void getCountries(){
        mLocationsService.getCountries(new Callback<CountriesResponse>() {
            @Override
            public void success(CountriesResponse countriesResponse, Response response) {
                hiveListener.onResult(countriesResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }
}
