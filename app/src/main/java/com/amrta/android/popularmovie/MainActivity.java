package com.amrta.android.popularmovie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new MovieFragment())
                        .commit();
        }
    }
}
