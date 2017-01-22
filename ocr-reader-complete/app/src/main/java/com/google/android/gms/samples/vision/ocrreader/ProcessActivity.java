package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProcessActivity extends Activity {
    TextView tvProcessText;
    String text;
    DocsDatabaseHelper helper;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        tvProcessText = (TextView) findViewById(R.id.tvProcessText);
        etName = (EditText) findViewById(R.id.etName);
        text = getIntent().getStringExtra("text");
        tvProcessText.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void toFeed(View view) {
        //add to database
        Doc doc = new Doc();
        doc.name = etName.getText().toString();
        doc.text = text;
        helper = DocsDatabaseHelper.getInstance(this);
        helper.addOrUpdateDoc(doc);
        Intent i = new Intent(ProcessActivity.this, FeedActivity.class);

        i.putExtra("text", "food");
        startActivity(i); // brings up the second activity
    }
}
