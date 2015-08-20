package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IConfirmation;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.activity.CarouselMenuActivity;
import uk.co.wehive.hive.view.dialog.ConfirmationDialog;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class FlagPostFragment extends Fragment implements IHiveResponse, IConfirmation {

    private CheckBox mChkSpam;
    private CheckBox mChkCompromised;
    private CheckBox mChkAbusive;
    private ProgressHive mProgressHive;
    private FragmentManager mFragmentManager;
    private PostBL mPostBL;
    private String mPostId;
    private User mUser;
    private String mIssues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_issue_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = ManagePreferences.getUserPreferences();
        mFragmentManager = getActivity().getSupportFragmentManager();
        mPostBL = new PostBL();
        mPostBL.setListener(this);
        mPostId = getArguments().getString(AppConstants.ID_POST);
        initViewComponents();
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.select_issue));
        mChkSpam = (CheckBox) getView().findViewById(R.id.chk_spam);
        mChkCompromised = (CheckBox) getView().findViewById(R.id.chk_compromised);
        mChkAbusive = (CheckBox) getView().findViewById(R.id.chk_abusive);
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.report_post, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_post:
                reportPost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reportPost() {

        if (mChkSpam.isChecked() || mChkCompromised.isChecked() || mChkAbusive.isChecked()) {
            mIssues = "";
            if (mChkSpam.isChecked()) {
                mIssues = "0,";
            } else {
                mIssues = ",";
            }

            if (mChkCompromised.isChecked()) {
                mIssues = mIssues + "1,";
            } else {
                mIssues = mIssues + ",";
            }

            if (mChkAbusive.isChecked()) {
                mIssues = mIssues + "2";
            }

            ConfirmationDialog.showMessage(getActivity(), "", getString(R.string.delete_post), this);
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.connection_lost));
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        mFragmentManager.beginTransaction().remove(this).commit();
        mFragmentManager.popBackStackImmediate();
    }

    @Override
    public void positiveSelected() {
        mProgressHive.show(mFragmentManager, "");
        mPostBL.reportPost(mUser.getToken(),String.valueOf(mUser.getId_user()), mPostId, mIssues);
    }

    @Override
    public void negativeSelected() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            CarouselMenuActivity.mCarouselMenuActivity.finish();
        } catch (Exception ignored) {
        }
    }
}
