package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.CommentsBL;
import uk.co.wehive.hive.entities.Comment;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.fragment.UserProfileFragment;

public class CommentAdapter extends BaseAdapter implements IHiveResponse {

    private LayoutInflater mInflater;
    private ArrayList<Comment> mPostsList;
    private ImageView mImgComment;
    private CommentsBL mCommentBL;
    private User mUserPrefs;
    private Context mContext;
    private FragmentManager mFragmentManager;

    public CommentAdapter(Context context, ArrayList<Comment> postsList, FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        mInflater = LayoutInflater.from(context);
        mPostsList = postsList;
        mCommentBL = new CommentsBL();
        mCommentBL.setListener(this);
        mUserPrefs = ManagePreferences.getUserPreferences();
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPostsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPostsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comment_temp_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgComment = (ImageView) v;
                addLike();
            }
        });

        holder.imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = (Comment) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.USER_ID, String.valueOf(comment.getUser_id_user()));
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
            }
        });

        Comment comment = (Comment) getItem(position);
        if (comment.getUser_real_photo() != null && comment.getUser_real_photo().length() > 6) {
            Picasso.with(mInflater.getContext())
                    .load(comment.getUser_real_photo())
                    .transform(new PictureTransformer())
                    .error(R.drawable.ic_or_drawable)
                    .into(holder.imgUserPhoto);
        }

        holder.txtUserName.setText(comment.getUser_real_username());
        holder.txtPostDate.setText(getTextDate(comment));
        holder.txtPostDate.setPadding(10, 0, 0, 0);
        holder.txtPostDetail.setText(Html.fromHtml(comment.getComment()));
        holder.txtPostDetail.setMovementMethod(LinkMovementMethod.getInstance());
        holder.imgLike.setTag(comment);
        holder.imgUserPhoto.setTag(comment);
        changeImageLike(holder.imgLike, comment.getFlag_like());

        if (comment.getComment().contains("@")) {
            String commentTmp = comment.getComment();
            SpannableString ss = new SpannableString(commentTmp);

            Pattern pattern = Pattern.compile("@\\S*\\b");
            Matcher matcher = pattern.matcher(commentTmp);
            // Check all occurrences
            while (matcher.find()) {
                final String found = matcher.group();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        int userId = getIdUser(found.substring(1));

                        if (userId > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstants.USER_ID, String.valueOf(userId));
                            UserProfileFragment userProfileFragment = new UserProfileFragment();
                            userProfileFragment.setArguments(bundle);

                            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
                        } else {
                            ErrorDialog.showErrorMessage((android.app.Activity) mContext,
                                    mContext.getString(R.string.something_wrong),
                                    mContext.getString(R.string.user_not_fount));
                        }
                    }
                };
                ss.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txtPostDetail.setText(ss);
            }
        }
        return convertView;
    }

    private String getTextDate(Comment comment) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(Long.parseLong(comment.getDate_comment()) * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm:a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(instance.getTime());
    }

    private void changeImageLike(ImageView img, String state) {
        if (state.equals(AppConstants.TRUE)) {
            img.setBackgroundResource(R.drawable.ic_like_active);
        } else {
            img.setBackgroundResource(R.drawable.ic_like_inactive);
        }
    }

    private void addLike() {
        Comment comment = (Comment) mImgComment.getTag();
        mCommentBL.like(mUserPrefs.getToken(),String.valueOf(mUserPrefs.getId_user()), String.valueOf(comment.getId_comment()), changeStateComment(comment));
    }

    private String changeStateComment(Comment comment) {
        String active;
        if (comment.getFlag_like().equals(AppConstants.TRUE)) {
            active = AppConstants.FALSE;
        } else {
            active = AppConstants.TRUE;
        }
        return active;
    }

    @Override
    public void onError(RetrofitError error) {
    }

    @Override
    public void onResult(HiveResponse response) {
        Comment comment = (Comment) mImgComment.getTag();
        String state = changeStateComment(comment);
        comment.setFlag_like(state);
        mImgComment.setTag(comment);
        changeImageLike(mImgComment, state);
    }

    private int getIdUser(String userName) {
        int userId = 0;
        for (int i = 0; i < mUserPrefs.getConnections().size(); i++) {
            if (mUserPrefs.getConnections().get(i).getName().equalsIgnoreCase(userName)) {
                userId = mUserPrefs.getConnections().get(i).getId_user();
                break;
            }
        }
        return userId;
    }

    static class ViewHolder {

        @InjectView(R.id.img_user_comment)
        ImageView imgUserPhoto;
        @InjectView(R.id.img_like_event)
        ImageView imgLike;
        @InjectView(R.id.txt_user_name)
        TextView txtUserName;
        @InjectView(R.id.txt_comment_date)
        TextView txtPostDate;
        @InjectView(R.id.txt_comment_detail)
        TextView txtPostDetail;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
