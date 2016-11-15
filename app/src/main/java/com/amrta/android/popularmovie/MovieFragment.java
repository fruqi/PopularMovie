package com.amrta.android.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrta on 08/11/2016.
 */

public class MovieFragment extends Fragment {

    private static final String TAG = "MovieFragment";
    private static final String BUNDLE_MOVIE = "bundle_movie";

    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovieList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_MOVIE)) {
            mMovieList = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIE);
            Log.i(TAG, "onCreate | savedInstance");
        } else {
            mMovieList = new ArrayList<>();
            Log.i(TAG, "onCreate | no savedInstance");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView | ");

        // Inflate movie fragment into the view
        View rootView = inflater.inflate(R.layout.fragment_my_movie, container, false);

        mGridView = (GridView)  rootView.findViewById(R.id.movie_gridview);
        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);

        // Set empty list for first time launch
        mGridView.setAdapter(mMovieAdapter);

        // Set click listener on movie poster grid
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "onItemClick | position: " + position);

                MovieAdapter adapter = (MovieAdapter) parent.getAdapter();

                if (adapter != null) {
                    // Get selected movie item from adapter
                    Movie movie = (Movie) parent.getAdapter().getItem(position);

                    // Create MovieDetailActivity intent, and pass movie parcelable object
                    Intent intent = MovieDetailActivity.newIntent(getActivity(), movie);
                    startActivity(intent);
                }
            }
        });

        updateMovie();

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_MOVIE, (ArrayList<Movie>) mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume | ");
        updateMovie();
    }
    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_movie, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent intent = SettingsActivity.newIntent(getActivity());
            startActivity(intent);
        }

        return true;
    }


    private void updateMovie()
    {
        // Get selected sort preference
        String sortPreference = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        Log.i(TAG, "updateMovie | sortPreference: " + sortPreference); 
        
        if (MovieUtil.isOnline(getContext())) { 
            new FetchMovieTask().execute(sortPreference);
        } else {
            
        }
    }


    private class FetchMovieTask extends AsyncTask<String, Void, String>
    {
        private static final String TAG = "FetchMovieTask";

        @Override
        protected String doInBackground(String... params)
        {
            String MDB_API = "api_key";
            String sortPreference = params[0];

            // Building movie DB api url
            String movieDBUrl = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon()
                    .appendPath(sortPreference)
                    .appendQueryParameter(MDB_API, BuildConfig.MOVIE_DB_API_KEY)
                    .build()
                    .toString();

            HttpURLConnection connection = null;
            StringBuffer buffer;
            BufferedReader reader = null;

            try
            {
                // Prepare connection to movie db
                URL url = new URL(movieDBUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Initiate connection to url
                InputStream in = connection.getInputStream();
                buffer = new StringBuffer();

                // exit if there is no result from connection
                if (in == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(in));
                String line;

                if ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null)
                {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String jsonString)
        {
            super.onPostExecute(jsonString);

            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_POSTER = "poster_path";
            final String MDB_OVERVIEW = "overview";
            final String MDB_USER_RATING = "vote_average";
            final String MDB_RELEASE_DATE = "release_date";


            try {
                JSONObject movieJson = new JSONObject(jsonString);
                JSONArray results = movieJson.getJSONArray(MDB_RESULTS);

                ArrayList<Movie> list = new ArrayList<>();

                // Exit if results is empty
                if (results.length() == 0)  {
                    return;
                }

                for (int i = 0; i < results.length(); i++)
                {
                    JSONObject movie = results.getJSONObject(i);
                    String title = movie.getString(MDB_TITLE);
                    String posterPath = movie.getString(MDB_POSTER);
                    String overview = movie.getString(MDB_OVERVIEW);
                    double rating = movie.getDouble(MDB_USER_RATING);
                    String releaseDate = movie.getString(MDB_RELEASE_DATE);
//                    list.add(title);
                    Movie movieObj = new Movie(title, posterPath, overview, rating, releaseDate);
                    list.add(movieObj);
                }

                mMovieAdapter.clear();
                mMovieAdapter.addAll(list);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class MovieAdapter extends ArrayAdapter
    {
        private static final String mainUrl =  "http://image.tmdb.org/t/p/w342/";

        MovieAdapter(Context context, List<Movie> movies)
        {
            super(context, 0, movies);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Movie movie = (Movie) getItem(position);

            // Check if layout has been initialized before
            // If it is, reuse it
            // Else, create a new layout
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item, parent, false);
            }

            // Initialize root view; a view to put our ImageView

            if (movie != null && !movie.getPoster().isEmpty()) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_imageView);
                Picasso.with(getContext()).load(mainUrl + movie.getPoster()).into(imageView);
            }

            return convertView;
        }
    }
}
