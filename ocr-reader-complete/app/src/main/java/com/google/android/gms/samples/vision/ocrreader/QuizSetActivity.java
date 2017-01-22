package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class QuizSetActivity extends Activity {

    HashMap<String, String> vocab; //single map of terms to definitions (one quiz)
    Set<String> medTerms = new HashSet<String>();
    Set<HashMap<String, String>> sets = new HashSet<HashMap<String, String>>(); //complete set of all quizzes
    static ArrayList<QuizSet> quizSets = new ArrayList<QuizSet>(); //list of all quizzes or sets of terms quizset
    static ArrayList<QuizSet> quizSets2 = new ArrayList<QuizSet>(); //list of all quizzes or sets of terms quizset
    QuizAdapter adapter;
    String name;
    Set<String> names = new HashSet<String>();
    int numQuizSets = 0;

    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_set);
        adapter = new QuizAdapter(this, quizSets);






    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        name = getIntent().getStringExtra("name"); //we get the text
        tvTitle.setText(name);
        text = getIntent().getStringExtra("text"); //we get the text


        adapter.clear();
        final ListView listView = (ListView) findViewById(R.id.lvQuizSets);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                QuizSet o = (QuizSet) listView.getItemAtPosition(position);


                Intent i = new Intent(QuizSetActivity.this, QuizActivity.class);
                i.putExtra("map", o.termsToDefs);
                i.putExtra("name", o.name);

                startActivity(i); // brings up the second activity

            }
        });


        //text = "osteoporosis anesthesia hello";


        findMedTerms(text);
    }

    public void request(String word) {

        String url = "https://api.quizlet.com/2.0/search/sets?client_id=VTRDjeJ82R&q=" + word;
        AsyncHttpClient client = new AsyncHttpClient();
        final int[] id = {0};
        final String[] name = {""};

        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                JSONArray array = null;
                try {
                    array = response.getJSONArray("sets");
                    JSONObject set = array.getJSONObject(0);
                    id[0] = Integer.parseInt(set.getString("id"));
                    name[0] = set.getString("title");

                    if (!names.contains(name[0])) {
                        names.add(name[0]);
                        request2(id[0], name[0]);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });




    }

    public void request2(int id, final String name1) {
        vocab = new HashMap<String, String>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url2 = "https://api.quizlet.com/2.0/sets/" + id + "/terms?client_id=VTRDjeJ82R";
        client.get(url2, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                vocab = new HashMap<String, String>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        String term = response.getJSONObject(i).getString("term");

                        String def = response.getJSONObject(i).getString("definition");
                        vocab.put(term, def);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                // vocab has stuff in it!!
                sets.add(vocab);

                QuizSet qs = new QuizSet();
                qs.termsToDefs = vocab;

                qs.name = name1;
                quizSets.add(qs);
                quizSets2.add(qs);
                System.out.println("Name333: " + qs.name + " Term/Def: " + qs.termsToDefs.toString());

                adapter.notifyDataSetChanged();
                //populateList();



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    public void readTermsFromFile() {
        int count = 0;
        try {


            InputStream is = this.getResources().openRawResource(R.raw.wordlist);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // read line from file
            String line = reader.readLine();


            while (line != null) {
                if (line.length() > 5) {
                    medTerms.add(line.toLowerCase());
                }

                line = reader.readLine();
            }
            reader.close();
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void findMedTerms(String text) {
        readTermsFromFile();
        String replaced = text.replaceAll("\n", " ").toLowerCase();
        String[] textArray = replaced.split("\\s+");
        boolean done = true;
        for (int i = 0; i < textArray.length; i++) {
            if (medTerms.contains(textArray[i])) {
                String word = textArray[i];
                request(word);
            }
        }


    }



}
