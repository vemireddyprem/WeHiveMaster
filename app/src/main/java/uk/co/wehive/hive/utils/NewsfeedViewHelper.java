package uk.co.wehive.hive.utils;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.NewsFeed;
import uk.co.wehive.hive.entities.NewsfeedDetail;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.listeners.INewsFeedEventsListener;
import uk.co.wehive.hive.view.fragment.PostLikersFragment;
import uk.co.wehive.hive.view.fragment.ShareGoodTimesDetailFragment;
import uk.co.wehive.hive.view.fragment.UserProfileFragment;

public class NewsfeedViewHelper implements View.OnClickListener {

    private static INewsFeedEventsListener mActionListener;
    private FragmentActivity mContext;

    public NewsfeedViewHelper(FragmentActivity context) {
        mContext = context;
    }

    public void setListener(INewsFeedEventsListener actionListener) {
        mActionListener = actionListener;
    }

    public View getPostView(View view, final NewsFeed newsfeed) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.newsfeed_post_row, null);
            holder = new ViewHolder();
            holder.rltGoodTimes = view.findViewById(R.id.lyt_good_times_row);
            holder.imgUSer = (ImageView) view.findViewById(R.id.img_user_photo);
            holder.imgUSer.setTag(newsfeed);
            holder.lblName = (TextView) view.findViewById(R.id.txt_user_username);
            holder.lblName.setTypeface(null, Typeface.BOLD);
            holder.lblPost = (TextView) view.findViewById(R.id.txt_message);
            holder.lblDate = (TextView) view.findViewById(R.id.txt_age_newsfeed);
            holder.imgPost = (ImageView) view.findViewById(R.id.img_comment_photo);
            holder.imgPlay = (ImageView) view.findViewById(R.id.img_play);
            holder.txtLikePost = (TextView) view.findViewById(R.id.txt_like_event);
            holder.txtLikePostCounter = (TextView) view.findViewById(R.id.txt_like_event_counter);
            holder.lblComments = (TextView) view.findViewById(R.id.txt_comment_event);
            holder.lblShares = (TextView) view.findViewById(R.id.txt_share_event);
            holder.imgUSer.setOnClickListener(this);
            holder.txtLikePost.setOnClickListener(this);
            holder.txtLikePostCounter.setOnClickListener(this);
            holder.lblShares.setOnClickListener(this);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setUserPremiumBackground(newsfeed, holder);

        holder.lblName.setText(newsfeed.getFullname());
        holder.lblDate.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(String.valueOf(newsfeed.getAge_newsfeed())) * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        holder.imgUSer.setTag(newsfeed);
        holder.lblPost.setText(newsfeed.getMessage());
        holder.imgUSer.setTag(newsfeed);
        holder.txtLikePost.setTag(newsfeed);
        holder.txtLikePostCounter.setTag(newsfeed);
        holder.txtLikePost.setTag(R.string.likes_counter_tag, holder.txtLikePostCounter);
        holder.lblShares.setTag(newsfeed);

        holder.txtLikePostCounter.setText(String.valueOf(newsfeed.getDetail().getTotal_likes()));
        setLikePostImage(newsfeed, holder.txtLikePost);
        holder.lblComments.setText(String.valueOf(newsfeed.getDetail().getTotal_comments()));
        holder.lblShares.setText(String.valueOf(newsfeed.getDetail().getTotal_shares()));
        setImage(newsfeed.getPhoto(), true, R.drawable.ic_userphoto_menu, holder.imgUSer);

        holder.imgPlay.setVisibility(View.GONE);
        String postMediaType = newsfeed.getDetail().getPost_media_type();
        if (postMediaType == null) {
            holder.imgPost.setVisibility(View.GONE);
        } else if (postMediaType.toLowerCase().equals(AppConstants.IMAGE)) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(newsfeed.getDetail().getPost_media()).into(holder.imgPost);
        } else if (postMediaType.toLowerCase().equals(AppConstants.VIDEO)) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(newsfeed.getDetail().getVideo_thumb()).into(holder.imgPost);
            holder.imgPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }
        return view;
    }

    private void setLikePostImage(NewsFeed newsfeed, TextView holder) {
        if (newsfeed.getDetail().isHas_liked()) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_heart_active);
            holder.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_heart);
            holder.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
    }

    public View getCheckInView(View view, NewsFeed newsfeed) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.newsfeed_check_in_row, null);
            holder = new ViewHolder();
            holder.rltGoodTimes = view.findViewById(R.id.lyt_good_times_row);
            holder.imgUSer = (ImageView) view.findViewById(R.id.img_user_photo);
            holder.lblName = (TextView) view.findViewById(R.id.txt_user_name);
            holder.lblName.setTypeface(null, Typeface.BOLD);
            holder.lblDate = (TextView) view.findViewById(R.id.txt_age_newsfeed);
            holder.lblPost = (TextView) view.findViewById(R.id.txt_message);
            holder.imgPost = (ImageView) view.findViewById(R.id.img_event_photo);
            holder.imgUSer.setOnClickListener(this);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        setUserPremiumBackground(newsfeed, holder);

        holder.lblName.setText(newsfeed.getUsername());
        holder.lblPost.setText(newsfeed.getMessage());
        holder.lblDate.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(newsfeed.getAge_newsfeed())) * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        holder.imgUSer.setTag(newsfeed);
        setImage(newsfeed.getPhoto(), true, R.drawable.ic_userphoto_menu, holder.imgUSer);
        setImage(newsfeed.getDetail().getEvents_photo(), false, R.drawable.default_newsfeed_image, holder.imgPost);

        return view;
    }

    public View getGoodTimesView(View view, NewsFeed newsfeed) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.newsfeed_good_times_row, null);
            holder = new ViewHolder();
            holder.rltGoodTimes = view.findViewById(R.id.lyt_good_times_row);
            holder.imgUSer = (ImageView) view.findViewById(R.id.img_user_photo);
            holder.lblName = (TextView) view.findViewById(R.id.txt_user_name);
            holder.lblName.setTypeface(null, Typeface.BOLD);
            holder.lblPost = (TextView) view.findViewById(R.id.txt_message);
            holder.lblDate = (TextView) view.findViewById(R.id.txt_age_newsfeed);
            holder.imgPost = (ImageView) view.findViewById(R.id.img_event_photo);
            holder.txtEventName = (CustomFontTextView) view.findViewById(R.id.txt_event_name);
            holder.txtEventLocation = (CustomFontTextView) view.findViewById(R.id.txt_event_location);
            holder.txtEventDate = (CustomFontTextView) view.findViewById(R.id.txt_event_date);
            holder.imgUSer.setOnClickListener(this);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        setUserPremiumBackground(newsfeed, holder);

        holder.lblName.setText(newsfeed.getFullname());
        holder.lblPost.setText(newsfeed.getMessage());
        holder.lblDate.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(newsfeed.getAge_newsfeed())) * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        holder.imgUSer.setTag(newsfeed);
        holder.imgUSer.setTag(newsfeed);
        holder.txtEventName.setText(newsfeed.getDetail().getEvent_name());
        holder.txtEventLocation.setText(newsfeed.getDetail().getEvent_venue() + " " + newsfeed.getDetail().getCity());
        holder.txtEventDate.setText(DateOperations.getDate(DateOperations.getDate(newsfeed.getDetail().getAge_event() * 1000, "dd MMMM h:m:a")));
        setImage(newsfeed.getPhoto(), true, R.drawable.ic_userphoto_menu, holder.imgUSer);
        setImage(newsfeed.getDetail().getEvents_photo(), false, R.drawable.default_newsfeed_image, holder.imgPost);
        return view;
    }

    private void setUserPremiumBackground(NewsFeed newsfeed, ViewHolder holder) {

        boolean isUserPremium = newsfeed.isPremium();
        holder.rltGoodTimes.setBackgroundColor(isUserPremium ?
                mContext.getResources().getColor(R.color.user_premium_posts_background) :
                mContext.getResources().getColor(android.R.color.transparent));
        holder.lblName.setTextColor(isUserPremium ?
                mContext.getResources().getColor(R.color.user_premium_posts_name) :
                mContext.getResources().getColor(android.R.color.white));
        Drawable premiumDrawable = isUserPremium ?
                mContext.getResources().getDrawable(R.drawable.ic_star_blue) : null;
        holder.lblName.setCompoundDrawablesWithIntrinsicBounds(null, null, premiumDrawable, null);
    }

    private void setImage(String image, boolean transformImage, int defaultDrawable, ImageView imageView) {
        if (!image.isEmpty()) {
            if (transformImage) {
                Picasso.with(mContext).load(image).transform(new PictureTransformer())
                        .error(defaultDrawable).into(imageView);
            } else {
                Picasso.with(mContext).load(image).error(defaultDrawable).into(imageView);
            }
        }
    }

    private void addEventLike(TextView lblLike) {
        NewsFeed newsFeed = (NewsFeed) lblLike.getTag();
        TextView lblCounterLikes = (TextView) lblLike.getTag(R.string.likes_counter_tag);
        String active;
        int likes = Integer.parseInt(lblCounterLikes.getText().toString());
        if (newsFeed.getDetail().isHas_liked()) {
            likes--;
            active = AppConstants.UNLIKED;
        } else {
            likes++;
            active = AppConstants.LIKED;
        }
        newsFeed.getDetail().setTotal_likes(likes);
        newsFeed.getDetail().setHas_liked(!newsFeed.getDetail().isHas_liked());
        setLikePostImage(newsFeed, lblLike);
        lblCounterLikes.setText(String.valueOf(likes));
        mActionListener.setAction(newsFeed, AppConstants.LIKE_POST_SERVICE, active);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_user_photo:
                NewsFeed nf = (NewsFeed) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.USER_ID, String.valueOf(nf.getId_user()));
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                mContext.getSupportFragmentManager().beginTransaction()
                        .addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
                break;

            case R.id.txt_like_event:
                addEventLike((TextView) v);
                break;

            case R.id.txt_share_event:
                goToShareGoodTimes((NewsFeed) v.getTag());
                break;

            case R.id.txt_like_event_counter:
                NewsFeed newsFeed = (NewsFeed) v.getTag();
                Bundle b = new Bundle();
                b.putString(AppConstants.ID_POST, String.valueOf(newsFeed.getDetail().getId_post()));
                PostLikersFragment fragment = new PostLikersFragment();
                fragment.setArguments(b);
                mContext.getSupportFragmentManager().beginTransaction()
                        .addToBackStack("").replace(R.id.content_frame, fragment).commit();
                break;
        }
    }

    private void goToShareGoodTimes(NewsFeed newsFeed) {
        Post post = new Post();
        NewsfeedDetail detail = newsFeed.getDetail();

        post.setPost_text(newsFeed.getMessage());
        post.setId_post(String.valueOf(detail.getId_post()));
        post.setAge_post(String.valueOf(detail.getAge_event()));
        post.setMedia_absolute_file(detail.getPost_media());
        post.setMedia_video_thumbnail(detail.getVideo_thumb());
        post.setLiked(String.valueOf(detail.isHas_liked()));
        post.setShare_link(detail.getShare_link());
        post.setFirst_name(newsFeed.getUsername());
        post.setLast_name("");
        post.setUser_photo(newsFeed.getPhoto());
        post.setMedia_type(newsFeed.getDetail().getPost_media_type());

        ShareGoodTimesDetailFragment shareGoodTimesDetailFragment = new ShareGoodTimesDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.POST, post);
        shareGoodTimesDetailFragment.setArguments(bundle);

        mContext.getSupportFragmentManager()
                .beginTransaction().addToBackStack("")
                .replace(R.id.content_frame, shareGoodTimesDetailFragment)
                .commit();
    }

    private class ViewHolder {
        private View rltGoodTimes;
        private ImageView imgUSer;
        private TextView lblName;
        private TextView lblPost;
        private TextView lblDate;
        private TextView txtLikePost;
        private TextView txtLikePostCounter;
        private TextView lblComments;
        private TextView lblShares;
        private ImageView imgPost;
        private ImageView imgPlay;
        private CustomFontTextView txtEventName;
        private CustomFontTextView txtEventLocation;
        private CustomFontTextView txtEventDate;
    }
}