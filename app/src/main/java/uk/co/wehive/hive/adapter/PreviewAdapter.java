package uk.co.wehive.hive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.utils.camera.ImageFilters;

public class PreviewAdapter extends PagerAdapter {

    private Context mContext;
    private Bitmap mImgInitial;
    private IOptionSelected mFilterSelected;
    private LayoutInflater mInflater;
    private List<WeakReference<LinearLayout>> viewList;
    private ViewPager pager;

    public PreviewAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewList = new ArrayList<WeakReference<LinearLayout>>();
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.filter_types).length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (pager == null) {
            pager = (ViewPager) container;
        }
        View view;

        if (viewList.size() > 0) {
            if (viewList.get(0) != null) {
                view = initView(viewList.get(0).get(), position);
                viewList.remove(0);
            }
        }
        view = initView(null, position);
        pager.addView(view);
        return view;
    }

    private View initView(LinearLayout view, int position) {

        ViewHolder viewHolder;

        if (view == null) {
            view = (LinearLayout) mInflater.inflate(R.layout.photo_thumbnail, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imgFilter.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterSelected.onTypeSelected((Integer) ((ViewHolder) v.getTag()).imgFilter.getTag());
            }
        });

        if (position < mContext.getResources().getStringArray(R.array.filter_types).length) {
            viewHolder.txtTitle.setText(mContext.getResources().getStringArray(R.array.filter_types)[position]);
            viewHolder.imgFilter.setImageBitmap(ImageFilters.setImage(position, mImgInitial));
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
        viewList.add(new WeakReference<LinearLayout>(view));
    }

    public void setImgInitial(Bitmap imgInitial) {
        this.mImgInitial = imgInitial;
    }

    public void setFilterSelected(IOptionSelected mFilterSelected) {
        this.mFilterSelected = mFilterSelected;
    }

    static class ViewHolder {
        @InjectView(R.id.img_thumbnail) ImageView imgFilter;
        @InjectView(R.id.txt_title) TextView txtTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
