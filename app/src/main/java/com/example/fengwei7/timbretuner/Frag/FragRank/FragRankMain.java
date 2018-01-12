package com.example.fengwei7.timbretuner.Frag.FragRank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.RankAdapter;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragRankMain extends Fragment  {

    private RankAdapter myListAdapter=null;
    private ListView fileList;
    private String cur_title;

    public FragRankMain() {
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
        View rootView = inflater.inflate(R.layout.listview_rank, container, false);


        Bundle args = getArguments();
        this.cur_title= args.get("cur_title").toString();
        fileList = (ListView) rootView.findViewById(R.id.file_list_rank);
        TextView label = (TextView) rootView.findViewById(R.id.rank_label);
        label.setText("Rank  for  "+cur_title+" ");


        RecHandler recHandler = RecHandler.getHandler();
        List<UserRecEntry> rec_list = recHandler.getAllValues();

        List<UserRecEntry> matched_rec_list = new ArrayList<UserRecEntry>();
        for(UserRecEntry one_rec:rec_list){
            if(one_rec.getTitle().equals(cur_title))
                matched_rec_list.add(one_rec);
        }
        Collections.sort(matched_rec_list, new Comparator<UserRecEntry>() {
            @Override
            public int compare(UserRecEntry o1, UserRecEntry o2) {
                return o2.getScore()-o1.getScore();
            }
        });

        myListAdapter = new RankAdapter(getActivity(), matched_rec_list);
        fileList.setAdapter(myListAdapter);


        return rootView;
    }





}
