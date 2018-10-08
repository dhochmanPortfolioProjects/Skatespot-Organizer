package com.dhochmanrquick.skatespotorganizer;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SearchableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_searchable);

        // When a user executes a search from the search dialog or widget, the system starts your
        // searchable activity and sends it a ACTION_SEARCH intent. This intent carries the search
        // query in the QUERY string extra. You must check for this intent when the activity starts
        // and extract the string. For example, here's how you can get the search query when your
        // searchable activity starts:

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    public void doMySearch(String query) {
        Toast.makeText(this, "Querying for " + query, Toast.LENGTH_LONG).show();
    }
}
