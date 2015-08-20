package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import uk.co.wehive.hive.R;

public class FontSizeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> mFontSizesList;

    public FontSizeAdapter(Context context, List<String> menu) {
        mInflater = LayoutInflater.from(context);
        mFontSizesList = menu;
    }

    @Override
    public int getCount() {
        return mFontSizesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFontSizesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.font_size_adapter, null);
            holder = new ViewHolder();

            holder.txtFontStyle = (TextView) convertView.findViewById(R.id.txt_font_size);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String fontSize = mFontSizesList.get(position);
        holder.txtFontStyle.setText(fontSize);
        return convertView;
    }

    private class ViewHolder {
        private TextView txtFontStyle;
    }
}