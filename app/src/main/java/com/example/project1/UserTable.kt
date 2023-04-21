package com.example.project1
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_table")
data class UserTable(
    @field:ColumnInfo(name = "fullName")
    @field:PrimaryKey
    var fullName: String,
    @field:ColumnInfo(name = "cityCountry")
    var cityCountry: String,
    @field:ColumnInfo(name = "activityLevel")
    var activityLevel: String,
    @field:ColumnInfo(name = "sex")
    var sex: String,
    @field:ColumnInfo(name = "weight")
    var weight: String,
    @field:ColumnInfo(name = "height")
    var height: String,
    @field:ColumnInfo(name = "age")
    var age: String,
)