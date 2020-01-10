package com.koliksoftware.spendd

import java.math.BigDecimal

class SmsParser {
    fun parse(sms: String): BigDecimal {
        if (!isCsobSms(sms)) {
            return BigDecimal.ZERO
        }

        val regex = "Suma: ([+-]?)([0-9]+,[0-9]+)".toRegex()
        val amount = regex.find(sms)?.groupValues?.get(2)
        val shouldNegate = regex.find(sms)?.groupValues?.get(1) != "+"

        return if (amount == null) BigDecimal.ZERO else toAmountVal(amount, shouldNegate)
    }

    private fun toAmountVal(amount: String, shouldNegate: Boolean): BigDecimal {
        val amountVal = BigDecimal(amount.replace(',', '.'))
        return if (shouldNegate) amountVal.negate() else amountVal
    }

    private fun isCsobSms(sms: String) = sms.contains("CSOB")
}