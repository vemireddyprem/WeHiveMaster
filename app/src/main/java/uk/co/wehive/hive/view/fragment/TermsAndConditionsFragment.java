package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;

public class TermsAndConditionsFragment extends GATrackerFragment {

    private FragmentManager mFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.terms_and_conditions_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentManager = getActivity().getSupportFragmentManager();

        setHasOptionsMenu(true);
        initViewComponents();
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.terms_and_conditions_title));
        CustomFontTextView mWebView = (CustomFontTextView) getView().findViewById(R.id.txt_terms_and_conditions);

        try {
            InputStream is = getActivity().getAssets().open("TermsAndConditions.txt");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String text = new String(buffer);
            mWebView.setText(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.follow_people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_done:
                mFragmentManager.beginTransaction().remove(this).commit();
                mFragmentManager.popBackStackImmediate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
