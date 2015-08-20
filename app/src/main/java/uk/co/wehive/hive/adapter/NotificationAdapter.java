package uk.co.wehive.hive.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.NotificationMessage;
import uk.co.wehive.hive.utils.PictureTransformer;

public class NotificationAdapter extends BaseAdapter {

    private Activity mContext;
    private ArrayList<NotificationMessage> mListNotifications;

    public NotificationAdapter(Activity context, ArrayList<NotificationMessage> data) {
        mListNotifications = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mListNotifications.size();
    }

    @Override
    public Object getItem(int i) {
        return mListNotifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.network_notification_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NotificationMessage notificationBody = (NotificationMessage) getItem(position);
        if (notificationBody.getPhoto() != null && notificationBody.getPhoto().length() > 0) {
            Picasso.with(mContext).load(notificationBody.getPhoto()).transform(new PictureTransformer())
                    .error(R.drawable.ic_userphoto_menu).into(holder.imguser);
        }

        String origin = notificationBody.getOrigin();
        String username = notificationBody.getUsername();

        holder.imguser.setVisibility(View.VISIBLE);
        holder.txtusername.setVisibility(View.VISIBLE);

        if (origin.equals("goodtime") || username.equals("")) {
            holder.imguser.setVisibility(View.GONE);
            holder.txtusername.setVisibility(View.GONE);
        }

        holder.txtusername.setText(notificationBody.getUsername());
        holder.txtmembernorification.setText(notificationBody.getMessage());
        holder.txttime.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(notificationBody.getAge_notification())) * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        return convertView;
    }

    public void updateNotificationsList(ArrayList<NotificationMessage> notificationsList) {
        mListNotifications = notificationsList;
    }

    static class ViewHolder {

        @InjectView(R.id.img_member_photo) ImageView imguser;
        @InjectView(R.id.txt_member_username) TextView txtusername;
        @InjectView(R.id.txt_member_activity_notification) TextView txtmembernorification;
        @InjectView(R.id.txt_time) TextView txttime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
