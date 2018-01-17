package com.example.janari.SimpleDailyBudgetApp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

    private static final String ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE";
    public static final String ACTION_UPDATE = "yourpackage.TEXT_CHANGED";
    private static int mCounter = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        // Construct an Intent which is pointing this class.
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(ACTION_SIMPLEAPPWIDGET);
        // And this time we are sending a broadcast with getBroadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.text, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            Intent intent = new Intent(context, RegisterActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.btn, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        /*if (intent.getAction().equals(ACTION_UPDATE)) {
            // handle intent here
            String s = intent.getStringExtra("budget");

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.text, "tuli kohale :D");

            ComponentName appWidget = new ComponentName(context, Widget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            appWidgetManager.updateAppWidget(appWidget, views);
        }*/

        if (ACTION_SIMPLEAPPWIDGET.equals(intent.getAction())) {


            Bundle extras = intent.getExtras();
            String s = extras.getString("budget");

            if (extras == null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.text, "shitkit pole :D");

                ComponentName appWidget = new ComponentName(context, Widget.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                appWidgetManager.updateAppWidget(appWidget, views);
            } else {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.text, s);

                ComponentName appWidget = new ComponentName(context, Widget.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                appWidgetManager.updateAppWidget(appWidget, views);
            }


        }
    }
}





