package uk.co.wehive.hive.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.SearchResultAdapter;
import uk.co.wehive.hive.bl.ArtistBL;
import uk.co.wehive.hive.bl.LocationsBL;
import uk.co.wehive.hive.entities.GenericSearch;
import uk.co.wehive.hive.entities.Location;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LocationResponse;
import uk.co.wehive.hive.entities.response.SearchArtistResponse;
import uk.co.wehive.hive.listeners.ISearchCriteriaListener;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontEditText;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class SearchDetailFragment extends GATrackerFragment implements TextView.OnEditorActionListener,
        IHiveResponse, AdapterView.OnItemClickListener {

    @InjectView(R.id.edt_search_user) CustomFontEditText mEdtSearch;
    @InjectView(R.id.lvt_social_network) ListView mLtvSearchResults;

    private String searchingTag;
    private ProgressHive mProgressHive;
    private ArtistBL mArtistBL;
    private LocationsBL mLocationsBL;
    private ArrayList<Follower> mArtistsList;
    private ArrayList<Location> mLocationsList;
    private User mUser;
    private ISearchCriteriaListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_network_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String lookFor = getArguments().getString(AppConstants.SEARCH_BY);
        if (lookFor != null) {
            searchingTag = lookFor;
        }

        mArtistBL = new ArtistBL();
        mArtistBL.setHiveListener(this);
        mLocationsBL = new LocationsBL();
        mLocationsBL.setHiveListener(this);
        mUser = ManagePreferences.getUserPreferences();

        initViewComponents();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == KeyEvent.KEYCODE_ENTER) {
            if (mEdtSearch.getText().length() > 0) {
                search(mEdtSearch.getText().toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onError(RetrofitError error) {
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {

        mProgressHive.dismiss();
        if (AppConstants.ARTIST.equals(searchingTag)) {
            mArtistsList = ((SearchArtistResponse) response).getData();
        } else if (AppConstants.LOCATION.equals(searchingTag)) {
            mLocationsList = ((LocationResponse) response).getData();
        }

        showResults();
    }

    private void initViewComponents() {
        mProgressHive = new ProgressHive();

        if (AppConstants.LOCATION.equals(searchingTag) || AppConstants.ARTIST.equals(searchingTag)) {
            mEdtSearch.setVisibility(View.VISIBLE);
        }

        mEdtSearch.setOnEditorActionListener(this);
        mLtvSearchResults.setOnItemClickListener(this);
    }

    private void search(String search) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
        mProgressHive.show(getActivity().getSupportFragmentManager(), "");
        if (AppConstants.ARTIST.equals(searchingTag)) {
            mArtistBL.getArtists("0", "20", search, String.valueOf(mUser.getId_user()));
        } else {
            mLocationsBL.getLocations(search, "0", "20");
        }
    }

    private void showResults() {
        ArrayList<GenericSearch> genericSearchList = new ArrayList<GenericSearch>();

        if (AppConstants.ARTIST.equals(searchingTag)) {
            for (Follower artist : mArtistsList) {

                GenericSearch result = new GenericSearch();
                result.setId(String.valueOf(result.getId()));
                result.setImage(artist.getReal_photo());
                result.setName(artist.getUsername());
                genericSearchList.add(result);
            }
        } else if (AppConstants.LOCATION.equals(searchingTag)) {
            for (Location location : mLocationsList) {

                GenericSearch result = new GenericSearch();
                result.setId(String.valueOf(location.getId_location()));
                result.setImage(AppConstants.LOCATION);
                result.setName(location.getName_location());
                result.setLocationType(location.getType());
                genericSearchList.add(result);
            }
        }

        if (genericSearchList.size() > 0) {
            mEdtSearch.setVisibility(View.GONE);
            mLtvSearchResults.setAdapter(new SearchResultAdapter(getActivity(), genericSearchList));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (AppConstants.ARTIST.equals(searchingTag)) {
            Follower artist = mArtistsList.get(position);
            mListener.onItemSelected(String.valueOf(artist.getId_user()),
                    artist.getUsername(), "", AppConstants.ARTIST);

        } else if (AppConstants.LOCATION.equals(searchingTag)) {
            Location location = mLocationsList.get(position);
            mListener.onItemSelected(String.valueOf(location.getId_location()),
                    location.getName_location(), location.getType(), AppConstants.LOCATION);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setListener(ISearchCriteriaListener listener) {
        this.mListener = listener;
    }
}
