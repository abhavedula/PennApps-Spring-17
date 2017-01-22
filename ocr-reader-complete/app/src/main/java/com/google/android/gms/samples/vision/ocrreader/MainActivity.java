package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;



import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    Map<String, String> vocab;

    Set<String> medTerms = new HashSet<String>();

    Set<Map<String, String>> sets = new HashSet<Map<String, String>>();

    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = "osteoporosis anesthesia hello";

        findMedTerms(text);



    }

    public void request(String word) {

        String url = "https://api.quizlet.com/2.0/search/sets?client_id=VTRDjeJ82R&q=" + word;
        AsyncHttpClient client = new AsyncHttpClient();
        final int[] id = {0};

        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println(response.toString());

                JSONArray array = null;
                try {
                    array = response.getJSONArray("sets");
                    JSONObject set = array.getJSONObject(0);
                    id[0] = Integer.parseInt(set.getString("id"));
                    System.out.println("id: " + id[0]);
                    request2(id[0]);

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

    public void request2(int id) {
        vocab = new HashMap<String, String>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url2 = "https://api.quizlet.com/2.0/sets/" + id + "/terms?client_id=VTRDjeJ82R";
        client.get(url2, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                //System.out.println("response" + response.toString());

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

                System.out.println("size of set: " + sets.size());
                System.out.println("quizlet sets: " + sets.toString());




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
                medTerms.add(line);

                line = reader.readLine();
            }
            reader.close();
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void findMedTerms(String text) {
        readTermsFromFile();

        String[] textArray = text.split("\\s+");
        boolean done = true;
        for (int i = 0; i < textArray.length; i++) {
            if (medTerms.contains(textArray[i])) {
                String word = textArray[i];
                request(word);
            }
        }
    }
}


