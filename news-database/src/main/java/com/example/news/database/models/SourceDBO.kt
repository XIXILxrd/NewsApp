package com.example.news.database.models

import androidx.room.ColumnInfo

data class SourceDBO(
    @ColumnInfo("id") val id: String,
    @ColumnInfo("name") val name: String,
)
