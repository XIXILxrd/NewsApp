package com.example.news.database.utils

import androidx.room.TypeConverter
import java.text.DateFormat
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { DateFormat.getDateTimeInstance().parse(it)}
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let { DateFormat.getDateTimeInstance().format(it)}
    }
}