/*******************************************************************************
 PROJECT:       Hive
 FILE:          PendingPostsAdapter.java
 DESCRIPTION:
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        29/09/2014  Marcela Güiza A.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.listeners.dialogs.IConfirmation;
import uk.co.wehive.hive.listeners.dialogs.IPendingPost;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.view.dialog.ConfirmationDialog;

public class PendingPostsAdapter extends BaseAdapter implements View.OnClickListener, IConfirmation {

    private LayoutInflater mInflater;
    private ArrayList<Post> mPostsList;
    private IPendingPost pendingPostListener;
    private Post mPost;

    public PendingPostsAdapter(Context context, ArrayList<Post> data) {
        mInflater = LayoutInflater.from(context);
        mPostsList = data;
    }

    public void setPendingPostListener(IPendingPost pendingPostListener) {
        this.pendingPostListener = pendingPostListener;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pending_posts_adapter, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Post post = (Post) getItem(position);

        if (AppConstants.MEME_POST.equals(post.getPostType())) {
            Bitmap myBitmap = BitmapFactory.decodeFile(post.getPostPhotoFile().getAbsolutePath());
            holder.imgPost.setImageBitmap(myBitmap);
        } else if (AppConstants.VIDEO_POST.equals(post.getPostType())) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(post.getPostVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            holder.imgPost.setImageBitmap(thumb);
        } else {
            holder.imgPost.setVisibility(View.INVISIBLE);
        }

        holder.txtPost.setText(post.getPost_text());
        holder.imgDeletePost.setTag(post);
        holder.imgPublishPost.setTag(post);
        holder.imgDeletePost.setOnClickListener(this);
        holder.imgPublishPost.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_delete_post:
                mPost = (Post) v.getTag();
                ConfirmationDialog.showMessage(mInflater.getContext(), "",
                        mInflater.getContext().getString(R.string.delete_post), this);
                break;
            case R.id.img_publish_post:
                mPost = (Post) v.getTag();
                pendingPostListener.createPost(mPost);
                break;
        }
    }

    @Override
    public void positiveSelected() {
        pendingPostListener.deletePost(mPost);
    }

    @Override
    public void negativeSelected() {
    }

    static class Holder {

        @InjectView(R.id.img_post) ImageView imgPost;
        @InjectView(R.id.txt_post_description) CustomFontTextView txtPost;
        @InjectView(R.id.img_delete_post) ImageView imgDeletePost;
        @InjectView(R.id.img_publish_post) ImageView imgPublishPost;

        public Holder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
