package com.example.fengwei7.timbretuner.Frag.FragStd;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fengwei7.timbretuner.DataBase.DataHandler;
import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.DataEntry;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.R;
import com.example.fengwei7.timbretuner.RecordingActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragStdAdd extends Fragment {
    private String title=null;
    private String comment=null;
    private String url=null;
    private View rootView;
    private String BACKUP_DB_PATH_DIR=null;
    DataInputStream dis=null;

    public FragStdAdd() {
        // Required empty public constructor
        Bundle args = new Bundle();
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_std_add, container, false);
        Button bt = (Button) rootView.findViewById(R.id.btn_play);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_add_new);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_add_confirm);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        return rootView;
    }

    public void onButtonPressed(View view){
        switch (view.getId()) {
            case R.id.btn_play:
                //play recorded voice
                title=  ((EditText) rootView.findViewById(R.id.std_enter_title)).getText().toString();
                Play play=new Play();
                play.execute();
                break;
            case R.id.btn_add_new:
                //record new voice for this title
                title=  ((EditText) rootView.findViewById(R.id.std_enter_title)).getText().toString();
                comment =((EditText) rootView.findViewById(R.id.std_enter_comment)).getText().toString();
                Intent i = new Intent(getActivity(), RecordingActivity.class);
                i.putExtra("title",title);
                i.putExtra("comment",comment);
                i.putExtra("username","none");
                startActivity(i);

                break;
            case R.id.btn_add_confirm:

                title=  ((EditText) rootView.findViewById(R.id.std_enter_title)).getText().toString();
                comment =((EditText) rootView.findViewById(R.id.std_enter_comment)).getText().toString();
                DataHandler handler = DataHandler.getHandler();
                handler.addValue(new DataEntry(title,comment,"not_init"));
                RecHandler recHandler = RecHandler.getHandler();
                HashSet<String> users = recHandler.getUsers();
                for(String one_user:users){
                    recHandler.addValue(new UserRecEntry(one_user,title,0));
                }

                Toast.makeText(getActivity(), "Add Target Suceess",
                        Toast.LENGTH_SHORT).show();
                this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;

        }
    }

    public class Play extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            BACKUP_DB_PATH_DIR= "/data/data/audio";

            String path = getActivity().getExternalFilesDir(BACKUP_DB_PATH_DIR).toString()+"/"+title;
            try {
                dis= new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(dis==null) return null;
            int bufferSize = AudioTrack.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);

            AudioTrack  audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

            audioTrack.play();

            short[] buffer = new short[256];

            try {
                while (dis.available()>0){
                    int i=0;
                    while (dis.available()>0 && i<buffer.length){
                        buffer[i]=dis.readShort();
                        i++;
                    }
                    audioTrack.write(buffer,0,buffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;

        }


    }


}
