package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.entities.response.EventsUserResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.ISearchCriteriaListener;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontButton;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.view.dialog.DatePickerDialog;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class SearchFragment extends GATrackerFragment implements View.OnClickListener,
        DatePickerDialog.DatePickerListener, IHiveResponse, ISearchCriteriaListener {

    @InjectView(R.id.btn_artist)
    CustomFontButton mBtnArtist;
    @InjectView(R.id.btn_location)
    CustomFontButton mBtnLocation;
    @InjectView(R.id.lyt_date_from)
    LinearLayout mLytDateFrom;
    @InjectView(R.id.lyt_date_to)
    LinearLayout mLytDateTo;
    @InjectView(R.id.txt_day_date_from)
    CustomFontTextView mTxtDayDateFrom;
    @InjectView(R.id.txt_date_from)
    CustomFontTextView mTxtDateFrom;
    @InjectView(R.id.txt_day_date_to)
    CustomFontTextView mTxtDayDateTo;
    @InjectView(R.id.txt_date_to)
    CustomFontTextView mTxtDateTo;
    @InjectView(R.id.btn_search)
    CustomFontButton mBtnSearch;

    private static final String DATE_FROM = "DATE_FROM";
    private static final String DATE_TO = "DATE_TO";

    private FragmentManager mFragmentManager;
    private DatePickerDialog mDatePicker;
    private String mLastSelected;
    private SearchDetailFragment mSearchDetailFragment;
    private String mArtistId = "";
    private String mArtistName = "";
    private String mLocationId = "";
    private String mLocationName = "";
    private String mLocationType = "";
    private String mDateFrom = "";
    private String mDateTo = "";
    private String mSelectedToDay = "";
    private String mSelectedFromDay = "";
    private String mSelectedToDate = "";
    private String mSelectedFromDate = "";
    private ProgressHive mProgressIndicator;
    private boolean mIsExploreMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.search));
        mFragmentManager = getActivity().getSupportFragmentManager();
        mProgressIndicator = new ProgressHive();

        // Validate for explore mode
        if (getArguments().containsKey(AppConstants.EXPLORE_MODE)) {
            mIsExploreMode = getArguments().getBoolean(AppConstants.EXPLORE_MODE);
        }

        setInitialViewsDetails();

        setSearchingInfo();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_artist:
                searchBy(AppConstants.ARTIST);
                break;
            case R.id.btn_location:
                searchBy(AppConstants.LOCATION);
                break;
            case R.id.lyt_date_from:
                mLastSelected = DATE_FROM;
                showDatePickerDialog();
                break;
            case R.id.lyt_date_to:
                mLastSelected = DATE_TO;
                showDatePickerDialog();
                break;
            case R.id.btn_search:
                searchEvent();
                break;
        }
    }

    @Override
    public void onResult(int year, int month, int day) {
        updateDate(year, month, day);
    }

    @Override
    public void onError(RetrofitError error) {
        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong),
                getString(R.string.connection_lost));
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressIndicator.dismiss();
        ArrayList<EventsUser> events = ((EventsUserResponse) response).getData();
        if (events == null) {
            events = new ArrayList<EventsUser>();
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppConstants.RESULTS, events);
        bundle.putBoolean(AppConstants.EXPLORE_MODE, mIsExploreMode);

        SearchResultsFragment resultsFragment = new SearchResultsFragment();
        resultsFragment.setArguments(bundle);

        mFragmentManager.beginTransaction()
                .addToBackStack("").replace(R.id.content_frame, resultsFragment).commit();
    }

    @Override
    public void onItemSelected(String idResult, String nameResult, String typeResult, String tagResult) {

        if (AppConstants.ARTIST.equals(tagResult)) {
            mArtistId = idResult;
            mArtistName = nameResult;
        } else if (AppConstants.LOCATION.equals(tagResult)) {
            mLocationId = idResult;
            mLocationName = nameResult;
            mLocationType = typeResult;
        }
        mBtnArtist.setText(nameResult);
    }

    private void setInitialViewsDetails() {
        mBtnLocation.setOnClickListener(this);
        mBtnArtist.setOnClickListener(this);
        mLytDateFrom.setOnClickListener(this);
        mLytDateTo.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mDatePicker = new DatePickerDialog();
        mDatePicker.setDateChangedListener(this);

        mSearchDetailFragment = new SearchDetailFragment();
        mSearchDetailFragment.setListener(this);

        Calendar calendar = Calendar.getInstance();
        mLastSelected = DATE_FROM;
        updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mLastSelected = DATE_TO;
        updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mLastSelected = "";
    }


    private void searchEvent() {
        if ((mArtistName.length() < 2) && (mLocationName.length() < 2) && (mDateFrom.length() < 4)
                && (mDateTo.length() < 4)) {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_need_select_a_location), "");
        } else {
            mProgressIndicator.show(mFragmentManager, "");
            EventsBL eventsBL = new EventsBL();
            eventsBL.setEventsUserListener(this);

            String idLocTmp = (mLocationId.length() < 2) ? null : mLocationId;
            String typeLocTmp = (mLocationType.length() < 2) ? null : mLocationType;
            eventsBL.searchEvents(mArtistId, idLocTmp, typeLocTmp, mDateFrom, mDateTo, "0", "20");
        }
    }

    private void searchBy(String label) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.SEARCH_BY, label);

        mSearchDetailFragment.setArguments(bundle);
        mFragmentManager.beginTransaction()
                .addToBackStack("").replace(R.id.content_frame, mSearchDetailFragment).commit();
    }

    private void showDatePickerDialog() {
        mDatePicker.show(mFragmentManager, "datePicker");
    }

    private void setSearchingInfo() {
        mBtnArtist.setText((mArtistName != null && mArtistName.length() > 3) ?
                mArtistName.toUpperCase() : getString(R.string.all_artists));
        mBtnLocation.setText((mLocationName != null && mLocationName.length() > 3) ?
                mLocationName.toUpperCase() : getString(R.string.search_location));

        if (mSelectedFromDate.length() > 3) {
            mTxtDayDateFrom.setText(mSelectedFromDay);
            mTxtDateFrom.setText(mSelectedFromDate);
        }

        if (mSelectedToDate.length() > 3) {
            mTxtDayDateTo.setText(mSelectedToDay);
            mTxtDateTo.setText(mSelectedToDate);
        }
    }

    private void updateDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        month_date.setTimeZone(TimeZone.getTimeZone("UTC"));
        String monthName = month_date.format(calendar.getTime());

        String dayText = (day < 10) ? "0" + String.valueOf(day) : String.valueOf(day);
        String monthText = ((month + 1) < 10) ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);

        if (mLastSelected.equals(DATE_FROM)) {
            mSelectedFromDay = dayText;
            mSelectedFromDate = monthName.toUpperCase() + " " + String.valueOf(year);
            mTxtDayDateFrom.setText(dayText);
            mTxtDateFrom.setText(monthName.toUpperCase() + " " + String.valueOf(year));
            mDateFrom = String.valueOf(year) + "-" + monthText + "-" + String.valueOf(day);

        } else if (mLastSelected.equals(DATE_TO)) {
            mSelectedToDay = dayText;
            mSelectedToDate = monthName.toUpperCase() + " " + String.valueOf(year);
            mTxtDayDateTo.setText(dayText);
            mTxtDateTo.setText(monthName.toUpperCase() + " " + String.valueOf(year));
            mDateTo = String.valueOf(year) + "-" + monthText + "-" + String.valueOf(day);
        }
    }
}
