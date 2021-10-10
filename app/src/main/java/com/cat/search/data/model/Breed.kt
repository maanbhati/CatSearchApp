package com.cat.search.data.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "breed_table")
data class Breed(
    @PrimaryKey(autoGenerate = true)
    val breedId: Int = 0,
    val id: String?,
    val image: @RawValue Image?,
    val name: String?,
    val temperament: String?,
    val wikipedia_url: String?,
    val energy_level: Int?,
) : Parcelable