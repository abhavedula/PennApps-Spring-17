package com.google.android.gms.samples.vision.ocrreader;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * Created by ramyarao on 1/21/2017.
 */
public class CustomDocAdapter extends ArrayAdapter<Doc> {
    public CustomDocAdapter(Context context, ArrayList<Doc> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Doc user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_doc, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvText);
        // Populate the data into the template view using the data object
        tvName.setText(user.name);
        tvHome.setText(user.text);
        // Return the completed view to render on screen
        return convertView;
    }


}