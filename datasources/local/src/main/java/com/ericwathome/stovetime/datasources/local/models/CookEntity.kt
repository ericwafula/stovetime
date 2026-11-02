package com.ericwathome.stovetime.datasources.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class CookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dishId: String,
    val kind: String,
    val name: String,
    val photo: String?,
    val startedAt: Long,
    val finishedAt: Long,
    val xp: Int,
    val rating: String?,
)

data class CookCache(
    val id: Long,
    val dishId: String,
    val kind: String,
    val name: String,
    val photo: String?,
    val startedAt: Long,
    val finishedAt: Long,
    val xp: Int,
    val rating: String?,
)
