package app.moviebase.tmdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbChange(
    @SerialName("id") val id: Int,
    @SerialName("adult") val adult: Boolean,
)
