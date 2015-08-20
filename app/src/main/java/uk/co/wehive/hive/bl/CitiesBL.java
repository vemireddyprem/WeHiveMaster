package uk.co.wehive.hive.bl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.CitiesResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.ILocationsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class CitiesBL {

    private IHiveResponse hiveListener;
    private ILocationsApi mLocationsService;

    public void setHiveListener(IHiveResponse hiveListener) {
        this.hiveListener = hiveListener;
    }

    public CitiesBL() {
        this.mLocationsService = ApplicationHive.getRestAdapter().create(ILocationsApi.class);
    }

    public void getCities(String code){
        mLocationsService.getCities(code,new Callback<CitiesResponse>() {
            @Override
            public void success(CitiesResponse citiesResponse, Response response) {
                hiveListener.onResult(citiesResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

}
