package app.moviebase.tmdb.url

import app.moviebase.tmdb.TmdbWebConfig
import app.moviebase.tmdb.model.TmdbMediaType
import app.moviebase.tmdb.model.TmdbVideo
import app.moviebase.tmdb.model.TmdbVideoSite

object TmdbUrlBuilder {

    /**
     * Example: https://www.themoviedb.org/u/msbreviews
     */
    fun buildUserPage(userId: String) = "${TmdbWebConfig.BASE_WEBSITE_URL}/u/$userId"

    /**
     * Example: https://www.themoviedb.org/review/63501e9ed363e5007a664110
     */
    fun buildReviewPage(reviewId: Int) = "${TmdbWebConfig.BASE_WEBSITE_URL}/review/$reviewId"

    /**
     * Example: https://www.themoviedb.org/tv/96677-lupin/watch?locale=AU
     */
    fun buildWatch(mediaType: TmdbMediaType, mediaId: Int, region: String): String {
        require(mediaType == TmdbMediaType.MOVIE || mediaType == TmdbMediaType.SHOW) { "Only movie and tv are supported" }

        val tmdbMediaType = if (mediaType == TmdbMediaType.MOVIE) "movie" else "tv"
        return "${TmdbWebConfig.BASE_WEBSITE_URL}/$tmdbMediaType/$mediaId/watch?locale=$region"
    }

    /**
     * https://www.themoviedb.org/list/82963
     */
    fun buildList(listId: Int): String = "${TmdbWebConfig.BASE_WEBSITE_URL}/list/$listId"

    /**
     * Build the video URL depending on the site the video is from
     */
    fun buildVideo(tmdbVideo: TmdbVideo): String? = when (tmdbVideo.site) {
        TmdbVideoSite.YOUTUBE -> "https://www.youtube.com/watch?v=${tmdbVideo.key}"
        TmdbVideoSite.VIMEO -> "https://vimeo.com/${tmdbVideo.key}"
        else -> null
    }

    /**
     * Build the media page URL depending on the media type.
     *
     * Example:
     * https://www.themoviedb.org/movie/9257
     * https://www.themoviedb.org/tv/7179/season/8/episode/1
     */
    fun buildMedia(
        mediaType: TmdbMediaType,
        parentId: Int,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null,
    ): String {
        return when (mediaType) {
            TmdbMediaType.MOVIE -> "${TmdbWebConfig.BASE_WEBSITE_URL}/movie/$parentId"
            TmdbMediaType.SHOW -> "${TmdbWebConfig.BASE_WEBSITE_URL}/tv/$parentId"
            TmdbMediaType.SEASON -> "${TmdbWebConfig.BASE_WEBSITE_URL}/tv/$parentId/season/$seasonNumber"
            TmdbMediaType.EPISODE -> "${TmdbWebConfig.BASE_WEBSITE_URL}/tv/$parentId/season/$seasonNumber/episode/$episodeNumber"
        }
    }

    /**
     * Build person page URL.
     * https://www.themoviedb.org/person/1456295
     */
    fun buildPerson(personId: Int): String = "${TmdbWebConfig.BASE_WEBSITE_URL}/person/$personId"
}
