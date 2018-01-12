package com.example.fengwei7.timbretuner.Frag.FragUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.Frag.FragRank.FragRankTitle;
import com.example.fengwei7.timbretuner.R;

import java.util.List;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragUserLogin extends Fragment {
    private RecHandler recHandler;
    private View rootView;
    private String username=null;
    public FragUserLogin() {
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
        recHandler= RecHandler.getHandler();
        rootView =inflater.inflate(R.layout.fragment_user_login, container, false);
        Button bt = (Button) rootView.findViewById(R.id.btn_user_login);
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

    public boolean checkuser(List<UserRecEntry>rec_list,String username){
        for(UserRecEntry one_list:rec_list){
            if(one_list.getUsername().equals(username))return true;
        }
        return false;
    }
    public void onButtonPressed(View view){
        switch (view.getId()){
            case R.id.btn_user_login:
                EditText usernameView= (EditText) rootView.findViewById(R.id.user_username);
                username= usernameView.getText().toString();
                if(username.isEmpty()){
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(this.getActivity());
                    adBuilder.setMessage("Please Enter Username").setCancelable(true);
                    adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = adBuilder.create();
                    alertDialog.show();
                    // Don't add to the database if name is empty
                    return;
                }
                List<UserRecEntry> rec_list= recHandler.getAllValues();

                if(this.checkuser(rec_list,username)) {

                    Bundle args = new Bundle();
                    args.putString("username", username);
                    Fragment newFrag = new FragUserTitle();
                    newFrag.setArguments(args);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.layout_main, newFrag);
                    ft.addToBackStack("Enter Standard module");
                    ft.commit();
                }
                else{
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(this.getActivity());
                    adBuilder.setMessage("No record for this user\nDo you want to create this new user?").setCancelable(true);
                    adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment newFrag = new VideoFrag();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Bundle args = new Bundle();
                            args.putString("username", username);
                            newFrag.setArguments(args);
                            ft.replace(R.id.layout_main, newFrag);
                            ft.addToBackStack("Enter Standard module");
                            ft.commit();
//                            DataHandler handler = DataHandler.getHandler();
//                            List<DataEntry> all_titles = handler.getAllValues();
//                            for(DataEntry one_title:all_titles) {
//                                recHandler.addValue(new UserRecEntry(username,one_title.getTitle(),0));
//                            }
//                            Bundle args = new Bundle();
//                            args.putString("username", username);
//                            Fragment newFrag = new FragUserTitle();
//                            newFrag.setArguments(args);
//                            FragmentManager fm = getActivity().getSupportFragmentManager();
//                            FragmentTransaction ft = fm.beginTransaction();
//                            ft.replace(R.id.layout_main, newFrag);
//                            ft.addToBackStack("Enter Standard module");
//                            ft.commit();
//                            return;
                        }
                    });
                    adBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });
                    adBuilder.create().show();
                }
                break;

        }
    }



}
