package com.example.fengwei7.timbretuner.DataEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fengwei7.timbretuner.R;

import java.util.List;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class UserRecAdapter extends ArrayAdapter<UserRecEntry>{
    private final Context context;
    private final List<UserRecEntry> entries;

    public UserRecAdapter(Context context, List<UserRecEntry> entries) {
        super(context, R.layout.fragment_user_title, entries);

        this.context = context;
        this.entries = entries;
    }

    static class ViewHolder {

        public TextView title;
        public TextView score;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View listEntry = convertView;

        if (listEntry == null) {
            // Inflate the view and the adapter since there is no view that can be converter
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            listEntry = inflater.inflate(R.layout.fragment_user_title, parent, false);
            // Create a new holder to reduce lookup
            ViewHolder viewHolder = new ViewHolder();

            // Populate the viewholder with the required views
            viewHolder.title = (TextView) listEntry.findViewById(R.id.title_user);
            viewHolder.score = (TextView) listEntry.findViewById(R.id.score_user);

            listEntry.setTag(viewHolder);
        }

        // Fill the data of the holders from the tag.
        ViewHolder viewHolder = (ViewHolder) listEntry.getTag();
        String space="          ";

        viewHolder.title.setText(entries.get(position).getTitle());
        viewHolder.score.setText(String.valueOf(entries.get(position).getScore()));


        return listEntry;
    }
}

