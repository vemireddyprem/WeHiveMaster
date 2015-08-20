/*******************************************************************************
 PROJECT:       Hive
 FILE:          SelectPictureDialog.java
 DESCRIPTION:   Show dialog for selected type of picture
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        11/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.utils.PictureTransformer;

public class ShowPictureDialog extends DialogFragment implements View.OnClickListener {

    private String mUrlPhoto;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_picture_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewComponents();
    }

    public void setUrlPhoto(String urlPhoto) {
        this.mUrlPhoto = urlPhoto;
    }

    private void initViewComponents() {
        ImageView imgPhoto = (ImageView) getView().findViewById(R.id.img_dialog);
        imgPhoto.setOnClickListener(this);
        if (mUrlPhoto.length() > 10) {
            Picasso.with(getActivity())
                    .load(mUrlPhoto).transform(new PictureTransformer())
                    .error(R.drawable.ic_or_drawable)
                    .into(imgPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
