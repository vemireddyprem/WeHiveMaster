package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;

public class PostGoodTimesDetail extends Fragment {

    protected Post mPost;
    protected ImageView mImgUSer;
    protected ImageView mImgPost;
    protected TextView mLblPost;
    protected TextView mLblName;
    protected TextView mLblDate;
    protected Picasso mPicasso;
    protected User mUserPrefs;
    private final String TAG = "PostGoodTimesDetail";

    public void initControls() {

        Bundle bundle = getArguments();
        mPicasso = new Picasso.Builder(getActivity()).executor(Executors.newSingleThreadExecutor()).build();
        mLblPost = (TextView) getView().findViewById(R.id.txt_comment);
        mImgUSer = (ImageView) getView().findViewById(R.id.img_user_photo);
        mImgPost = (ImageView) getView().findViewById(R.id.img_comment_photo);
        mLblName = (TextView) getView().findViewById(R.id.txt_user_username);
        mLblDate = (TextView) getView().findViewById(R.id.txt_date_comment);
        mUserPrefs = ManagePreferences.getUserPreferences();
        if (bundle.containsKey(AppConstants.POST)) {
            mPost = bundle.getParcelable(AppConstants.POST);
            loadData();
        }
    }

    public void loadData() {
        try {
            mPicasso.load(mPost.getUser_photo()).error(R.drawable.ic_userphoto_menu).transform(new PictureTransformer()).into(mImgUSer);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        mLblName.setText(mPost.getFirst_name() + " " + mPost.getLast_name());
        mLblDate.setText(DateOperations.getDate(DateOperations.getDate(Long.parseLong(mPost.getAge_post()) * 1000, "dd MMMM h:m:a")));

        if (mPost.getMedia_type() != null && mPost.getMedia_type().toLowerCase().equals("image")) {
            mImgPost.setVisibility(View.VISIBLE);
            mPicasso.load(mPost.getMedia_absolute_file()).into(mImgPost);
        } else if (mPost.getMedia_type() != null && mPost.getMedia_type().toLowerCase().equals("video")) {
            mImgPost.setVisibility(View.VISIBLE);
            mPicasso.load(mPost.getMedia_absolute_thumbvideo_file()).into(mImgPost);
        } else {
            mImgPost.setVisibility(View.GONE);
        }
        if (mPost.getPost_text() != null) {
            mLblPost.setText(mPost.getPost_text());
        }
    }

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post mPost) {
        this.mPost = mPost;
    }
}
