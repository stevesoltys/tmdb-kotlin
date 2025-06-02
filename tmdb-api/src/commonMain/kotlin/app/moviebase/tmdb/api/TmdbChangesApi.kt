package app.moviebase.tmdb.api

import app.moviebase.tmdb.core.endPointV3
import app.moviebase.tmdb.core.parameters
import app.moviebase.tmdb.model.TmdbChange
import app.moviebase.tmdb.model.TmdbResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TmdbChangesApi internal constructor(private val client: HttpClient) {

    suspend fun getMovieChanges(page: Int, startDate: String, endDate: String): TmdbResult<TmdbChange> = client.get {
        endPointV3("movie/changes")
        parameters(
            mapOf(
                "page" to page,
                "start_date" to startDate,
                "end_date" to endDate
            )
        )
    }.body()

    suspend fun getTvChanges(page: Int, startDate: String, endDate: String): TmdbResult<TmdbChange> = client.get {
        endPointV3("tv/changes")
        parameters(
            mapOf(
                "page" to page,
                "start_date" to startDate,
                "end_date" to endDate
            )
        )
    }.body()
}
