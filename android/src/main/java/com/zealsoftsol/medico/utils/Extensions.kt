package com.zealsoftsol.medico.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.parseDateToMmYy(): String {

    val inputFormatter: DateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    val outputFormatter: DateFormat = SimpleDateFormat("MM/yy", Locale.getDefault())

    val date: Date
    return try {
        date = inputFormatter.parse(this)
        outputFormatter.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}







