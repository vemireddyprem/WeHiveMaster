package uk.co.wehive.hive.view.fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.CommentAdapter;
import uk.co.wehive.hive.bl.CommentsBL;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.Comment;
import uk.co.wehive.hive.entities.LikePost;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.CommentsResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LikePostResponse;
import uk.co.wehive.hive.entities.response.PostDetailsResponse;
import uk.co.wehive.hive.listeners.dialogs.IConfirmation;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.utils.swipe.ListViewSwipeGesture;
import uk.co.wehive.hive.view.dialog.ConfirmationDialog;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class CommentGoodTimesDetailFragment extends GATrackerFragment implements IHiveResponse,
        View.OnClickListener, IConfirmation {

    private static CommentGoodTimesDetailFragment instance;
    private final String TAG = "CommentGoodTimesDetailF";

    private enum optionsResult {like, comment, getcomments, getpost}

    private PullToRefreshListView mLvtPostList;
    private ArrayList<Comment> mCommentList;
    private String mOption;
    private PostBL mPostBL;
    private CustomFontTextView mTxtLikeComment;
    private CustomFontTextView mTxtLikeCommentCounter;
    private CustomFontTextView mTxtCommentEvent;
    private User mUser;
    private Post mPost;
    private ImageView mImgUSer;
    private ImageView mImgPost;
    private ImageView mImgPlay;
    private TextView mLblPost;
    private TextView mLblName;
    private TextView mLblDate;
    private TextView mLblUser;
    private FragmentManager mFragmentManager;
    private VideoView mVideoView;
    private FrameLayout mFrameVideo;
    private Menu mMenu;
    private ProgressHive mProgressHive;
    private CommentAdapter mCommentAdapter;
    private String mPostUserId;
    private int mCounterLikes;
    private boolean mIsExploreMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_good_times_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = ManagePreferences.getUserPreferences();
        mPostBL = new PostBL();
        mPostBL.setListener(this);
        instance = this;

        mFragmentManager = getActivity().getSupportFragmentManager();

        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
        mProgressHive.show(getActivity().getSupportFragmentManager(), "");

        Bundle bundle = getArguments();
        if (bundle.containsKey(AppConstants.POST)) {
            mPost = bundle.getParcelable(AppConstants.POST);
            Log.i(TAG, " POST POST");
        }

        if (bundle.containsKey(AppConstants.EXPLORE_MODE)) {
            mIsExploreMode = getArguments().getBoolean(AppConstants.EXPLORE_MODE);
        }
        setHasOptionsMenu(true);

        if (bundle.containsKey(AppConstants.ID_POST)) {
            mOption = optionsResult.getpost.toString();
            mPostBL.getDetails(mUser.getToken(), String.valueOf(mUser.getId_user()), bundle.getString(AppConstants.ID_POST));
        } else {
            initViewComponents();
            mOption = optionsResult.getcomments.toString();
            mPostBL.newsFeed(mUser.getToken(), mPost.getId_post(), "", "", String.valueOf(mUser.getId_user()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mIsExploreMode) {
            menu.clear();
            getActivity().getMenuInflater().inflate(R.menu.delete_post, menu);
            mMenu = menu;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_post:
                if (mPost.getId_user().equals(Integer.toString(mUser.getId_user()))) {
                    deletePost();
                } else {
                    Bundle bundle;
                    bundle = new Bundle();
                    bundle.putString(AppConstants.ID_POST, mPost.getId_post());
                    FlagPostFragment flagPostFragment = new FlagPostFragment();
                    flagPostFragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, flagPostFragment).commit();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePost() {
        ConfirmationDialog.showMessage(getActivity(), "", getString(R.string.delete_post), this);
    }

    // Initialize view components
    private void initViewComponents() {

        mLvtPostList = (PullToRefreshListView) getView().findViewById(R.id.lvt_posts_list);

        mCommentList = new ArrayList<Comment>();
        mCommentAdapter = new CommentAdapter(getActivity(), mCommentList, getActivity().getSupportFragmentManager());
        mLvtPostList.setAdapter(mCommentAdapter);

        // Add header to list view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.comment_detail_header, mLvtPostList, false);
        mLvtPostList.addHeaderView(header, null, false);

        mLblPost = (TextView) header.findViewById(R.id.txt_comment);
        mImgUSer = (ImageView) header.findViewById(R.id.img_user_photo);
        mImgPost = (ImageView) header.findViewById(R.id.img_comment_photo);
        mImgPlay = (ImageView) header.findViewById(R.id.img_play);
        mLblName = (TextView) header.findViewById(R.id.txt_user_username);
        mLblDate = (TextView) header.findViewById(R.id.txt_date_comment);
        mLblUser = (TextView) header.findViewById(R.id.txt_user);

        getView().findViewById(R.id.btn_create_comment).setOnClickListener(this);

        mTxtLikeComment = (CustomFontTextView) header.findViewById(R.id.txt_like_event);
        mTxtLikeComment.setOnClickListener(this);
        mTxtLikeCommentCounter = (CustomFontTextView) header.findViewById(R.id.txt_like_event_counter);
        mTxtLikeCommentCounter.setOnClickListener(this);

        CustomFontTextView mTxtShareEvent = (CustomFontTextView) header.findViewById(R.id.txt_share_event);
        mTxtCommentEvent = (CustomFontTextView) header.findViewById(R.id.txt_comment_event);
        if (mPost != null) {
            mTxtShareEvent.setText(mPost.getShares());
            mTxtCommentEvent.setText(mPost.getComments());
            mCounterLikes = Integer.parseInt(mPost.getLikes());
            setLikeImage();
        }
        mTxtShareEvent.setOnClickListener(this);

        mImgUSer.setOnClickListener(this);
        mImgPlay.setOnClickListener(this);

        mFrameVideo = (FrameLayout) header.findViewById(R.id.frame_video);
        mVideoView = (VideoView) header.findViewById(R.id.video_comment);

        // Create a custom media controller that ignores the back button
        MediaController mMediaController = new MediaController(getActivity()) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return super.dispatchKeyEvent(event);
            }
        };

        try {
            if (mPost.getMedia_type() != null && mPost.getMedia_type().toLowerCase().equals("video")) {
                // Attach the media controller
                mVideoView.setVideoURI(Uri.parse(mPost.getMedia_absolute_file()));
                mVideoView.setMediaController(mMediaController);
                mVideoView.seekTo(0);
            }
        } catch (Exception e) {
            Log.d(TAG, "error " + e.getMessage());
        }

        loadData();

        ListViewSwipeGesture everythingTouchListener = new ListViewSwipeGesture(mLvtPostList,
                swipeListener, this.getActivity(), R.id.list_display_view_container,
                R.drawable.ic_delete, true);
        mLvtPostList.setOnTouchListener(everythingTouchListener);

        mLvtPostList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCommentList.clear();
                try {
                    mOption = optionsResult.getcomments.toString();
                    mPostBL.newsFeed(mUser.getToken(), mPost.getId_post(), "", "", String.valueOf(mUser.getId_user()));
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }

            }
        });
    }

    public void loadData() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.post_title));
        if (mPost.getUser_photo() != null && mPost.getUser_photo().length() > 0) {
            Picasso.with(getActivity()).load(mPost.getUser_photo()).error(R.drawable.ic_userphoto_menu).transform(new PictureTransformer()).into(mImgUSer);
        }

        mLblUser.setText("@" + mPost.getUsername());
        mLblName.setText(mPost.getFirst_name() + " " + mPost.getLast_name());
        mLblDate.setText(DateOperations.getDate(DateOperations.getDate(Long.parseLong(mPost.getAge_post()) * 1000, "dd MMMM h:m:a")));
        if (mPost.getMedia_type() != null && mPost.getMedia_type().toLowerCase().equals("image")) {
            mImgPost.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(mPost.getMedia_absolute_file()).into(mImgPost);
        } else if (mPost.getMedia_type() != null && mPost.getMedia_type().toLowerCase().equals("video")) {
            mImgPost.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(mPost.getMedia_absolute_thumbvideo_file()).into(mImgPost);
            mImgPlay.setVisibility(View.VISIBLE);
        } else {
            mImgPost.setVisibility(View.GONE);
        }

        if (mPost.getPost_text() != null) {
            mLblPost.setText(Html.fromHtml(mPost.getPost_text()));
            mLblPost.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();

        if (!response.getStatus()) {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.post_has_been_removed));
            mFragmentManager.popBackStack();
        } else {
            if (mOption.equals(optionsResult.getcomments.toString())) {
                mCommentList.clear();
                try {
                    mCommentList.addAll(((CommentsResponse) response).getData());
                    for (Comment aMCommentList : mCommentList) {
                        aMCommentList.setSwipe(mUser.getId_user() == aMCommentList.getUser_id_user());
                    }
                    mTxtCommentEvent.setText(String.valueOf(mCommentList.size()));

                    cleanRefreshingLabels();
                    mLvtPostList.onRefreshComplete();
                    mCommentAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            } else if (mOption.equals(optionsResult.like.toString())) {
                try {

                    LikePost likePost = ((LikePostResponse) response).getData();
                    mCounterLikes = likePost.getCount();
                    mPost.setLikes(String.valueOf(mCounterLikes));
                    mPost.setLiked(mPost.getLiked().equals(AppConstants.TRUE) || mPost.getLiked().equals(AppConstants.LIKED) ? AppConstants.FALSE : AppConstants.TRUE);
                    setLikeImage();
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            } else if (mOption.equals(optionsResult.getpost.toString())) {
                try {
                    mPost = ((PostDetailsResponse) response).getData();
                    initViewComponents();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

                loadData();
                mOption = optionsResult.getcomments.toString();
                mPostBL.newsFeed(mUser.getToken(), mPost.getId_post(), "", "", String.valueOf(mUser.getId_user()));
            }

            if (mPost.getId_user() != null) {
                mPostUserId = mPost.getId_user();
            } else {
                mPostUserId = mPost.getUser_id_user();
            }

            setPostOptionsMenu(mPostUserId);
        }
    }

    private void setPostOptionsMenu(String postUserId) {
        if (!mIsExploreMode && mMenu != null) {
            if (postUserId.equals(Integer.toString(mUser.getId_user())) && mMenu.size() > 0) {
                mMenu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_delete));
            } else if (!postUserId.equals(Integer.toString(mUser.getId_user())) && mMenu.size() > 0) {
                mMenu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_flag));
            }
        }
    }

    // Webservice call
    private void addLike() {
        mOption = optionsResult.like.toString();
        String state = mPost.getLiked().equals(AppConstants.TRUE) || mPost.getLiked().equals(AppConstants.LIKED) ? "0" : "1";
        mPostBL.addLike(mUser.getToken(), String.valueOf(mUser.getId_user()), mPost.getId_post(), state);
    }

    // Set like or unlike icon
    private void setLikeImage() {
        if (mPost.getLiked().equals(AppConstants.TRUE) || mPost.getLiked().equals(AppConstants.LIKED)) {
            Drawable img = getResources().getDrawable(R.drawable.ic_heart_active);
            mTxtLikeComment.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            mTxtLikeCommentCounter.setText(String.valueOf(mCounterLikes));
        } else {
            Drawable img = getResources().getDrawable(R.drawable.ic_heart);
            mTxtLikeComment.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            mTxtLikeCommentCounter.setText(String.valueOf(mCounterLikes));
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {

            case R.id.btn_create_comment:
                if (mIsExploreMode) {
                    ConfirmationDialog.showMessage(getActivity(), "",
                            getString(R.string.you_need_log_in_or_sign_up_to_add_comments), new IConfirmation() {
                                @Override
                                public void positiveSelected() {
                                    returnToLoginView();
                                }

                                @Override
                                public void negativeSelected() {
                                }
                            }
                    );
                } else {
                    bundle.putString(AppConstants.POST_ID, mPost.getId_post());
                    bundle.putParcelableArrayList(AppConstants.COMMENTS_LIST, mCommentList);
                    CreateCommentFragment commentFragment = new CreateCommentFragment();
                    commentFragment.setArguments(bundle);

                    mFragmentManager.beginTransaction().addToBackStack("")
                            .replace(R.id.content_frame, commentFragment).commit();
                }
                break;

            case R.id.txt_like_event:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    addLike();
                }
                break;

            case R.id.txt_share_event:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    ShareGoodTimesDetailFragment shareGoodTimesDetailFragment = new ShareGoodTimesDetailFragment();

                    bundle = new Bundle();
                    bundle.putParcelable(AppConstants.POST, mPost);
                    shareGoodTimesDetailFragment.setArguments(bundle);

                    mFragmentManager.beginTransaction().addToBackStack("")
                            .replace(R.id.content_frame, shareGoodTimesDetailFragment).commit();
                }
                break;

            case R.id.img_user_photo:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    bundle.putString(AppConstants.USER_ID, mPostUserId);
                    UserProfileFragment userProfileFragment = new UserProfileFragment();
                    userProfileFragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().addToBackStack("")
                            .replace(R.id.content_frame, userProfileFragment).commit();
                }
                break;

            case R.id.img_play:
                mImgPost.setVisibility(View.INVISIBLE);
                mImgPlay.setVisibility(View.INVISIBLE);
                mVideoView.setVisibility(View.VISIBLE);
                mFrameVideo.setVisibility(View.VISIBLE);
                mVideoView.start();
                break;

            case R.id.txt_like_event_counter:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ID_POST, mPost.getId_post());
                    PostLikersFragment fragment = new PostLikersFragment();
                    fragment.setArguments(b);
                    mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
                }
                break;
        }
    }

    @Override
    public void positiveSelected() {
        // Delete post
        mProgressHive.show(mFragmentManager, "");
        PostBL postBL = new PostBL();
        postBL.setListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.connection_lost));
            }

            @Override
            public void onResult(HiveResponse response) {
                mProgressHive.dismiss();
                if (response.getStatus()) {
                    mFragmentManager.beginTransaction().remove(instance).commit();
                    mFragmentManager.popBackStackImmediate();
                } else {
                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), response.getMessageError());
                }
            }
        });
        postBL.deletePost(mUser.getToken(), String.valueOf(mUser.getId_user()), mPost.getId_post());
    }

    @Override
    public void negativeSelected() {
    }

    ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

        @Override
        public void HalfSwipeListView(final int position) {
            CommentsBL commentsBL = new CommentsBL();
            commentsBL.setListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                }

                @Override
                public void onResult(HiveResponse response) {
                    if (response.getStatus()) {
                        mCommentList.remove(position - 2);
                        mCommentAdapter.notifyDataSetChanged();
                        mTxtCommentEvent.setText(String.valueOf(mCommentList.size()));
                    }
                }
            });
            commentsBL.deleteComment(mUser.getToken(), String.valueOf(mCommentList.get(position - 2).getId_comment()));
        }

        @Override
        public void LoadDataForScroll(int count) {
        }

        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        }

        @Override
        public void OnClickListView(int position) {
        }
    };

    private void cleanRefreshingLabels() {
        mLvtPostList.setTextPullToRefresh("");
        mLvtPostList.setTextReleaseToRefresh("");
        mLvtPostList.setTextRefreshing("");
    }

    private void returnToLoginView() {
        if (mIsExploreMode) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_in_left, 0);
        }
    }
}