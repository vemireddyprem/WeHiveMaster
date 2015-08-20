package uk.co.wehive.hive.bl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.SearchArtistResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.IArtistsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class ArtistBL {

    private IHiveResponse hiveListener;
    private IArtistsApi mArtistService;

    public void setHiveListener(IHiveResponse hiveListener) {
        this.hiveListener = hiveListener;
    }

    public ArtistBL() {
        this.mArtistService = ApplicationHive.getRestAdapter().create(IArtistsApi.class);
    }

    public void getArtists(String start, String amount, String criteria, String userId) {
        mArtistService.searchArtist(start, amount, criteria, userId, new Callback<SearchArtistResponse>() {
            @Override
            public void success(SearchArtistResponse searchArtistResponse, Response response) {
                hiveListener.onResult(searchArtistResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }
}
