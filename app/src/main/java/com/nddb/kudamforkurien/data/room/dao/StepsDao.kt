package com.nddb.kudamforkurien.data.room.dao

import androidx.room.*
import com.nddb.kudamforkurien.data.room.entity.Steps
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
    fun deleteSteps()

    @Query("select * from Steps")
    fun getSteps(): List<Steps>

    @Query("select sum(step) from Steps")
    fun getTotalSteps(): Int

    @Query("select * from Steps WHERE date=:date")
    fun getStep(date: String): Steps

    @Query("select * from Steps ORDER BY id DESC LIMIT 1")
    fun getLastStep(): Steps

    @Query("select * from Steps where ispass=0")
    fun getStepsOnlyNotPass(): List<Steps>

    /*@Query("UPDATE Steps SET ispass=1 WHERE id =:tblId")
    fun updateItem(tblId:Int)*/

    @Query("UPDATE Steps SET ispass=:ispass,step=:step,location=:address,lat=:lat,lng=:lng WHERE id =:tblId")
    fun updateStep(tblId:Int,step:Int,address:String, lat:String, lng:String,ispass:Boolean)


}