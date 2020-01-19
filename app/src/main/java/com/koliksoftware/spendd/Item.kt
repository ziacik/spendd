package com.koliksoftware.spendd

import java.math.BigDecimal
import java.time.LocalDateTime

data class Item(val doneAt: LocalDateTime, val amount: BigDecimal, val purpose: String)
