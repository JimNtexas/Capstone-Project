package com.grayraven.electoralcalc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class ElectionWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.election_widget);
        String daysToGo = context.getResources().getString (R.string.days_to_go_format, getDaysToGo());
        views.setTextViewText(R.id.days_to_go_txt, daysToGo);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch MainActivity
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.election_widget);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.the_widget, pendingIntent);

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    //November 3, 2020
    //November 8, 2016
    public static String getDaysToGo() {
        Date electionDay = new Date(2016-1900,10,8);
        long today = System.currentTimeMillis();
        long toGo =  electionDay.getTime();
        long diff = toGo - today;
        if(diff < 0.0) {
            electionDay = new Date(2020-1900,10,3);
            diff = electionDay.getTime() - today;
        }
        long days = diff/(24*60*60*1000);
        return  String.valueOf(days + 1); // add one day because most people would start counting with today

    }
}

