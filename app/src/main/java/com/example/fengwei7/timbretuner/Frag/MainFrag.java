package com.example.fengwei7.timbretuner.Frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fengwei7.timbretuner.Frag.FragRank.FragRankTitle;
import com.example.fengwei7.timbretuner.Frag.FragStd.FragStdStart;
import com.example.fengwei7.timbretuner.Frag.FragUser.FragUserLogin;
import com.example.fengwei7.timbretuner.R;


public class MainFrag extends Fragment {

    public MainFrag() {
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
        View rootView =inflater.inflate(R.layout.fragment_main, container, false);
        Button bt = (Button) rootView.findViewById(R.id.btn_standard);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_user);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_rank);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );

        bt = (Button) rootView.findViewById(R.id.btn_exit);
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
        switch (view.getId()){
            case R.id.btn_standard:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment newFrag = new FragStdStart();
                ft.replace(R.id.layout_main, newFrag);
                ft.addToBackStack("Enter Standard module");
                ft.commit();
                break;
            case R.id.btn_user:
                fm = getActivity().getSupportFragmentManager();
                ft = fm.beginTransaction();
                newFrag = new FragUserLogin();
                ft.replace(R.id.layout_main, newFrag);
                ft.addToBackStack("Enter Standard module");
                ft.commit();
                break;
            case R.id.btn_rank:
                newFrag = new FragRankTitle();
                fm = getActivity().getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.layout_main, newFrag);
                ft.addToBackStack("Enter Standard module");
                ft.commit();
                break;
            case R.id.btn_exit:
                // Exit Procedure
                this.getActivity().finish();
                break;
        }


    }

}
