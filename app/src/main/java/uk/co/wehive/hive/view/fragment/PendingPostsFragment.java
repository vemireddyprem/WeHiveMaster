/*******************************************************************************
 PROJECT:       Hive
 FILE:          PendingPostsFragment.java
 DESCRIPTION:   Manage user pending posts
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        29/09/2014  Marcela Güiza A.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.PendingPostsAdapter;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.CreatePost;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.CreatePostResponse;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IPendingPost;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class PendingPostsFragment extends Fragment implements IPendingPost, IHiveResponse {

    @InjectView(R.id.ltv_pending_post)
    ListView mLvPendingPosts;

    private static final String POST_WITHOUT_VIDEO = "0";
    private final String TAG = "PendingPostsFragment";

    private ArrayList<Post> mPendingPosts;
    private PendingPostsAdapter mAdapter;
    private EventPostsResponse mReadResponse;
    private User mUserPrefs;
    private PostBL mPostBL;
    private Post mPost;
    private ProgressHive mProgressBar;
    private boolean publishingAllPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_posts_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.pending_posts));
        setHasOptionsMenu(true);

        mUserPrefs = ManagePreferences.getUserPreferences();

        mPostBL = new PostBL();
        mPostBL.setListener(this);

        mProgressBar = new ProgressHive();

        mReadResponse = PersistenceHelper.readResponse(getActivity(), AppConstants.PENDING_POSTS + "_" +
                String.valueOf(mUserPrefs.getId_user()), EventPostsResponse.class);

        mPendingPosts = mReadResponse.getData();
        mAdapter = new PendingPostsAdapter(getActivity(), mPendingPosts);
        mAdapter.setPendingPostListener(this);
        mLvPendingPosts.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.pending_posts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.publish_all_post:
                reloadAllPosts();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reloadAllPosts() {
        mProgressBar.show(getFragmentManager(), "");

        publishingAllPosts = mPendingPosts.size() > 0;
        if (publishingAllPosts) {
            reloadPostPublish(mPendingPosts.get(mPendingPosts.size() - 1));
        } else {
            mProgressBar.dismiss();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void createPost(Post post) {
        mProgressBar.show(getFragmentManager(), "");
        reloadPostPublish(post);
    }

    @Override
    public void deletePost(Post post) {
        removePostFromList(post);
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
        publishingAllPosts = false;
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressBar.dismiss();
        removePostFromList(mPost);
        if (AppConstants.MEME_POST.equals(mPost.getPostType())) {
            CreatePost postResponse = ((CreatePostResponse) response).getData();
            mPostBL.uploadPostMedia(mUserPrefs.getToken(),String.valueOf(mUserPrefs.getId_user()),
                    String.valueOf(postResponse.getId()), mPost.getPostPhotoFile(), "");
            mPost.setPostType("");

        } else if (AppConstants.VIDEO_POST.equals(mPost.getPostType())) {
            CreatePost postResponse = ((CreatePostResponse) response).getData();
            Log.i(TAG, "pending - before post video");
            mPostBL.uploadPostVideo(mUserPrefs.getToken(),String.valueOf(mUserPrefs.getId_user()),
                    String.valueOf(postResponse.getId()), new File(mPost.getPostVideoPath()),
                    new File(mPost.getMedia_video_thumbnail()));
            Log.i(TAG, "pending - after post video");
            mPost.setPostType("");
        } else {
            if (publishingAllPosts) {
                reloadAllPosts();
            }
        }
    }

    private void reloadPostPublish(Post post) {
        mPost = post;
        mPostBL.createPost(mUserPrefs.getToken(),String.valueOf(mUserPrefs.getId_user()), post.getPost_text(),
                post.getPostEventId(), POST_WITHOUT_VIDEO, "");
    }

    private void removePostFromList(Post post) {
        if (mPendingPosts.contains(post)) {
            mPendingPosts.remove(post);
        }

        mReadResponse.setData(mPendingPosts);
        PersistenceHelper.saveResponse(getActivity(), mReadResponse, AppConstants.PENDING_POSTS + "_" +
                String.valueOf(mUserPrefs.getId_user()));
        mAdapter.notifyDataSetChanged();
    }
}