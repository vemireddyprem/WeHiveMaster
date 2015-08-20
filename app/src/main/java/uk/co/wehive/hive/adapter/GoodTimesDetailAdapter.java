package uk.co.wehive.hive.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.listeners.dialogs.IGoodTimesOptions;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.fragment.PostLikersFragment;
import uk.co.wehive.hive.view.fragment.UserProfileFragment;

public class GoodTimesDetailAdapter extends BaseAdapter {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String LIKED = "1";
    private static final String UNLIKED = "0";

    private Activity mContext;
    private ArrayList<Post> mData;
    private IGoodTimesOptions optionListener;
    private FragmentManager mFragmentManager;
    private boolean mIsExploreMode;

    public void setListener(IGoodTimesOptions optionListener) {
        this.optionListener = optionListener;
    }

    public GoodTimesDetailAdapter(Fragment context, ArrayList<Post> data) {
        this.mContext = context.getActivity();
        this.mData = data;
        mFragmentManager = context.getActivity().getSupportFragmentManager();
    }

    // Only for EXPLORE mode
    public GoodTimesDetailAdapter(Fragment context, ArrayList<Post> data, boolean isExploreMode) {
        mIsExploreMode = isExploreMode;
        this.mContext = context.getActivity();
        this.mData = data;
        mFragmentManager = context.getActivity().getSupportFragmentManager();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.good_time_parent_adapter_layout, null);

            holder = new ViewHolder(view);
            holder.lblName.setTypeface(null, Typeface.BOLD);
            holder.lblLikesCounter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) v.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.ID_POST, post.getId_post());
                    PostLikersFragment fragment = new PostLikersFragment();
                    fragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
                }
            });

            holder.lblLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtCounterLikes = (TextView) v.getTag(R.string.likes_counter_tag);
                    addEventLike((TextView) v, txtCounterLikes);
                }
            });

            holder.lblShares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) v.getTag();
                    if (mIsExploreMode) {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.explore, "");
                    } else {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.share, "");
                    }
                }
            });

            holder.lblDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) v.getTag();
                    if (mIsExploreMode) {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.explore, "");
                    } else {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.comments, "");
                    }
                }
            });

            holder.lblComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) v.getTag();
                    if (mIsExploreMode) {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.explore, "");
                    } else {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.comments, "");
                    }
                }
            });
            holder.imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) v.getTag();
                    if (mIsExploreMode) {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.explore, "");
                    } else {
                        optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.comments, "");
                    }
                }
            });
            holder.imgUSer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsExploreMode) {
                        Post post = (Post) v.getTag();
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.USER_ID, post.getId_user());
                        UserProfileFragment userProfileFragment = new UserProfileFragment();
                        userProfileFragment.setArguments(bundle);
                        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
                    }
                }
            });

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Post post = (Post) getItem(position);
        if (!post.getUser_photo().isEmpty()) {
            Picasso.with(mContext).load(post.getUser_photo()).transform(new PictureTransformer()).error(R.drawable.ic_userphoto_menu).into(holder.imgUSer);
        } else {
            holder.imgUSer.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_userphoto_menu));
        }

        // Change view details if user is premium
        boolean isUserPremium = post.isPremium();
        holder.rltGoodTimes.setBackgroundColor(isUserPremium ?
                mContext.getResources().getColor(R.color.user_premium_posts_background) :
                mContext.getResources().getColor(android.R.color.transparent));
        holder.lblName.setTextColor(isUserPremium ?
                mContext.getResources().getColor(R.color.user_premium_posts_name) :
                mContext.getResources().getColor(android.R.color.white));
        Drawable premiumDrawable = isUserPremium ?
                mContext.getResources().getDrawable(R.drawable.ic_star_blue) : null;
        holder.lblName.setCompoundDrawablesWithIntrinsicBounds(null, null, premiumDrawable, null);

        // Set general information
        holder.lblName.setText(post.getFirst_name() + " " + post.getLast_name());
        holder.lblComments.setText(post.getComments());
        holder.lblLikesCounter.setText(post.getLikes());
        holder.lblShares.setText(post.getShares());
        holder.lblDate.setText(getTextDate(post));
        holder.lblLikes.setTag(post);
        holder.lblLikes.setTag(R.string.likes_counter_tag, holder.lblLikesCounter);
        holder.lblLikesCounter.setTag(post);
        holder.lblShares.setTag(post);
        holder.lblComments.setTag(post);
        holder.imgPost.setTag(post);
        holder.imgUSer.setTag(post);
        holder.imgPlay.setVisibility(View.GONE);

        if (post.getMedia_type() == null) {
            holder.imgPost.setVisibility(View.GONE);
        } else if (post.getMedia_type().toLowerCase().equals("image")) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(post.getMedia_absolute_file()).into(holder.imgPost);
        } else if (post.getMedia_type().toLowerCase().equals("video")) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(post.getMedia_absolute_thumbvideo_file()).into(holder.imgPost);
            holder.imgPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }


        if (post.getPost_text() != null && post.getPost_text().length() > 0) {
            holder.lblPost.setText(Html.fromHtml(post.getPost_text()));
        } else {
            holder.lblPost.setText(Html.fromHtml(post.getText()));
        }

        holder.lblPost.setMovementMethod(LinkMovementMethod.getInstance());
        setImageLike(post, holder.lblLikes);
        return view;
    }

    private void setImageLike(Post post, TextView textView) {
        if (post.getLiked().equals(TRUE) || post.getLiked().equals(LIKED)) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_heart_active);
            textView.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_heart);
            textView.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
    }

    private void addEventLike(TextView lblLike, TextView txtCounterLikes) {
        Post post = (Post) lblLike.getTag();
        if (mIsExploreMode) {
            optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.explore, "");
        } else {
            String active;
            int likes = Integer.parseInt(txtCounterLikes.getText().toString());
            if (post.getLiked().equals(TRUE) || post.getLiked().equals(LIKED)) {
                likes--;
                active = UNLIKED;
            } else {
                likes++;
                active = LIKED;
            }
            post.setLikes(String.valueOf(likes));
            post.setLiked((post.getLiked().equals(TRUE) || post.getLiked().equals(LIKED)) ? FALSE : TRUE);
            setImageLike(post, lblLike);
            txtCounterLikes.setText(String.valueOf(likes));
            optionListener.setAction(post, AppConstants.OPTIONS_GOOD_TIMES.like, active);
        }
    }

    private String getTextDate(Post post) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(Long.parseLong(post.getAge_post()) * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, h:mm:a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String postHour = sdf.format(instance.getTime());

        String agePublishPost = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(post.getAge_post())) * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        if (!agePublishPost.toLowerCase().contains("ago") && !agePublishPost.toLowerCase().contains("hace")) {
            agePublishPost = postHour;
        }
        return agePublishPost;
    }

    static class ViewHolder {

        @InjectView(R.id.lyt_good_times_row)
        LinearLayout rltGoodTimes;
        @InjectView(R.id.txt_comment)
        TextView lblPost;
        @InjectView(R.id.img_user_photo)
        ImageView imgUSer;
        @InjectView(R.id.img_comment_photo)
        ImageView imgPost;
        @InjectView(R.id.img_play)
        ImageView imgPlay;
        @InjectView(R.id.txt_user_username)
        TextView lblName;
        @InjectView(R.id.txt_date_comment)
        TextView lblDate;
        @InjectView(R.id.txt_comment_event)
        TextView lblComments;
        @InjectView(R.id.txt_like_event_counter)
        TextView lblLikesCounter;
        @InjectView(R.id.txt_like_event)
        TextView lblLikes;
        @InjectView(R.id.txt_share_event)
        TextView lblShares;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
