package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.CommentAdapter;
import uk.co.wehive.hive.bl.CommentsBL;
import uk.co.wehive.hive.entities.Comment;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontMultiAutoCompleteTextView;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class CreateCommentFragment extends Fragment implements View.OnClickListener, IHiveResponse {

    private MultiAutoCompleteTextView mEdtComment;
    private CommentsBL mCommentsBL;
    private User mUser;
    private String mPostId;
    private ProgressHive mProgressHive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_comment_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPostId = getArguments().getString(AppConstants.POST_ID);
        ArrayList<Comment> list = getArguments().getParcelableArrayList(AppConstants.COMMENTS_LIST);
        ListView listview = (ListView) getView().findViewById(R.id.lvt_comments_list);
        if (list != null && list.size() > 0) {
            listview.setAdapter(new CommentAdapter(getActivity(), list, getActivity().getSupportFragmentManager()));
        }

        mCommentsBL = new CommentsBL();
        mCommentsBL.setListener(this);
        mProgressHive = new ProgressHive();
        mUser = ManagePreferences.getUserPreferences();

        initViewComponents();
    }

    //Initialize view components
    private void initViewComponents() {
        mEdtComment = (CustomFontMultiAutoCompleteTextView) getView().findViewById(R.id.edt_create_comment);

        // Each row in the list stores country name, currency and flag
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < mUser.getConnections().size(); i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("txt", "@" + mUser.getConnections().get(i).getName());
            hm.put("picture", mUser.getConnections().get(i).getPhoto());
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"picture", "txt"};

        // Ids of views in listview_layout
        int[] to = {R.id.img_auto_complete, R.id.txt_auto_complete};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), aList, R.layout.autocomplete_item, from, to);

        // Setting the adapter to the listView
        mEdtComment.setAdapter(adapter);

        mEdtComment.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        getView().findViewById(R.id.btn_create_comment).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_create_comment:
                sendComment();
                break;
        }
    }

    //Webservice call
    private void sendComment() {

        String comment = mEdtComment.getText().toString();
        if (comment.length() > 0) {
            mProgressHive.show(getActivity().getSupportFragmentManager(), "");
            mCommentsBL.createComment(mUser.getToken(),String.valueOf(mUser.getId_user()), AppConstants.POST, comment, mPostId, "");
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_need_write_a_comment), "");
        }
    }

    @Override
    public void onError(RetrofitError error) {
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStackImmediate();
    }
}
