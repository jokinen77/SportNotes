/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils.dao;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DateTimeType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author jaakko
 */
public class OffsetDateTimePresister extends DateTimeType {
    
    private static final OffsetDateTimePresister singleton = new OffsetDateTimePresister();

    private OffsetDateTimePresister() {
        super(SqlType.LONG, new Class<?>[]{LocalDateTime.class});
    }

    public static OffsetDateTimePresister getSingleton() {
        return singleton;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        OffsetDateTime dateTime = (OffsetDateTime) javaObject;
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.toEpochSecond();
        }
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond((Long) sqlArg, 0, ZoneOffset.UTC);
        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }
}
