package com.koliksoftware.spendd

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.math.BigDecimal
import java.time.Month


class SpenddAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val sms = SmsReader().readAll(context.contentResolver, 2020, Month.JANUARY)
        val parser = SmsParser()
        val amount = sms.fold(BigDecimal.ZERO) { acc, one -> parser.parse(one.text).plus(acc) }

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setOnClickPendingIntent(R.id.empty_view, pendingIntent)
                setTextViewText(R.id.empty_view, amount.toPlainString())
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}