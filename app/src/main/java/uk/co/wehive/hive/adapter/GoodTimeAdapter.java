package uk.co.wehive.hive.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.GoodTimes;
import uk.co.wehive.hive.utils.DateOperations;

public class GoodTimeAdapter extends BaseAdapter {

    private List<GoodTimes> mGoodTimeList = new ArrayList<GoodTimes>();
    private Activity mContext;

    public GoodTimeAdapter(Fragment context, List<GoodTimes> goodTimesList, int size) {
        mGoodTimeList = goodTimesList;
        mContext = context.getActivity();
    }

    @Override
    public int getCount() {
        return mGoodTimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGoodTimeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.good_time_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GoodTimes goodTime = mGoodTimeList.get(position);
        holder.txtNumPost.setText(goodTime.getPosts() + "");
        holder.txtName.setText(goodTime.getName());
        holder.txtPlace.setText(goodTime.getVenue() + ", " + goodTime.getCity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate;
        try {
            convertedDate = dateFormat.parse(goodTime.getDate_event() + " " + goodTime.getTime_event());
            holder.txtDate.setText(DateOperations.getDate(DateOperations.getDate(convertedDate.getTime(), "dd MMMM h:m:a")));
        } catch (ParseException ignore) {
        }
        Picasso.with(mContext).load(goodTime.getAbsolute_photo()).into(holder.imgGoodTime);
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.img_good_time) ImageView imgGoodTime;
        @InjectView(R.id.lbl_num_post) TextView txtNumPost;
        @InjectView(R.id.lbl_name) TextView txtName;
        @InjectView(R.id.lbl_place) TextView txtPlace;
        @InjectView(R.id.lbl_date) TextView txtDate;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
