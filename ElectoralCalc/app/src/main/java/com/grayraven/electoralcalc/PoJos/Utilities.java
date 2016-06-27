package com.grayraven.electoralcalc.PoJos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jim on 6/23/2016.
 */
@SuppressWarnings("unused")
public class Utilities {

    public static void SortElectionByTitle(ArrayList<Election> elections){
        Collections.sort(elections, new Comparator<Election>() {
            public int compare(Election e1, Election e2) {
                return e1.getTitle().compareTo(e2.getTitle());
            }
        });
    }

    private void sortElectionsByYear(ArrayList<Election> list) {
        Collections.sort(list, new Comparator<Election>() {
            public int compare(Election e1, Election e2) {
                int a = e1.getYear();
                int b = e2.getYear();
                return a - b; // use  b - a to sort high to low
            }
        });
    }

    public static void sortStatesByAbbreviation(ArrayList<State> list) {
        Collections.sort(list, new Comparator<State>() {
            public int compare(State a1, State a2) {
                return a1.getAbbr().compareTo(a2.getAbbr());
            }
        });
    }

    private static void sortStatesByVotes(ArrayList<State> list) {
        Collections.sort(list, new Comparator<State>() {
            public int compare(State a1, State a2) {
                int a = a1.getVotes();
                int b = a2.getVotes();
                return b - a; // use a - b to sort low to high
            }
        });
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean available = false;
        ConnectivityManager mgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = mgr.getActiveNetworkInfo();
        available = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return available;
    }
}
