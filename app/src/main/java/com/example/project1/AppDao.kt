package com.example.project1

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Insert ignore
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userTable: UserTable)

    @Update()
    fun updateUsers(vararg userTable: UserTable)

    // Delete all
    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Query("UPDATE user_table SET active=false WHERE active=true")
    suspend fun deactivateAll()

    // Get all the weather info that is currently in the database
    // automatically triggered when the db is updated because of Flow<List<WeatherTable>>
    @Query("SELECT * from user_table where active=true")
    fun getUser(): List<UserTable>
}