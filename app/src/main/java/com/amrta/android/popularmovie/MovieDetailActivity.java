package com.amrta.android.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailActivity";
    private static final String EXTRA_MOVIE = "com.amrta.android.popularmovie.movie";


    public static Intent newIntent(Context context, Parcelable movieParcelable) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movieParcelable);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Retrieve parcelable extra from the intent
        Movie movie = (Movie) getIntent().getParcelableExtra(EXTRA_MOVIE);

        TextView titleTextView = (TextView) findViewById(R.id.movie_title_textView);
        titleTextView.setText(movie.getTitle());

        ImageView posterImageView = (ImageView) findViewById(R.id.movie_poster_imageView);
        Picasso.with(getApplicationContext()).load(MovieUtil.getPosterUrl(movie.getPoster())).into(posterImageView);

        TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date_textView);
        String[] dateData = movie.getReleaseDate().split("-");

        if (dateData.length > 0) {
            String yearOfRelease = dateData[0];
            releaseDateTextView.setText(yearOfRelease);
        } else {
            releaseDateTextView.setText("-");
        }

        TextView ratingTextView = (TextView) findViewById(R.id.movie_rating_textView);
        String averageRating = String.format(getResources().getString(R.string.average_rating_text), movie.getRating());
        ratingTextView.setText(averageRating);

        TextView overviewTextView = (TextView) findViewById(R.id.movie_overview_textView);
        overviewTextView.setText(movie.getOverview());
    }


}
