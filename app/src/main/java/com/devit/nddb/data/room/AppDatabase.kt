package com.wajahatkarim3.imagine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devit.nddb.utils.Converters
import com.wajahatkarim3.imagine.data.room.dao.StepsDao
import com.wajahatkarim3.imagine.data.room.entity.Steps

@TypeConverters(Converters::class)
@Database(entities = [Steps::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stepsDao(): StepsDao

}