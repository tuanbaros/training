package com.simple.training;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by FRAMGIA\nguyen.thanh.tuan on 10/18/17.
 */

public class ArticleFragment extends Fragment {
    public static final String ARG_POSITION = "arg_position";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        int position = bundle == null ? 0 : bundle.getInt(ARG_POSITION);
        TextView text = view.findViewById(R.id.text_position);
        text.setText(String.valueOf(position));
    }

    public void updateArticleView(int position) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView text = view.findViewById(R.id.text_position);
        text.setText(String.valueOf(position));
    }
}
