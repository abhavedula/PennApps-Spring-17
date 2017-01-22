package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import android.widget.Button;
import android.widget.EditText;

import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import android.view.textservice.SuggestionsInfo;

import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DocActivity extends Activity implements SpellCheckerSessionListener{
    TextView tvDocName;
    TextView tvDocText;
    TextView tvCheck;
    String text;
    String name;
    private SpellCheckerSession mScs;
    static Ngram ngram;
    private String corrected;
    private String bestSugg;
    private boolean hasCorr;
    private List<String> list;
    DocsDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);



        name = getIntent().getStringExtra("name");
        text = getIntent().getStringExtra("text");



        tvDocName = (TextView) findViewById(R.id.tvDocName);
        tvDocText = (TextView) findViewById(R.id.tvDocText);
        //tvCheck = (TextView) findViewById(R.id.tvCheck);

        tvDocName.setText(name);
        tvDocText.setText(text);

        helper = DocsDatabaseHelper.getInstance(this);


        bestSugg = "";
        hasCorr = false;

        InputStream is = this.getResources().openRawResource(R.raw.text);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ngram = CreateNgramModel.readFromFile(reader);

//        Button btnSpellcheck=(Button)findViewById(R.id.btnSpellcheck);
//
//        btnSpellcheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),
//                        text,Toast.LENGTH_SHORT).show();
//                String[] in = text.split(" ");
//                //List<TextInfo[]> input = new LinkedList<>();
//                corrected = "";
//                list = new LinkedList<>();
//                for (int i = 0; i < in.length; i++) {
//                    TextInfo[] txt = new TextInfo[1];
//                    txt[0] = new TextInfo(in[i]);
//                    mScs.getSentenceSuggestions(txt, 5);
//                    System.out.println("HASCORR: " + hasCorr);
//                    if (hasCorr) {
//                        corrected += bestSugg + " ";
//                        list.add(bestSugg);
//                        System.out.println("CORRECTED: " + corrected);
//                    } else {
//                        corrected += in[i] + " ";
//                        list.add(in[i]);
//                    }
//                    //input.add(txt);
//                    System.out.println(corrected);
//                }
//
//
//
//            }
//        });
    }

    public void grammar(View view) {
        fixGrammar(text);


    }

    public void toFeed(View view) {
        Intent i = new Intent(DocActivity.this, FeedActivity.class);

        startActivity(i);



    }

    public void fixGrammar(final String text) {

//        if (ngram == null) {
//
//            InputStream is = this.getResources().openRawResource(R.raw.text);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            ngram = CreateNgramModel.readFromFile(reader);
//
//        }


        String text2 = text.replaceAll(" ", "+");
        String url = "https://api.textgears.com/check.php?text=" + text2 + "&key=BGC9tsjxC23D4nzg";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println(response.toString());

                String corrected = text;


                JSONArray array = null;
                try {
                    array = response.getJSONArray("errors");
                    for (int i = 0; i < array.length(); i++) {
                        String bad = array.getJSONObject(i).getString("bad");
                        //String[] goodArr = (String[]) array.getJSONObject(i).get("better");
                        JSONArray goodArr = (JSONArray) array.getJSONObject(i).get("better");

                        String good = ngram.getMostLikelySugg(truncate(text.split(" "), bad), goodArr);
                       // corrected.replace(bad, good);

                        //String good = (String) arr.get(0);
                        //String good = goodArr[0];
                        corrected = corrected.replace(bad, good);

                    }
                    //System.out.println("629382" + corrected);
                    Doc doc = new Doc();
                    doc.name = name;
                    doc.text = corrected;
                    helper.addOrUpdateDoc(doc);
                    tvDocText.setText(corrected);
                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();

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

    public List<String> truncate(String[] strArr, String word){
        List<String> list = new LinkedList<>();

        for(String str: strArr){
            if(!str.equals(word)){
                list.add(str);
            }
            else{
                break;
            }
        }

        return list;
    }

    @Override
    public void onGetSuggestions(final SuggestionsInfo[] arg0) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        final StringBuffer sb = new StringBuffer("");
        final StringBuffer corr = new StringBuffer("");
        hasCorr = false;
        for(SentenceSuggestionsInfo result:results){
            int n = result.getSuggestionsCount();
            for(int i=0; i < n; i++){
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();

                if((result.getSuggestionsInfoAt(i).getSuggestionsAttributes() &
                        SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO) != SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO ) {
//                    list.add(result.getSuggestionsInfoAt(i).getSuggestionAt(0)); // TO CHECK
                    hasCorr = false;
                    continue;
                }
                hasCorr = true;
                String[] sugg = new String[m];

                for(int k=0; k < m; k++) {
                    sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                            .append("\n");
                    sugg[k] = result.getSuggestionsInfoAt(i).getSuggestionAt(k);
                }
                sb.append("\n");
                //list.add(ngram.getMostLikelySugg(list, sugg));
//                bestSugg = ngram.getMostLikelySugg(list, sugg); // change to mostlikelysugg later
//                System.out.println("BESTSUGG: " + bestSugg);
            }
        }
//        runOnUiThread(new Runnable() {
//            public void run() {
//                tvCheck.append(sb.toString());
//            }
//        });
    }

    public void delete(View view) {
        helper.deleteDoc(name);
        Intent i = new Intent(DocActivity.this, FeedActivity.class);

        startActivity(i); // brings up the second activity
    }

    @Override
    protected void onResume() {
        super.onResume();
//        final TextServicesManager tsm = (TextServicesManager)
//                getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
//
//        mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, false);
//        System.out.println(mScs);
    }

    public void onPause() {
        super.onPause();
//        if (mScs != null) {
//            mScs.close();
//        }
    }

    public void toQuizSets(View view) {
        Intent i = new Intent(DocActivity.this, QuizSetActivity.class);
        i.putExtra("name", name);
        i.putExtra("text", text);
        startActivity(i); // brings up the second activity
    }

}
