package uk.co.wehive.hive.adapter;

import android.content.Context;
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
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.GenericSearch;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.PictureTransformer;

public class SearchResultAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<GenericSearch> mResultsList;

    public SearchResultAdapter(Context context, ArrayList<GenericSearch> resultsList) {
        mInflater = LayoutInflater.from(context);
        mResultsList = resultsList;
    }

    @Override
    public int getCount() {
        return mResultsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mResultsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_result_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GenericSearch result = (GenericSearch) getItem(position);

        final Context context = mInflater.getContext();
        if (AppConstants.LOCATION.equals(result.getImage())) {
            holder.imgSearchResult.setImageResource(R.drawable.ic_map);
        } else if (result.getImage() != null && result.getImage().length() > 6) {
            Picasso.with(context)
                    .load(result.getName())
                    .transform(new PictureTransformer())
                    .placeholder(R.drawable.ic_userphoto_menu)
                    .error(R.drawable.ic_userphoto_menu)
                    .into(holder.imgSearchResult);
        } else {
            holder.imgSearchResult.setImageResource(R.drawable.ic_userphoto_menu);
        }
        holder.txtSearchResult.setText(result.getName());

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.img_search_result) ImageView imgSearchResult;
        @InjectView(R.id.txt_search_result) CustomFontTextView txtSearchResult;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
