package com.example.mycookbook.utils

import androidx.room.TypeConverter

fun getReceiptCategory(position: Int): Char {
    when (position) {
        0 -> return 'O'
        1 -> return 'A'
        2 -> return 'S'
        3 -> return 'M'
        else -> return 'D'
    }
}

fun setReceiptCategory(cat: Char): Int {
    when (cat) {
        'O' -> return 0
        'A' -> return 1
        'S' -> return 2
        'M' -> return 3
        else -> return 4
    }
}

class Converters {

    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
    }
}

