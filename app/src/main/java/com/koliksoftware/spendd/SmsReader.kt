package com.koliksoftware.spendd

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class SmsReader {
    fun readAll(contentResolver: ContentResolver, year: Int, month: Month): List<Sms> {
        val inboxUri = Uri.parse("content://sms/inbox")
        val cursorOrNull: Cursor? = contentResolver.query(inboxUri, null, null, null, null)

        cursorOrNull.use {
            val cursor = cursorOrNull ?: return emptyList()

            return sequenceFrom(cursor)
                .map { asSms(it) }
                .filter { it.date.year == year && it.date.month == month }
                .toList()
        }
    }

    private fun sequenceFrom(cursor: Cursor): Sequence<Cursor> {
        return generateSequence(
            { cursor.takeIf { it.moveToFirst() } },
            { cursor.takeIf { it.moveToNext() } }
        )
    }

    private fun asSms(cursor: Cursor): Sms {
        val id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
        val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("date"))), ZoneId.systemDefault()
        )

//        Log.i("XXX", body)

        return Sms(id, body, date)
    }
}
