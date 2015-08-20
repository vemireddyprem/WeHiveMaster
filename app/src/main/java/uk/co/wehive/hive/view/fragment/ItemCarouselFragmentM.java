package uk.co.wehive.hive.view.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.Media;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.activity.CarouselMenuActivity;
import uk.co.wehive.hive.view.dialog.PhotoCarouselOptionsDialog;

public class ItemCarouselFragmentM extends GATrackerFragment implements View.OnClickListener,
        IOptionSelected, IHiveResponse {

    @InjectView(R.id.img_user_photo)
    ImageView mImgUserPhoto;
    @InjectView(R.id.img_post_photo)
    ImageView mImgThumbnail;
    @InjectView(R.id.img_play)
    ImageView mImgPlayVideo;
    @InjectView(R.id.txt_user_name)
    CustomFontTextView mTxtUserName;
    @InjectView(R.id.txt_date)
    CustomFontTextView mTxtPhotoAge;
    @InjectView(R.id.txt_like)
    CustomFontTextView mTxtLike;
    @InjectView(R.id.video_comment)
    VideoView mVideoView;
    @InjectView(R.id.frame_video)
    FrameLayout mFrameVideo;

    private Media mMedia;
    private PostBL mPostBL;
    private FragmentManager mFragmentManager;
    private int mItemPosition;
    private User userPreference;

    public static ItemCarouselFragmentM newInstance(Media media, int position) {
        ItemCarouselFragmentM fragment = new ItemCarouselFragmentM();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.MEDIA, media);
        bundle.putInt("pos", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_carousel_adapter, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomViewHelper.setUpActionBar(getActivity(), "");
        setHasOptionsMenu(true);

        mPostBL = new PostBL();
        mPostBL.setListener(this);
        mTxtLike.setOnClickListener(this);
        mMedia = getArguments().getParcelable(AppConstants.MEDIA);
        mItemPosition = getArguments().getInt("pos");
        mImgPlayVideo.setOnClickListener(this);
        mFragmentManager = getActivity().getSupportFragmentManager();
        userPreference = ManagePreferences.getUserPreferences();
        setViewInfo();
        setVideoInfo();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                PhotoCarouselOptionsDialog dialog = new PhotoCarouselOptionsDialog();
                dialog.setTypeSelectedListener(this);
                dialog.show(mFragmentManager, null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setVideoInfo() {
        // Create a custom media controller that ignores the back button
        MediaController mMediaController = new MediaController(getActivity()) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return super.dispatchKeyEvent(event);
            }
        };

        if (mMedia.getType().equals(AppConstants.VIDEO)) {
            // Attach the media controller
            mVideoView.setVideoURI(Uri.parse(mMedia.getAbsolute_file()));
            mVideoView.setMediaController(mMediaController);
            mVideoView.seekTo(0);
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mMedia.setLiked(!mMedia.isLiked());
        setImageLike(mMedia.isLiked());
    }

    @Override
    public void onResult(HiveResponse response) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txt_like:
                addLike(mMedia);
                break;

            case R.id.img_play:
                mImgThumbnail.setVisibility(View.INVISIBLE);
                mImgPlayVideo.setVisibility(View.INVISIBLE);
                mVideoView.setVisibility(View.VISIBLE);
                mFrameVideo.setVisibility(View.VISIBLE);
                mVideoView.start();
                break;
        }
    }

    private void setViewInfo() {
        if (mMedia.getUser_photo() != null && mMedia.getUser_photo().length() > 6) {
            Picasso.with(getActivity()).load(mMedia.getUser_photo()).transform(new PictureTransformer())
                    .error(R.drawable.ic_userphoto_menu).into(mImgUserPhoto);
        }
        if (mMedia.getType().equals(AppConstants.VIDEO)) {
            mImgPlayVideo.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(mMedia.getThumbnail()).into(mImgThumbnail);
        } else if (mMedia.getType().equals(AppConstants.IMAGE)) {
            mImgPlayVideo.setVisibility(View.GONE);
            Picasso.with(getActivity()).load(mMedia.getAbsolute_file()).into(mImgThumbnail);
        }
        mTxtUserName.setText(mMedia.getFirst_name() + " " + mMedia.getLast_name() + " +++ " + mItemPosition);
        mTxtPhotoAge.setText(DateOperations.getDate(DateOperations.getDate(
                Long.parseLong(mMedia.getAge_post()) * 1000, "dd MMMM h:m:a")));
        setImageLike(mMedia.isLiked());
    }

    private void setImageLike(Boolean isLiked) {
        if (isLiked) {
            Drawable img = getActivity().getResources().getDrawable(R.drawable.ic_heart_active);
            mTxtLike.setText("Unlike");
            mTxtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            Drawable img = getActivity().getResources().getDrawable(R.drawable.ic_heart);
            mTxtLike.setText("Like");
            mTxtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
    }

    private void addLike(Media media) {
        mMedia.setLiked(!mMedia.isLiked());
        setImageLike(mMedia.isLiked());
        String state = media.isLiked() ? AppConstants.LIKED : AppConstants.UNLIKED;

        mPostBL.addLike(userPreference.getToken(), String.valueOf(userPreference.getId_user()),
                String.valueOf(media.getId_post()), state);
    }

    @Override
    public void onTypeSelected(int index) {
        Post currentPost = createPost(mMedia);

        Intent intent = new Intent(getActivity(), CarouselMenuActivity.class);
        intent.putExtra("option", index);
        intent.putExtra("currentPost", currentPost);
        startActivity(intent);
    }

    private Post createPost(Media mMedia) {
        Post post = new Post();
        post.setId_post(String.valueOf(mMedia.getId_post()));
        post.setAge_post(mMedia.getAge_post());
        post.setMedia_absolute_file(mMedia.getAbsolute_file());
        post.setMedia_absolute_thumbvideo_file(mMedia.getThumbnail());
        post.setLiked(String.valueOf(mMedia.isLiked()));
        post.setShare_link(mMedia.getShare_link());
        post.setFirst_name(mMedia.getFirst_name());
        post.setLast_name(mMedia.getLast_name());
        post.setUser_photo(mMedia.getUser_photo());
        post.setMedia_type(mMedia.getType());
        return post;
    }
}