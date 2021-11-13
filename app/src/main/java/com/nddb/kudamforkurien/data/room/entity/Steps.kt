package com.nddb.kudamforkurien.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Steps(
  /*  @PrimaryKey(autoGenerate = true) val id: Int,*/
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "step") val step: Int?,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "lat") val lat: String?,
    @ColumnInfo(name = "lng") val longitude: String?,
    @ColumnInfo(name = "ispass") val ispass: Boolean

){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // or id: Int? = null

}