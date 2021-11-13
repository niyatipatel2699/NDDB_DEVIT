package com.nddb.kudamforkurien.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nddb.kudamforkurien.utils.Converters
import com.nddb.kudamforkurien.data.room.dao.StepsDao
import com.nddb.kudamforkurien.data.room.entity.Steps

@TypeConverters(Converters::class)
@Database(entities = [Steps::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stepsDao(): StepsDao

}