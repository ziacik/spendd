package com.koliksoftware.spendd

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class SmsParserTest {
    private lateinit var parser: SmsParser

    @BeforeEach
    fun setUp() {
        this.parser = SmsParser()
    }

    @Test
    fun `parse returns 0 for an arbitrary sms`() {
        val result = this.parser.parse("An arbitrary sms")
        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `parse returns 0 for an sms that contains an amount but does not contain "CSOB"`() {
        val result = this.parser.parse("Something Suma: 12,50 EUR")
        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `parse returns 0 for an sms that contains unparsable amount and "CSOB"`() {
        val result = this.parser.parse("CSOB Suma: EUR")
        assertThat(result).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `parse returns actual value from an sms that contains an amount and "CSOB", with negative amount if no sign is specified`() {
        val result = this.parser.parse("CSOB Suma: 12,50 EUR")
        assertThat(result).isEqualTo(BigDecimal("-12.50"))
    }

    @Test
    fun `parse returns actual value from an sms that contains an amount and "CSOB", with positive amount if positive sign is specified`() {
        val result = this.parser.parse("CSOB Suma: +12,50 EUR")
        assertThat(result).isEqualTo(BigDecimal("12.50"))
    }
}