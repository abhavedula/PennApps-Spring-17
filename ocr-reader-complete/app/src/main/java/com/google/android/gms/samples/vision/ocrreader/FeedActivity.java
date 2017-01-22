package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedActivity extends AppCompatActivity {

    DocsDatabaseHelper helper;
    ArrayList<Doc> docs = new ArrayList<Doc>();
    CustomDocAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        helper = DocsDatabaseHelper.getInstance(this);
        adapter = new CustomDocAdapter(this, docs);

        final ListView listView = (ListView) findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
        docs.addAll(helper.getAllDocs());
        adapter.notifyDataSetChanged();


        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Doc o = (Doc) listView.getItemAtPosition(position);

                Intent i = new Intent(FeedActivity.this, DocActivity.class);
                i.putExtra("name", o.name);
                i.putExtra("text", o.text);
                startActivity(i); // brings up the second activity

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                docs.clear();
                adapter.notifyDataSetChanged();
                Set<Doc> included = new HashSet<Doc>();
                for (Doc doc : helper.getAllDocs()) {
                    if (doc.getText().contains(query) || doc.getName().contains(query)) {
                        included.add(doc);
                    }
                }

                docs.addAll(included);
                //adapter.notifyDataSetChanged();
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                docs.clear();
                docs.addAll(helper.getAllDocs());
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    public void toImage(View view) {
        Intent i = new Intent(FeedActivity.this, OcrCaptureActivity.class);

        startActivity(i); // brings up the second activity
    }

}
