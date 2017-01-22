package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends Activity {
    HashMap<String, String> hashMap;
    int index = 0;
    boolean definition = false;
    TextView tvQuestion;
    ArrayList<Quiz> quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);



    }

    @Override
    protected void onResume() {
        super.onResume();

//        for (QuizSet qs : QuizSetActivity.quizSets2) {
//            System.out.println("Name333: " + qs.name + " Term/Def: " + qs.termsToDefs.toString());
//        }



        Intent intent = getIntent();
        String nameGiven = intent.getStringExtra("name");
        System.out.println("7512627: " + nameGiven);
        //hashMap = (HashMap<String, String>)intent.getSerializableExtra("map");
        for (QuizSet qs : QuizSetActivity.quizSets) {
            if (qs.name.equals(nameGiven)) {
                hashMap = qs.termsToDefs;
                System.out.println("terms3456: " + qs.termsToDefs.toString());
            }
        }

        quizzes = new ArrayList<Quiz>();
        for (String s : hashMap.keySet()) {
            Quiz q = new Quiz();
            q.term = s;
            q.definition = hashMap.get(s);
            quizzes.add(q);
        }
        if (tvQuestion == null) {
            tvQuestion = new TextView(this);
            tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        }
        tvQuestion.setText("");
        tvQuestion.setText(quizzes.get(0).term);

    }

    public void flip(View view) {
        if (definition) {
            definition = false;

            tvQuestion.setText(quizzes.get(index).term);
        } else {
            definition = true;
            tvQuestion.setText(quizzes.get(index).definition);
        }

    }

    public void next(View view) {
        if (index < hashMap.size()) {
            index++;
            definition = false;
            tvQuestion.setText(quizzes.get(index).term);
        }
    }
}
