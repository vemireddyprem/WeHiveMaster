package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.FollowUser;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.FollowUserResponse;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.CustomFontButton;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;

public class NetworkMemberAdapter extends BaseAdapter implements IHiveResponse {

    private static final int FOLLOWED_USER = 1;
    private static final int NOT_FOLLOWED_USER = 0;

    private LayoutInflater mInflater;
    private List<Follower> mNetworkList;
    private UsersBL mUsersBL;
    private User mUSer;
    private final String TAG = "NetworkMemberAdapter";

    public NetworkMemberAdapter(Context context, ArrayList<Follower> networkList) {
        mInflater = LayoutInflater.from(context);
        mNetworkList = networkList;
        mUsersBL = new UsersBL();
        mUsersBL.setHiveListener(this);
        mUSer = ManagePreferences.getUserPreferences();
    }

    @Override
    public int getCount() {
        return mNetworkList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNetworkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Follower) getItem(position)).getId_user();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.network_member_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Follower follower = (Follower) getItem(position);

        final Context context = mInflater.getContext();
        if (follower.getReal_photo() != null && follower.getReal_photo().length() > 6) {
            Picasso.with(context)
                    .load(follower.getReal_photo())
                    .transform(new PictureTransformer())
                    .placeholder(R.drawable.ic_userphoto_menu)
                    .error(R.drawable.ic_userphoto_menu)
                    .into(holder.imgMemberPhoto);
        } else {
            holder.imgMemberPhoto.setImageResource(R.drawable.ic_userphoto_menu);
        }
        String username = follower.getUsername();
        String premium = follower.getPremium();
        Log.e("PREMIUM",premium);
        if(!premium.equalsIgnoreCase("false"))
        {
            holder.imgMemberStatus = (ImageView)convertView.findViewById(R.id.img_member_status);
            holder.imgMemberStatus.setVisibility(View.VISIBLE);
        }
        holder.txtMemberUserName.setVisibility((username != null) ? View.VISIBLE : View.GONE);
        holder.txtMemberUserName.setText(username);
        holder.txtMemberFullName.setText(follower.getFirst_name() + " " + follower.getLast_name());

        if (mUSer.getId_user() == follower.getId_user()) {
            holder.btnMemberFollowed.setVisibility(View.GONE);
        } else {

            if (follower.isIs_blocked()) {
                holder.btnMemberFollowed.setText(R.string.unblock);
                holder.btnMemberFollowed.setBackgroundResource(R.drawable.btn_unbloked);
                holder.btnMemberFollowed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Follower f = (Follower) v.getTag();
                        holder.btnMemberFollowed.setText(context.getString(R.string.follow));
                        holder.btnMemberFollowed.setBackgroundResource(R.drawable.bt_followed_people_background);
                        f.setFollow(NOT_FOLLOWED_USER);
                        f.setIs_blocked(false);

                        UsersBL usersBL = new UsersBL();
                        usersBL.setHiveListener(new IHiveResponse() {
                            @Override
                            public void onError(RetrofitError error) {
                            }

                            @Override
                            public void onResult(HiveResponse response) {
                                notifyDataSetChanged();
                            }
                        });

                        usersBL.unblockUser(mUSer.getToken(), String.valueOf(mUSer.getId_user()),
                                String.valueOf(f.getId_user()));
                    }
                });
            } else {
                holder.btnMemberFollowed.setVisibility(View.VISIBLE);
                holder.btnMemberFollowed.setText(follower.getFollow() == FOLLOWED_USER ?
                        context.getString(R.string.following) :
                        context.getString(R.string.follow));
                holder.btnMemberFollowed.setBackgroundResource(follower.getFollow() == FOLLOWED_USER ?
                        R.drawable.bt_following_people_background :
                        R.drawable.bt_followed_people_background);

                holder.btnMemberFollowed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Follower f = (Follower) v.getTag();
                        holder.btnMemberFollowed.setText(f.getFollow() == FOLLOWED_USER ?
                                context.getString(R.string.follow) :
                                context.getString(R.string.following));
                        holder.btnMemberFollowed.setBackgroundResource(f.getFollow() == FOLLOWED_USER ?
                                R.drawable.bt_followed_people_background :
                                R.drawable.bt_following_people_background);
                        f.setFollow(f.getFollow() == NOT_FOLLOWED_USER ? FOLLOWED_USER : NOT_FOLLOWED_USER);

                        mUsersBL.followUser(mUSer.getToken(), String.valueOf(mUSer.getId_user()),
                                String.valueOf(f.getId_user()), String.valueOf(f.getFollow()));
                    }
                });
            }
        }

        holder.btnMemberFollowed.setTag(follower);
        holder.btnMemberFollowed.setTag(R.string.member_network_position, position);

        return convertView;
    }

    @Override
    public void onError(RetrofitError error) {
    }

    @Override
    public void onResult(HiveResponse response) {
        try {
            FollowUser followUser = ((FollowUserResponse) response).getData();
            mUSer.setConnections(followUser.getConnections());

            ManagePreferences.setPreferencesUser(mUSer);

            notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "error " + e.getMessage());
        }
    }

    public void updateData(ArrayList<Follower> networkList) {
        mNetworkList = networkList;
        notifyDataSetChanged();
    }

    static class ViewHolder {

        @InjectView(R.id.img_member_photo)
        ImageView imgMemberPhoto;
        //This imageview is for the tick mark.
        ImageView imgMemberStatus;
        @InjectView(R.id.txt_member_username)
        CustomFontTextView txtMemberUserName;
        @InjectView(R.id.txt_member_fullname)
        CustomFontTextView txtMemberFullName;
        @InjectView(R.id.btn_member_followed)
        CustomFontButton btnMemberFollowed;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}