package app.moviebase.tmdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbChangePageResult(
    @SerialName("page") override val page: Int,
    @SerialName("results") override val results: List<TmdbChange> = emptyList(),
    @SerialName("total_results") override val totalResults: Int,
    @SerialName("total_pages") override val totalPages: Int
) : TmdbPageResult<TmdbChange>

@Serializable
data class TmdbChange(
    @SerialName("id") val id: Int,
    @SerialName("adult") val adult: Boolean,
)
