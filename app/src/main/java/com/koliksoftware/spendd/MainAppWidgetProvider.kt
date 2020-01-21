package com.koliksoftware.spendd

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.RemoteViews
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.*

class MainAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val sms = SmsReader().readAll(context.contentResolver, 2020, Month.JANUARY)
        val parser = SmsParser()
        val items = sms
            .mapNotNull { parser.parse(it) }

        val today = LocalDate.now()
        val weekOfYearField: TemporalField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
        val monthSpendings = items.filter { it.amount < BigDecimal.ZERO }.fold(BigDecimal.ZERO) { acc, one -> one.amount.plus(acc) }
        val weekSpendings = items.filter { it.amount < BigDecimal.ZERO }.filter { it.doneAt.toLocalDate().get(weekOfYearField) == today.get(weekOfYearField) }
            .fold(BigDecimal.ZERO) { acc, one -> one.amount.plus(acc) }
        val daySpendings =
            items.filter { it.amount < BigDecimal.ZERO }.filter { it.doneAt.toLocalDate() == today }.fold(BigDecimal.ZERO) { acc, one -> one.amount.plus(acc) }

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setOnClickPendingIntent(R.id.spendings_month, pendingIntent)
                setTextViewText(R.id.spendings_month, monthSpendings.toPlainString())
                setTextViewText(R.id.spendings_week, weekSpendings.toPlainString())
                setTextViewText(R.id.spendings_day, daySpendings.toPlainString())
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, MainAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }
}