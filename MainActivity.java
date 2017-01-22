package com.example.victoria.myapplication;

import android.app.Activity;
import android.content.Context;
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

public class MainActivity extends Activity implements SpellCheckerSessionListener  {
    Button b1;
    TextView tv1;
    EditText ed1;
    private SpellCheckerSession mScs;
    private Ngram ngram;
    private String corrected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream is = this.getResources().openRawResource(R.raw.text);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ngram = CreateNgramModel.readFromFile(reader); // TODO
        corrected = "";

        b1=(Button)findViewById(R.id.button);
        tv1=(TextView)findViewById(R.id.textView3);

        ed1=(EditText)findViewById(R.id.editText);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        ed1.getText().toString(),Toast.LENGTH_SHORT).show();
                String[] in = ed1.getText().toString().split(" ");
                TextInfo[] txt = new TextInfo[in.length];

                for (int i = 0; i < txt.length; i++) {
                    txt[i] = new TextInfo(in[i]);
                }

                mScs.getSentenceSuggestions(txt, 3);

            }
        });
    }

    public String getCorr() {
        return corrected;
    }

    public void onResume() {
        super.onResume();
        final TextServicesManager tsm = (TextServicesManager)
                getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        //mScs = tsm.newSpellCheckerSession(null, null, this, true);
        mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, false);
    }

    public void onPause() {
        super.onPause();
        if (mScs != null) {
            mScs.close();
        }
    }

    public void onGetSuggestions(final SuggestionsInfo[] arg0) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arg0.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = arg0[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                sb.append("," + arg0[i].getSuggestionAt(j));
            }

            sb.append(" (" + len + ")");
        }

        runOnUiThread(new Runnable() {
            public void run() {
                tv1.append(sb.toString());
            }
        });
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        final StringBuffer sb = new StringBuffer("");
        final StringBuffer corr = new StringBuffer("");
        final List<String> list = new LinkedList<>();
        for(SentenceSuggestionsInfo result:results){
            int n = result.getSuggestionsCount();
            for(int i=0; i < n; i++){
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();

                if((result.getSuggestionsInfoAt(i).getSuggestionsAttributes() &
                        SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO) != SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO ) {
                    list.add(result.getSuggestionsInfoAt(i).getSuggestionAt(0)); // TO CHECK
                    continue;
                }

                String[] sugg = new String[m];

                for(int k=0; k < m; k++) {
                    sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                            .append("\n");
                    sugg[k] = result.getSuggestionsInfoAt(i).getSuggestionAt(k);
                }
                sb.append("\n");
                list.add(ngram.getMostLikelySugg(list, sugg));
            }
        }
        for (String str : list) {
            corr.append(str).append(" ");
        }
        corrected = corr.toString();
        runOnUiThread(new Runnable() {
            public void run() {
                tv1.append(sb.toString());
            }
        });
    }
}