package com.example.riji.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.CHINA)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA)
    private val fullFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA)

    fun formatDate(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(displayFormatter)
    }

    fun formatTime(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(timeFormatter)
    }

    fun formatFullDate(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(fullFormatter)
    }

    fun daysBetween(timestamp1: Long, timestamp2: Long): Long {
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(date1, date2)
    }

    fun daysUntil(targetDate: Long): Long {
        val today = LocalDate.now()
        val target = Instant.ofEpochMilli(targetDate).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(today, target)
    }

    fun daysSince(startDate: Long): Long {
        val today = LocalDate.now()
        val start = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(start, today)
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun getStartOfMonth(year: Int, month: Int): Long {
        return LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun getEndOfMonth(year: Int, month: Int): Long {
        return LocalDate.of(year, month, 1)
            .plusMonths(1)
            .minusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    // Zodiac (Chinese zodiac)
    fun getChineseZodiac(year: Int): String {
        val zodiacs = listOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")
        return zodiacs[(year - 4) % 12]
    }

    // Western constellation
    fun getConstellation(month: Int, day: Int): String {
        return when {
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "水瓶座"
            (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "双鱼座"
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "白羊座"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "金牛座"
            (month == 5 && day >= 21) || (month == 6 && day <= 21) -> "双子座"
            (month == 6 && day >= 22) || (month == 7 && day <= 22) -> "巨蟹座"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "狮子座"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "处女座"
            (month == 9 && day >= 23) || (month == 10 && day <= 23) -> "天秤座"
            (month == 10 && day >= 24) || (month == 11 && day <= 22) -> "天蝎座"
            (month == 11 && day >= 23) || (month == 12 && day <= 21) -> "射手座"
            else -> "摩羯座"
        }
    }

    fun getAge(birthDate: Long): Int {
        val birth = Instant.ofEpochMilli(birthDate).atZone(ZoneId.systemDefault()).toLocalDate()
        return Period.between(birth, LocalDate.now()).years
    }

    fun getVirtualAge(birthDate: Long): Int {
        // 虚岁: age at birth is 1, +1 each lunar new year
        val birth = Instant.ofEpochMilli(birthDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        val realAge = Period.between(birth, now).years
        return realAge + 1
    }

    fun daysInMonth(year: Int, month: Int): Int {
        return YearMonth.of(year, month).lengthOfMonth()
    }

    fun getDayOfWeek(year: Int, month: Int, day: Int): String {
        val dayOfWeek = LocalDate.of(year, month, day).dayOfWeek
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "一"
            DayOfWeek.TUESDAY -> "二"
            DayOfWeek.WEDNESDAY -> "三"
            DayOfWeek.THURSDAY -> "四"
            DayOfWeek.FRIDAY -> "五"
            DayOfWeek.SATURDAY -> "六"
            DayOfWeek.SUNDAY -> "日"
        }
    }
}
