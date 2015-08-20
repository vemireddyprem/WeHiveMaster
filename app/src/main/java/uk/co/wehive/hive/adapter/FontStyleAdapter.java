package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.wehive.hive.R;

public class FontStyleAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> mFontSizesList;
    private List<Typeface> mFontTypesList;

    public FontStyleAdapter(Context context, List<String> menu, ArrayList<Typeface> fonts) {
        mInflater = LayoutInflater.from(context);
        mFontSizesList = menu;
        mFontTypesList = fonts;
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
        Typeface typeface = mFontTypesList.get(position);
        holder.txtFontStyle.setText(fontSize);
        holder.txtFontStyle.setTypeface(typeface);
        return convertView;
    }

    private class ViewHolder {
        private TextView txtFontStyle;
    }
}