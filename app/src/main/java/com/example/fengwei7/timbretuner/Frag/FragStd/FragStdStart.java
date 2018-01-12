package com.example.fengwei7.timbretuner.Frag.FragStd;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fengwei7.timbretuner.R;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragStdStart extends Fragment {

    public FragStdStart() {
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
        View rootView =inflater.inflate(R.layout.fragment_std_start, container, false);
        Button bt = (Button) rootView.findViewById(R.id.btn_title_add);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_title_view);
        bt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed(view);
                    }
                }
        );
        bt = (Button) rootView.findViewById(R.id.btn_back);
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
            case R.id.btn_title_add:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment newFrag = new FragStdAdd();
                ft.replace(R.id.layout_main, newFrag);
                ft.addToBackStack("Enter Standard module");
                ft.commit();
                break;
            case R.id.btn_title_view:
                fm = getActivity().getSupportFragmentManager();
                ft = fm.beginTransaction();
                newFrag = new FragStdTitle();
                ft.replace(R.id.layout_main, newFrag);
                ft.addToBackStack("Enter Standard module");
                ft.commit();
                break;
            case R.id.btn_back:
                this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }



}
