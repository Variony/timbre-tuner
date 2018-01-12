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
public class StdTitleAdapter extends ArrayAdapter<DataEntry>{
    private final Context context;
    private final List<DataEntry> entries;

    public StdTitleAdapter(Context context, List<DataEntry> entries) {
        super(context, R.layout.fragment_std_title, entries);

        this.context = context;
        this.entries = entries;
    }

    static class ViewHolder {

        public TextView title;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View listEntry = convertView;

        if (listEntry == null) {
            // Inflate the view and the adapter since there is no view that can be converter
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            listEntry = inflater.inflate(R.layout.fragment_std_title, parent, false);
            // Create a new holder to reduce lookup
            ViewHolder viewHolder = new ViewHolder();

            // Populate the viewholder with the required views
            viewHolder.title = (TextView) listEntry.findViewById(R.id.title);


            listEntry.setTag(viewHolder);
        }

        // Fill the data of the holders from the tag.
        ViewHolder viewHolder = (ViewHolder) listEntry.getTag();
        String space="          ";

        viewHolder.title.setText(entries.get(position).getTitle());


        return listEntry;
    }
}

