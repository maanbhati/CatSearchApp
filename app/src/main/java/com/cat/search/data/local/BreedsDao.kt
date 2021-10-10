package com.cat.search.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cat.search.data.model.Breed

@Dao
interface BreedsDao {
    @Query("SELECT * FROM breed_table")
    fun getBreeds(): LiveData<List<Breed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreed(breed: Breed): Long

    @Delete
    suspend fun deleteBreed(breed: Breed)
}