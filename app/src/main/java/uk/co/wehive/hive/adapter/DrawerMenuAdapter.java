package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.OptionMenu;
import uk.co.wehive.hive.utils.PictureTransformer;

public class DrawerMenuAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<OptionMenu> mListMenu;

    public DrawerMenuAdapter(Context context, List<OptionMenu> menu) {
        mInflater = LayoutInflater.from(context);
        mListMenu = menu;
    }

    @Override
    public int getCount() {
        return mListMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return mListMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.menu_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OptionMenu optionMenu = mListMenu.get(position);
        String menuTitle = optionMenu.getMenuTitle();

        //Notifications and NewsFeed options menu
        if (AppConstants.NOTIFICATIONS.equals(menuTitle) || AppConstants.NEWSFEED.equals(menuTitle)) {
            holder.imgCounterIcon.setVisibility(View.VISIBLE);
            holder.txtMenuTitle.setText(menuTitle);

            int newsFeedsCounter = ManagePreferences.getNewsFeedCounterMenu();
            int notificationsCounter = ManagePreferences.getNotificationsCounterMenu();
            if (AppConstants.NOTIFICATIONS.equals(menuTitle)) {
                if (notificationsCounter > 9) {
                    holder.imgCounterIcon.setText("+9");
                } else {
                    holder.imgCounterIcon.setText(String.valueOf(notificationsCounter));
                }
            } else {
                if (newsFeedsCounter > 9) {
                    holder.imgCounterIcon.setText("+9");
                } else {
                    holder.imgCounterIcon.setText(String.valueOf(newsFeedsCounter));
                }
            }
            holder.imgMenuIcon.setImageDrawable(mInflater.getContext().getResources().getDrawable(optionMenu.getMenuImage()));
        }

        //User profile option menu
        else if (AppConstants.USER_PROFILE.equals(menuTitle)) {
            User localUser = ManagePreferences.getUserPreferences();
            convertView.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.wehive_blue));
            holder.txtMenuTitle.setText(localUser.getFirst_name() + " " + localUser.getLast_name());
            if (localUser.getPhoto() != null && localUser.getPhoto().length() > 6) {
                Picasso.with(mInflater.getContext())
                        .load(localUser.getPhoto())
                        .transform(new PictureTransformer())
                        .error(R.drawable.ic_userphoto_menu)
                        .into(holder.imgMenuIcon);
            }
            //Others options menu
        } else {
            holder.txtMenuTitle.setText(menuTitle);
            holder.imgMenuIcon.setImageDrawable(mInflater.getContext().getResources().getDrawable(optionMenu.getMenuImage()));
        }
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.img_menu_icon) ImageView imgMenuIcon;
        @InjectView(R.id.txt_menu_title) TextView txtMenuTitle;
        @InjectView(R.id.img_action_bar_counter) TextView imgCounterIcon;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}