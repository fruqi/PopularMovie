package com.amrta.android.popularmovie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by amrta on 13/11/2016.
 */

public class MovieUtil
{
    private static final String mainUrl = "http://image.tmdb.org/t/p/";

    public static String getPosterUrl(String posterUrl)
    {
        String url = mainUrl + "w185/";
        return url + posterUrl;
    }

    public static String getLargePosterUrl (String posterUrl)
    {
        String url = mainUrl + "w342/";
        return url + posterUrl;
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
