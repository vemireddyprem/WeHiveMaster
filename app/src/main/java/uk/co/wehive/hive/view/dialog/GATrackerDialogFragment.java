package uk.co.wehive.hive.view.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class GATrackerDialogFragment extends DialogFragment {

    private Tracker mTracker;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTracker = EasyTracker.getInstance(getActivity());
        mTracker.set(Fields.SCREEN_NAME, ((Object) this).getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        mTracker.send(MapBuilder.createAppView().build());
    }

    @Override
    public void onStop() {
        super.onStop();
        mTracker.send(MapBuilder.createAppView().build());
    }
}
