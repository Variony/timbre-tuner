package com.example.fengwei7.timbretuner.Frag.FragUser;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;
import android.content.Context;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.R;

import com.example.fengwei7.timbretuner.DataBase.DataHandler;
import com.example.fengwei7.timbretuner.DataEntry.DataEntry;


import java.util.List;

public class VideoFrag extends Fragment {
    private RecHandler recHandler;
    private String username = null;
    private VideoView videoView;
    private View rootView;
    private Context ctx;

    public VideoFrag() {
        // Required empty public constructor
        Bundle args = new Bundle();
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recHandler= RecHandler.getHandler();
        // Inflate the layout for this fragment
         rootView = inflater.inflate(R.layout.fragment_video, container, false);

        //记得在手机SD卡根目录添加视频
        String videoUrl1 = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/tut.mp4";

        Uri uri = Uri.parse(videoUrl1);

        videoView = (VideoView) rootView.findViewById(R.id.videoView);

        videoView.setMediaController(new MediaController(getActivity()));


        //设置视频路径
        videoView.setVideoURI(uri);

        //开始播放视频
        videoView.start();


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditText usernameView=(EditText) getActivity().findViewById(R.id.user_username);
        //button
        Button button1 = (Button) rootView.findViewById(R.id.btnContinue);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle args = getArguments();
                        username= args.getString("username");

                        Bundle args1 = new Bundle();
                        args1.putString("username", username);
                        DataHandler handler = DataHandler.getHandler();
                        List<DataEntry> all_titles = handler.getAllValues();
                        for (DataEntry one_title : all_titles) {
                            recHandler.addValue(new UserRecEntry(username, one_title.getTitle(), 0));
                        }

                        Fragment newFrag = new FragUserTitle();
                        newFrag.setArguments(args);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.layout_main, newFrag);
                        ft.addToBackStack("Enter Standard module");
                        ft.commit();
                    }
                }
        );
    }

    @Override
    public void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        videoView.resume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }
}


