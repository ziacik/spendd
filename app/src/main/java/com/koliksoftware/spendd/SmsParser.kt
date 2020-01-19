package com.koliksoftware.spendd

import java.math.BigDecimal

class SmsParser {
    fun parse(sms: Sms): Item? {
        if (!isCsobSms(sms)) {
            return null
        }

        val regex = "Suma: ([+-]?)([0-9]+,[0-9]+)".toRegex()
        val amount = regex.find(sms.text)?.groupValues?.get(2)
        val shouldNegate = regex.find(sms.text)?.groupValues?.get(1) != "+"

        return if (amount == null) null else toItem(sms, toAmountVal(amount, shouldNegate))
    }

    private fun toItem(sms: Sms, amount: BigDecimal): Item {
        return Item(sms.date, amount, "")
    }

    private fun toAmountVal(amount: String, shouldNegate: Boolean): BigDecimal {
        val amountVal = BigDecimal(amount.replace(',', '.'))
        return if (shouldNegate) amountVal.negate() else amountVal
    }

    private fun isCsobSms(sms: Sms) = sms.text.contains("CSOB")
}