package uk.co.wehive.hive.bl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.LocationResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.ILocationsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class LocationsBL {

    private IHiveResponse hiveListener;
    private ILocationsApi mLocationService;

    public void setHiveListener(IHiveResponse hiveListener) {
        this.hiveListener = hiveListener;
    }

    public LocationsBL() {
        this.mLocationService = ApplicationHive.getRestAdapter().create(ILocationsApi.class);
    }

    public void getLocations(String criteria, String start, String amount) {
        mLocationService.getLocations(criteria, start, amount, new Callback<LocationResponse>() {
            @Override
            public void success(LocationResponse locationResponse, Response response) {
                hiveListener.onResult(locationResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }
}