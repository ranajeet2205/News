package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    String apiKey = BuildConfig.ApiKey;


        //guardian api url
    private final String GUARDIAN_NEWS_API_URL =
            "https://content.guardianapis.com/search?&show-tags=contributor&api-key="+apiKey;

    public NewsAdapter newsAdapter ;

    TextView mEmptyTextView;

    private RecyclerView recyclerView;

    private List<News> newsList;

    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(newsAdapter);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        //Declaring shared preferences to update the url for data

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minNumber = sharedPreferences.getString(getString(R.string.settings_min_number_key),
                                            getString(R.string.settings_min_number_default));

        Uri baseUri = Uri.parse(GUARDIAN_NEWS_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Appending the data to uri
        //here page size is declared which updates the number of news

        uriBuilder.appendQueryParameter("page-size",minNumber);

        //Create new loader for this url

        return new NewsLoader(this,uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList1) {

        //Show the loading progressbar

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        if (newsList1==null){
            // Set empty state text to display "No News found."
            mEmptyTextView.setText(R.string.no_news);
        }
        // Clear the adapter of previous News data
            recyclerView.setAdapter(null);
        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsList1 != null && !newsList1.isEmpty()) {
            //Update the data in the list
            newsList = newsList1;
            newsAdapter = new NewsAdapter(this,newsList);
            recyclerView.setAdapter(newsAdapter);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        recyclerView.setAdapter(null);
    }

    //Async task loader class ....(Inner class)

    public static class NewsLoader extends AsyncTaskLoader<List<News>> {

       private String mURL;

        public NewsLoader(@NonNull Context context, String url) {
            super(context);
            mURL = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<News> loadInBackground() {
            if (mURL == null) {
                return null;
            }
            //perform network request and fetch data
            List<News> newsList = QueryUtils.fetchNewsData(mURL);
            return newsList;
        }
    }

    //On create options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    //On options item seleted menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
     return super.onOptionsItemSelected(item);
    }


}
