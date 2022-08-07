package me.dio.simulator.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Match(
    val description: String,
    val local: Local,
    val homeTeam: Team,
    val awayTeam: Team,
    var homeTeamScore: Int?,
    var awayTeamScore: Int?
) : Parcelable
