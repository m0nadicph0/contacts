package com.example.contacts

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("select * from `contacts`")
    fun fetchAll(): Flow<List<Contact>>

    @Query("select * from `contacts` where id=:id")
    fun fetchById(id: Int): Flow<Contact>
}