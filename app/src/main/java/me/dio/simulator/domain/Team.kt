package me.dio.simulator.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Team(
    val name: String,
    val strength: Int,
    val image: String
) : Parcelable
