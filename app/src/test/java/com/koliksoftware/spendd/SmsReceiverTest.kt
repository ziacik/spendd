package com.koliksoftware.spendd

import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SmsReceiverTest {
    private lateinit var smsReceivedIntent: Intent
    private lateinit var context: Context
    private lateinit var smsReceiver: SmsReceiver

    @BeforeEach
    fun setUp() {
        this.smsReceiver = SmsReceiver()
        this.context = mockk()
        this.smsReceivedIntent = mockk {
            every { action } returns Intents.SMS_RECEIVED_ACTION
        }
        mockkStatic(Intents::class)
        every { Intents.getMessagesFromIntent(smsReceivedIntent) } returns emptyArray()
    }

    @Test
    fun onReceive() {
        this.smsReceiver.onReceive(this.context, this.smsReceivedIntent)
    }
}