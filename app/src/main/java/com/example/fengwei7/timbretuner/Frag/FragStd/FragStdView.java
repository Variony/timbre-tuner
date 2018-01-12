package com.example.fengwei7.timbretuner.Frag.FragStd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fengwei7.timbretuner.DataBase.DataHandler;
import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.FFT.AudioInfo;
import com.example.fengwei7.timbretuner.Frag.FragRank.FragRankMain;
import com.example.fengwei7.timbretuner.R;
import com.example.fengwei7.timbretuner.RecordingActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragStdView extends Fragment{
    private String cur_title;
    private String cur_comment;
    private String BACKUP_DB_PATH_DIR=null;
    DataInputStream dis=null;

    public FragStdView() {
        // Required empty public constructor
        Bundle args = new Bundle();
        this.setArguments(args);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_std_view, container, false);
        TextView title= (TextView)rootView.findViewById(R.id.std_view_title);
        TextView comment = (TextView) rootView.findViewById(R.id.std_view_comment);

        Bundle args = getArguments();

        this.cur_title= args.getString("cur_title");
        this.cur_comment= args.getString("cur_comment");
        title.setText(cur_title);
        comment.setText(cur_comment);

        Button bt = (Button) rootView.findViewById(R.id.btn_play_target);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_record_again);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_viewrank);
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
                case R.id.btn_play_target:
                    //play recorded voice
                    Play play=new Play();
                    play.execute();
                    break;
                case R.id.btn_record_again:
                    //record new voice for this title
                    Intent i = new Intent(getActivity(), RecordingActivity.class);
                    i.putExtra("title",cur_title);
                    i.putExtra("comment",cur_comment);
                    i.putExtra("username","none");
                    startActivity(i);
                    break;
                case R.id.btn_viewrank:
                    Bundle args = new Bundle();
                    args.putString("cur_title",cur_title);
                    Fragment newFrag = new FragRankMain();
                    newFrag.setArguments(args);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.layout_main, newFrag);
                    ft.addToBackStack("Enter Standard module");
                    ft.commit();
                    break;
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete, menu);
        return ;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this.getActivity());
                adBuilder.setMessage("Delete This Title?").setCancelable(true);
                adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataHandler handler = DataHandler.getHandler();
                        handler.deleteTitle(cur_title);
                        RecHandler recHandler = RecHandler.getHandler();
                        recHandler.deleteTitle(cur_title);
                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();
                        RealmResults<AudioInfo> audioInfos = realm.where(AudioInfo.class).equalTo("mPath", cur_title).findAll();
                        if (!audioInfos.isEmpty()) {
                            audioInfos.deleteAllFromRealm();
                        }
                        realm.commitTransaction();

                        String path= "/data/data/audio/"+cur_title;
                        File file = new File(getActivity().getExternalFilesDir(path).toString());
                            file.delete();


                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Delete Title Success",
                                Toast.LENGTH_SHORT).show();

                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        return;
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });
                adBuilder.create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public class Play extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            BACKUP_DB_PATH_DIR= "/data/data/audio";

            String path = getActivity().getExternalFilesDir(BACKUP_DB_PATH_DIR).toString()+"/"+cur_title;
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
