package uk.co.wehive.hive.adapter;

import android.app.Activity;
import android.location.Location;
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
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.LocationHelper;

public class EventsUserAdapter extends BaseAdapter {

    private Activity mContext;
    private ArrayList<EventsUser> mListEvents;
    private Location mLocation;

    public EventsUserAdapter(Activity context, ArrayList<EventsUser> data) {
        mListEvents = data;
        mContext = context;
        mLocation = LocationHelper.getInstance(context).getLastKnownLocation();
    }

    @Override
    public int getCount() {
        return mListEvents.size();
    }

    @Override
    public Object getItem(int i) {
        return mListEvents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.events_user_adapter, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        EventsUser event = (EventsUser) getItem(i);
        Picasso.with(mContext).load(event.getThumbnail_photo()).into(holder.imgThumbnailPhoto);

        if (mLocation != null && event.getLongitude().length() > 1 && event.getLatitude().length() > 1) {
            double eventLongitude = Double.parseDouble(event.getLongitude());
            double eventLatitude = Double.parseDouble(event.getLatitude());
            holder.lblLiveEvent.setText(String.valueOf(String.format("%.2f", getDistance(eventLongitude, eventLatitude))) + " mi");
        }

        if (event.getName().length() > 17) {
            holder.lblArtist.setTextSize(11);
        } else if (event.getName().length() < 17 && event.getName().length() > 12) {
            holder.lblArtist.setTextSize(13);
        }
        holder.lblArtist.setText(event.getName());
        holder.lblVenue.setText(event.getVenue());
        holder.lblFollowins.setText(String.valueOf(event.getFollowings()));

        String eventDate = event.getEvent_date() != null ? event.getEvent_date() : event.getDate_event();
        if (eventDate != null) {
            String date = DateOperations.getDate(Long.parseLong(eventDate) * 1000, "dd-MMMM-yyyy");
            String hour = DateOperations.getDate(Long.parseLong(eventDate) * 1000, "H:mm:ss");
            holder.lblDate.setText(getDate(date));
            holder.lblHour.setText(getHour(hour));
        }
        if (event.getIs_live().equals(AppConstants.TRUE)) {
            holder.lblLiveEvent.setCompoundDrawablesWithIntrinsicBounds(
                    null, mContext.getResources().getDrawable(R.drawable.ic_live), null, null);
        } else {
            holder.lblLiveEvent.setCompoundDrawablesWithIntrinsicBounds(
                    null, mContext.getResources().getDrawable(R.drawable.ic_not_live), null, null);
        }
        return view;
    }

    private String getHour(String stringDate) {
        String[] cads = stringDate.split(":");
        return cads[0] + ":" + cads[1];
    }

    private String getDate(String stringDate) {
        String[] cads = stringDate.split("-");
        return cads[0] + " " + cads[1].substring(0, 3);
    }

    private double getDistance(double longitude, double latitude) {
        Location locationEvent = new Location("locationEvent");
        locationEvent.setLongitude(longitude);
        locationEvent.setLatitude(latitude);

        float distanceTo = mLocation.distanceTo(locationEvent);
        return distanceTo * 0.000621371192;
    }

    public void updateList(ArrayList<EventsUser> data) {
        mListEvents = data;
    }

    static class ViewHolder {

        @InjectView(R.id.img_artist_photo) ImageView imgThumbnailPhoto;
        @InjectView(R.id.txt_artist_name) TextView lblArtist;
        @InjectView(R.id.txt_event_city) TextView lblVenue;
        @InjectView(R.id.txt_event_assistants) TextView lblFollowins;
        @InjectView(R.id.txt_event_hour) TextView lblHour;
        @InjectView(R.id.txt_event_date) TextView lblDate;
        @InjectView(R.id.txt_live_event) TextView lblLiveEvent;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
