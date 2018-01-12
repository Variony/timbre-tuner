package com.example.fengwei7.timbretuner.Frag.FragUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fengwei7.timbretuner.DataBase.RecHandler;
import com.example.fengwei7.timbretuner.DataEntry.UserRecAdapter;
import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;
import com.example.fengwei7.timbretuner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class FragUserTitle extends Fragment  {

    private UserRecAdapter myListAdapter=null;
    private ListView fileList;
    private String cur_username;

    public FragUserTitle() {
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
        View rootView = inflater.inflate(R.layout.listview, container, false);


        Bundle args = getArguments();
        cur_username= args.getString("username");


        fileList = (ListView) rootView.findViewById(R.id.file_list);


        RecHandler recHandler = RecHandler.getHandler();
        List<UserRecEntry> rec_list = recHandler.getAllValues();

        List<UserRecEntry> matched_rec_list = new ArrayList<UserRecEntry>();
        for(UserRecEntry one_rec:rec_list){
            if(one_rec.getUsername().equals(cur_username))
                matched_rec_list.add(one_rec);
        }


        myListAdapter = new UserRecAdapter(getActivity(), matched_rec_list);
        fileList.setAdapter(myListAdapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Bundle args = new Bundle();
                    args.putString("cur_title",myListAdapter.getItem(position).getTitle());
                    args.putString("cur_username",myListAdapter.getItem(position).getUsername());
                    args.putInt("cur_score",myListAdapter.getItem(position).getScore());

                    Fragment newFrag = new FragUserMain();
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
                adBuilder.setMessage("Delete This User?").setCancelable(true);
                adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RecHandler recHandler = RecHandler.getHandler();
                        recHandler.deleteUser(cur_username);
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Delete User Success",
                                Toast.LENGTH_SHORT).show();

                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        return;
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
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
