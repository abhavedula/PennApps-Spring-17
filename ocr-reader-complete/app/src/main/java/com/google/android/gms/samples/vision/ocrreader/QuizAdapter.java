package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ramyarao on 1/21/2017.
 */
public class QuizAdapter extends ArrayAdapter<QuizSet> {

    public QuizAdapter(Context context, ArrayList<QuizSet> quizSets) {
        super(context, 0, quizSets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuizSet quizSet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_quiz_set, parent, false);
        }
        // Lookup view for data population

        TextView tvQuizSetName = (TextView) convertView.findViewById(R.id.tvQuizSetName);
        // Populate the data into the template view using the data object
        String str = "Title";
        if (quizSet.name != null) {
            str = quizSet.name;
        }
//        for (String s : quizSet.termsToDefs.keySet()) {
//            if (s != null) {
//                str = s;
//            }
//        }

        tvQuizSetName.setText(str);
        // Return the completed view to render on screen
        return convertView;
    }
}
