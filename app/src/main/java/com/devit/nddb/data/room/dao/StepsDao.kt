package com.wajahatkarim3.imagine.data.room.dao

import androidx.room.*
import com.wajahatkarim3.imagine.data.room.entity.Steps


@Dao
interface StepsDao {

    @Insert
    fun insert(note: Steps)

    @Update
    fun update(note: Steps)

    @Delete
    fun delete(note: Steps)

    @Query("delete from Steps")
    fun deleteAllNotes()
    //SELECT * FROM Steps WHERE date('date') = '2021-12-12'
    @Query("select * from Steps")
    fun getSteps(): List<Steps>

    /*@Query("SELECT * FROM Steps")
    suspend fun getAll(): List<Steps>

    @Insert
    suspend fun insertAll(users: List<Steps>)

    @Delete
    suspend fun delete(steps: Steps)*/

}