package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.FontStyleAdapter;
import uk.co.wehive.hive.entities.EventDetail;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.SquareRelativeLayout;
import uk.co.wehive.hive.utils.camera.ScreenshotHelper;
import uk.co.wehive.hive.view.dialog.ProgressHive;
import uk.co.wehive.hive.view.dialog.TextSizeListDialog;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MemeCreatorFragment extends GATrackerFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, TextSizeListDialog.IListItemClick,
        AmbilWarnaDialog.OnAmbilWarnaListener, View.OnFocusChangeListener {

    @InjectView(R.id.rlt_meme) SquareRelativeLayout mRlyMemeScreen;
    @InjectView(R.id.imv_meme_image) ImageView mMemeImage;
    @InjectView(R.id.edt_top_text) EditText mEdtTopText;
    @InjectView(R.id.edt_bottom_text) EditText mEdtBottomText;
    @InjectView(R.id.btn_font_size_selector) Button mBtnFontSize;
    @InjectView(R.id.spn_font_selector) Spinner mSpnFont;
    @InjectView(R.id.btn_color_selector) Button mBtnFontColor;

    private ArrayList<Typeface> mFontsList;
    private AmbilWarnaDialog mColorPicker;
    private EventDetail mEventDetails;
    private TextSizeListDialog mTextSizeDialog;
    private ProgressHive mProgressBar;
    private boolean mIsAPreview;
    private boolean mIsTopSelected;
    private boolean mIsBottomSelected;
    private int mColorTop;
    private int mColorBottom;
    private int mTextSizeTop;
    private int mTextSizeBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meme_creator_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        CustomViewHelper.setUpActionBar(getActivity(), "");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mEventDetails = getArguments().getParcelable(AppConstants.EVENT_DATA);
        mProgressBar = new ProgressHive();

        mFontsList = new ArrayList<Typeface>();
        mTextSizeDialog = new TextSizeListDialog();
        mTextSizeDialog.setItemSelectedListener(this);
        mColorPicker = new AmbilWarnaDialog(getActivity(), 0xff0000ff, this);
        if (!mIsAPreview) {
            initComponents();
        }
        setListeners();

        setMemeImage();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_font_size_selector:
                mTextSizeDialog.show(getActivity().getSupportFragmentManager(), "");
                break;

            case R.id.btn_color_selector:
                mColorPicker.show();
                break;

            case R.id.rlt_meme:
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.share_good_times, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuPost:
                createPost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mIsTopSelected) {
            mEdtTopText.setTypeface(mFontsList.get(position));
            mEdtTopText.setTypeface(mFontsList.get(position));
        } else if (mIsBottomSelected) {
            mEdtBottomText.setTypeface(mFontsList.get(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onListItemSelected(int selectedItem) {
        if (mIsTopSelected) {
            mTextSizeTop = selectedItem;
            mEdtTopText.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedItem);
        } else if (mIsBottomSelected) {
            mTextSizeBottom = selectedItem;
            mEdtBottomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedItem);
        } else {
            mTextSizeTop = selectedItem;
            mTextSizeBottom = selectedItem;
            mEdtTopText.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedItem);
            mEdtBottomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedItem);
        }
    }

    private void setListeners() {
        mEdtTopText.setOnFocusChangeListener(this);
        mEdtTopText.setOnClickListener(this);
        mEdtBottomText.setOnClickListener(this);
        mEdtBottomText.setOnFocusChangeListener(this);
        mBtnFontSize.setOnClickListener(this);
        mBtnFontColor.setOnClickListener(this);
        mRlyMemeScreen.setOnClickListener(this);
        mSpnFont.setOnItemSelectedListener(this);

        mSpnFont.setAdapter(new FontStyleAdapter(getActivity(), Arrays.asList(getFontsList()), mFontsList));
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {
    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        if (mIsTopSelected) {
            mColorTop = color;
            mEdtTopText.setTextColor(color);
        } else if (mIsBottomSelected) {
            mColorBottom = color;
            mEdtBottomText.setTextColor(color);
        } else {
            mColorTop = color;
            mColorBottom = color;
            mEdtTopText.setTextColor(color);
            mEdtBottomText.setTextColor(color);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            switch (v.getId()) {
                case R.id.edt_top_text:
                    mIsTopSelected = true;
                    mIsBottomSelected = false;
                    break;

                case R.id.edt_bottom_text:
                    mIsTopSelected = false;
                    mIsBottomSelected = true;
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIsAPreview) {
            setPreviewMemePrefs();
        }
    }

    private String[] getFontsList() {
        String[] fontsList = {};
        try {
            fontsList = getActivity().getResources().getAssets().list("fonts");
            for (int i = 0; i < fontsList.length; i++) {
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + fontsList[i]);
                mFontsList.add(font);
                fontsList[i] = fontsList[i].subSequence(0, fontsList[i].length() - 4).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontsList;
    }

    private void createPost() {
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");

        mIsAPreview = true;
        validateMemeTexts();

        String memePath = ScreenshotHelper.takeScreenshot(mRlyMemeScreen);

        CreatePostFragment fragment = new CreatePostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
        bundle.putBoolean(AppConstants.FROM_MEME, true);
        bundle.putBoolean(AppConstants.FROM_VIDEO, false);
        bundle.putString(AppConstants.MEME_PHOTO_PATH, memePath);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(AppConstants.MEME_CREATOR_FRAGMENT).commit();

        mProgressBar.dismiss();
    }

    private void setMemeImage() {
        File imgFile = new File(AppConstants.FILTER_PATH);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Drawable drawable = new BitmapDrawable(getResources(), myBitmap);
            mMemeImage.setImageDrawable(drawable);
        }
    }

    private void validateMemeTexts() {
        if (mEdtTopText.getText().toString().length() < 1) {
            mEdtTopText.setHint("");
        }
        if (mEdtBottomText.getText().toString().length() < 1) {
            mEdtBottomText.setHint("");
        }
        mEdtTopText.setCursorVisible(false);
        mEdtBottomText.setCursorVisible(false);
    }

    private void setPreviewMemePrefs() {
        if (mEdtTopText.getText().toString().length() < 1) {
            mEdtTopText.setHint(getString(R.string.top_text));
        } else {
            mEdtTopText.setTextColor(mColorTop);
            mEdtTopText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeTop);
        }
        if (mEdtBottomText.getText().toString().length() < 1) {
            mEdtBottomText.setHint(getString(R.string.bottom_text));
        } else {
            mEdtBottomText.setTextColor(mColorBottom);
            mEdtBottomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeBottom);
        }
    }

    private void initComponents() {
        mColorTop = getResources().getColor(android.R.color.white);
        mColorBottom = getResources().getColor(android.R.color.white);
        mTextSizeTop = 18;
        mTextSizeBottom = 18;
    }
}