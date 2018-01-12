package com.example.fengwei7.timbretuner.Frag.FragStd;

import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.example.fengwei7.timbretuner.DataBase.DataHandler;
import com.example.fengwei7.timbretuner.DataEntry.DataEntry;
import com.example.fengwei7.timbretuner.DataEntry.StdTitleAdapter;
import com.example.fengwei7.timbretuner.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragStdTitle extends Fragment  {

    private StdTitleAdapter myListAdapter=null;
    private ListView fileList;

    public FragStdTitle() {
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
        View rootView = inflater.inflate(R.layout.listview, container, false);


        DataHandler handler = DataHandler.getHandler();

        fileList = (ListView) rootView.findViewById(R.id.file_list);
        myListAdapter = new StdTitleAdapter(getActivity(), handler.getAllValues());
        fileList.setAdapter(myListAdapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Bundle args = new Bundle();
                    args.putString("cur_title",myListAdapter.getItem(position).getTitle());
                    args.putString("cur_comment",myListAdapter.getItem(position).getComment());
                    Fragment newFrag = new FragStdView();
                    newFrag.setArguments(args);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.layout_main, newFrag);
                    ft.addToBackStack("Enter Standard module");
                    ft.commit();
                } catch (Exception e) {
                    throw new ClassCastException(getActivity().toString()
                            + " IO Exception " + e.toString());
                }
            }
        });

        return rootView;
    }


}
