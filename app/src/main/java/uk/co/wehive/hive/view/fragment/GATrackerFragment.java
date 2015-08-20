package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class GATrackerFragment extends Fragment {

    private Tracker mTracker;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTracker = EasyTracker.getInstance(this.getActivity());
        mTracker.set(Fields.SCREEN_NAME, ((Object) this).getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        mTracker.send(MapBuilder.createAppView().build());
    }
}