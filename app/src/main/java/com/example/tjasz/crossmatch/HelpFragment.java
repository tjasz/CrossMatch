package com.example.tjasz.crossmatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class HelpFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_HEADER_ID = "arg_header_id";
    private static final String ARG_TEXT_ID1 = "arg_text_id1";
    private static final String ARG_TEXT_ID2 = "arg_text_id2";
    private static final String ARG_IMG_ID = "arg_img_id";

    private int mHeaderId;
    private int mTextId1;
    private int mTextId2;
    private int mImgId;


    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance(int h, int t1, int t2, int i) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HEADER_ID, h);
        args.putInt(ARG_TEXT_ID1, t1);
        args.putInt(ARG_TEXT_ID2, t2);
        args.putInt(ARG_IMG_ID, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeaderId = getArguments().getInt(ARG_HEADER_ID);
            mTextId1 = getArguments().getInt(ARG_TEXT_ID1);
            mTextId2 = getArguments().getInt(ARG_TEXT_ID2);
            mImgId = getArguments().getInt(ARG_IMG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        TextView header = (TextView) view.findViewById(R.id.help_header);
        TextView text1 = (TextView) view.findViewById(R.id.help_text1);
        TextView text2 = (TextView) view.findViewById(R.id.help_text2);

        header.setText(getResources().getString(mHeaderId));
        text1.setText(getResources().getString(mTextId1));
        text2.setText(getResources().getString(mTextId2));

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        ImageView img = (ImageView) getView().findViewById(R.id.help_img);
        img.setImageDrawable(getResources().getDrawable(mImgId));
    }
}
