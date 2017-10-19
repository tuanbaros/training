package com.simple.training;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.io.IOException;

/**
 * Created by FRAMGIA\nguyen.thanh.tuan on 10/18/17.
 */

public class HeadlinesFragment extends ListFragment {

    OnHeadLineSelectedListener mCallback;

    public interface OnHeadLineSelectedListener {
        void onArticleSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadLineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headlines, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] data = new String[] {
            "Name 1", "Name 2", "Name 3"
        };
//        List<String> names = new ArrayList<>(Arrays.asList(data));
        setListAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallback.onArticleSelected(position);
    }
}
