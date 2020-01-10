package com.koliksoftware.spendd

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

internal class SmsReaderTest {
    private lateinit var smsReader: SmsReader
    private lateinit var contentResolver: ContentResolver

    private val timeA = LocalDateTime.of(2020, 1, 1, 12, 50)

    private val timeB = LocalDateTime.of(2020, 2, 2, 13, 30)

    private val timeC = LocalDateTime.of(2020, 2, 15, 23, 55)

    @BeforeEach
    fun setUp() {
        val inboxUri = mockk<Uri>()
        val inboxCursor = mockk<Cursor> {
            every { moveToFirst() } returns true
            every { moveToNext() }.returnsMany(true, true, true, false)
            every { getColumnIndexOrThrow("_id") } returns 0
            every { getColumnIndexOrThrow("body") } returns 1
            every { getColumnIndexOrThrow("date") } returns 2
            every { getString(0) }.returnsMany("id-1", "id-2", "id-3")
            every { getString(1) }.returnsMany("Text A", "Text B", "Text C")
            every { getLong(2) }.returnsMany(
                timeA.toInstant(ZoneOffset.UTC).toEpochMilli(),
                timeB.toInstant(ZoneOffset.UTC).toEpochMilli(),
                timeC.toInstant(ZoneOffset.UTC).toEpochMilli()
            )
            every { close() } answers {}
        }

        this.contentResolver = mockk() {
            every {
                query(inboxUri, null, null, null, null)
            } returns inboxCursor
        }

        mockkStatic(Uri::class)
        every { Uri.parse("content://sms/inbox") } returns inboxUri

        this.smsReader = SmsReader()
    }

    @Test
    internal fun `reads all sms for the selected month, from selected sender`() {
        val sms = this.smsReader.readAll(this.contentResolver, 2020, Month.FEBRUARY)
//        assertThat(sms).hasSize(2)
        assertThat(sms[0]).isEqualTo(Sms("id-2", "Text B", timeB))
        assertThat(sms[1]).isEqualTo(Sms("id-3", "Text C", timeC))
    }
}