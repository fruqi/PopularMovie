package com.amrta.android.popularmovie;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by amrta on 05/11/2016.
 */

public class MyMovieFragment extends Fragment
{
    private static final String TAG = "MyMovieFragment";
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Inflate movie fragment into the view
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mMovieAdapter = new MovieAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mMovieAdapter);

        updateMovie();

        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }


    private void updateMovie() {
        new FetchMovieTask().execute();
    }


    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }


    private class FetchMovieTask extends AsyncTask<Void, Void, String>
    {
        private static final String TAG = "FetchMovieTask";

        @Override
        protected String doInBackground(Void... params)
        {
            String MDB_API = "api_key";

            // Building movie DB api url
            String movieDBUrl = Uri.parse("http://api.themoviedb.org/3/movie/popular")
                    .buildUpon()
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

            String MDB_RESULTS = "results";
            String MDB_TITLE = "original_title";
            String MDB_POSTER = "poster_path";

            try {
                JSONObject movieJson = new JSONObject(jsonString);
                JSONArray results = movieJson.getJSONArray(MDB_RESULTS);

                ArrayList<String> list = new ArrayList<>();

                // Exit if results is empty
                if (results.length() == 0)  {
                    return;
                }

                for (int i = 0; i < results.length(); i++)
                {
                    JSONObject movie = results.getJSONObject(i);
//                    String title = movie.getString(MDB_TITLE);
//                    list.add(title);

                    String posterPath = movie.getString(MDB_POSTER);
                    list.add(posterPath);
                }

                mMovieAdapter.updateList(list);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder>
    {
        private ArrayList<String> posterList;


        public MovieAdapter(ArrayList<String> posterList) {
            this.posterList = posterList;
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);

            return new MovieViewHolder(view, parent.getContext());
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position)
        {
            holder.bindView(posterList.get(position));
        }

        @Override
        public int getItemCount() {
            return posterList.size();
        }

        /*
            Solution from Stackoverlow
            http://stackoverflow.com/questions/30053610/best-way-to-update-data-with-a-recyclerview-adapter
         */
        public void updateList(ArrayList<String> list)
        {
            posterList.clear();
            posterList.addAll(list);
            notifyDataSetChanged();
        }
    }


    private class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;
        private Context mContext;

        public MovieViewHolder(View itemView, Context context)
        {
            super(itemView);
//            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mImageView = (ImageView) itemView;
            mContext = context;
        }

        public void bindView(String url)
        {
            String posterUrl = "http://image.tmdb.org/t/p/w185/" + url;
            Picasso.with(mContext).load(posterUrl).into(mImageView);
        }
    }
}
