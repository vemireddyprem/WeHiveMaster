package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.EventsUserAdapter;
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;

public class SearchResultsFragment extends GATrackerFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.lvt_social_network) ListView mLtvSearchResults;
    @InjectView(R.id.txt_no_available_events) CustomFontTextView mTxtNotEvents;

    private ArrayList<EventsUser> resultsList;
    private FragmentManager mFragmentManager;
    private boolean mIsExploreMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_network_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.search));
        mFragmentManager = getActivity().getSupportFragmentManager();

        resultsList = getArguments().getParcelableArrayList(AppConstants.RESULTS);
        if (resultsList.size() <= 0) {
            mTxtNotEvents.setVisibility(View.VISIBLE);
        }

        // Validate for explore mode
        if (getArguments().containsKey(AppConstants.EXPLORE_MODE)) {
            mIsExploreMode = getArguments().getBoolean(AppConstants.EXPLORE_MODE);
        }
        mLtvSearchResults.setAdapter(new EventsUserAdapter(getActivity(), resultsList));
        mLtvSearchResults.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        EventsUser eventUser = resultsList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.ID_EVENT, eventUser.getId_event());
        bundle.putBoolean(AppConstants.EXPLORE_MODE, mIsExploreMode);

        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        eventDetailFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().replace(R.id.content_frame, eventDetailFragment).commit();
    }
}