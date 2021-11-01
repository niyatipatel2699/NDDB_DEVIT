package com.wajahatkarim3.imagine.data.room.dao

import androidx.room.*
import com.wajahatkarim3.imagine.data.room.entity.Steps
import java.util.*


@Dao
interface StepsDao {

    @Insert
    fun insert(note: Steps):Long

    @Update
    fun update(note: Steps)

    @Delete
    fun delete(note: Steps)

    @Query("delete from Steps")
    fun deleteAllNotes()
    //SELECT * FROM Steps WHERE date('date') = '2021-12-12'
    @Query("select * from Steps")
    fun getSteps(): List<Steps>

    @Query("select * from Steps WHERE date=:date")
    fun getStep(date: String): Steps

    @Query("select * from Steps where ispass=0")
    fun getStepsOnlyNotPass(): List<Steps>

    @Query("select * from Steps where date=(:date)")
    fun getSs(date: String): Steps


    @Query("UPDATE Steps SET ispass=1 WHERE id =:tblId")
    fun updateItem(tblId:Int)

    /*@ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "step") val step: Int?,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "lat") val lat: String?,
    @ColumnInfo(name = "lng") val longitude: String?,
    @ColumnInfo(name = "ispass") val ispass: Boolean*/

    @Query("UPDATE Steps SET step=:step,location=:address,lat=:lat,lng=:lng WHERE id =:tblId")
    fun updateStep(tblId:Int,step:Int,address:String, lat:String, lng:String)

    /*@Query("SELECT * FROM Steps")
    suspend fun getAll(): List<Steps>

    @Insert
    suspend fun insertAll(users: List<Steps>)

    @Delete
    suspend fun delete(steps: Steps)*/

}