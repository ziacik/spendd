package com.koliksoftware.spendd

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

internal class SmsParserTest {
    private lateinit var parser: SmsParser

    @BeforeEach
    fun setUp() {
        this.parser = SmsParser()
    }

    @Test
    fun `parse returns null for an arbitrary sms`() {
        val result = this.parser.parse(smsWithText("An arbitrary sms"))
        assertThat(result).isNull()
    }

    @Test
    fun `parse returns null for an sms that contains an amount but does not contain "CSOB"`() {
        val result = this.parser.parse(smsWithText("Something Suma: 12,50 EUR"))
        assertThat(result).isNull()
    }

    @Test
    fun `parse returns null for an sms that contains unparsable amount and "CSOB"`() {
        val result = this.parser.parse(smsWithText("CSOB Suma: EUR"))
        assertThat(result).isNull()
    }

    @Test
    fun `parse returns an item with actual value from sms that contains an amount and "CSOB", with negative amount if no sign is specified`() {
        val sms = smsWithText("CSOB Suma: 12,50 EUR")
        val result = this.parser.parse(sms)
        assertThat(result?.amount).isEqualTo(BigDecimal("-12.50"))
        assertThat(result?.doneAt).isEqualTo(sms.date)
    }

    @Test
    fun `parse returns an item with actual value from sms that contains an amount and "CSOB", with positive amount if positive sign is specified`() {
        val sms = smsWithText("CSOB Suma: +12,50 EUR")
        val result = this.parser.parse(sms)
        assertThat(result?.amount).isEqualTo(BigDecimal("12.50"))
        assertThat(result?.doneAt).isEqualTo(sms.date)
    }

    private fun smsWithText(text: String): Sms {
        return Sms("some-id", text, LocalDateTime.now())
    }
}