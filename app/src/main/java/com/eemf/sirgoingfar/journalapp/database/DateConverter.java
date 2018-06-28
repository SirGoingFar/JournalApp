package com.eemf.sirgoingfar.journalapp.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Long toTimeStamp(Date date){
        return date != null ? date.getTime() : null;
    }

    @TypeConverter
    public static Date toDate(Long timeStamp){
        return timeStamp != null ? new Date(timeStamp) : null;
    }
}
