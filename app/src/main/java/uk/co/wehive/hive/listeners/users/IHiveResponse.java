package uk.co.wehive.hive.listeners.users;

import retrofit.RetrofitError;
import uk.co.wehive.hive.entities.response.HiveResponse;

public interface IHiveResponse {

    void onError(RetrofitError error);

    void onResult(HiveResponse response);
}