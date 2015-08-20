package uk.co.wehive.hive.view.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.view.fragment.FlagPostFragment;
import uk.co.wehive.hive.view.fragment.PostLikersFragment;
import uk.co.wehive.hive.view.fragment.ShareGoodTimesDetailFragment;

public class CarouselMenuActivity extends ActionBarActivity {

    public static CarouselMenuActivity mCarouselMenuActivity;

    private FragmentManager mFragmentManager;
    private Post mPost;
    private int mSelectedOption;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_carousel_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));

        mCarouselMenuActivity = this;
        mFragmentManager = getSupportFragmentManager();

        Bundle extras = getIntent().getExtras();
        mSelectedOption = extras.getInt("option");
        mPost = extras.getParcelable("currentPost");

        launchView();
    }

    private void launchView() {

        if (mSelectedOption == 0) {
            ShareGoodTimesDetailFragment fragment = new ShareGoodTimesDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.POST, mPost);
            fragment.setArguments(bundle);

            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
        } else if (mSelectedOption == 1) {
            Bundle bundle;
            bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, mPost.getId_post());

            FlagPostFragment fragment = new FlagPostFragment();
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
        } else if (mSelectedOption == 2) {
            Bundle bundle;
            bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, mPost.getId_post());

            PostLikersFragment fragment = new PostLikersFragment();
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
        }
    }
}
