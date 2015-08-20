package uk.co.wehive.hive.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import uk.co.wehive.hive.entities.NewsFeed;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.NewsfeedViewHelper;

public class NewsfeedAdapter extends BaseAdapter {

    private List<NewsFeed> mNewsfeedList;
    private NewsfeedViewHelper mNewsfeedDrawerHelper;

    public NewsfeedAdapter(FragmentActivity context, List<NewsFeed> newsfeedList) {
        mNewsfeedList = newsfeedList;
        mNewsfeedDrawerHelper = new NewsfeedViewHelper(context);
    }

    @Override
    public int getCount() {
        return mNewsfeedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsfeedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNewsfeedList.get(position).getId_newsfeed();
    }

    @Override
    public int getItemViewType(int position) {
        NewsFeed newsFeed = (NewsFeed) getItem(position);
        if (AppConstants.CHECK_IN_NEWSFEED.equals(newsFeed.getType_newsfeed())) {
            return 0;
        } else if (AppConstants.POST_CREATED_NEWSFEED.equals(newsFeed.getType_newsfeed())) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsFeed newsFeed = (NewsFeed) getItem(position);
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            return mNewsfeedDrawerHelper.getCheckInView(convertView, newsFeed);
        } else if (viewType == 1) {
            return mNewsfeedDrawerHelper.getPostView(convertView, newsFeed);
        } else {
            return mNewsfeedDrawerHelper.getGoodTimesView(convertView, newsFeed);
        }
    }

    public void updateNewsFeedList(List<NewsFeed> newsfeedList) {
        mNewsfeedList = newsfeedList;
    }
}