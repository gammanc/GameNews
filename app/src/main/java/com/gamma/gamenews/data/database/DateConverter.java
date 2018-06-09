package com.gamma.gamenews.data.database;

/**
 * Created by emers on 6/6/2018.
 */

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
