@file:Suppress("ktlint")

package app.moviebase.tmdb.api

import app.moviebase.tmdb.core.mockHttpClient
import app.moviebase.tmdb.model.AppendResponse
import app.moviebase.tmdb.model.TmdbDepartment
import app.moviebase.tmdb.model.TmdbGender
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test

class TmdbPeopleApiTest {

    val client = mockHttpClient(
        version = 3,
        responses = mapOf(
            "person/287?language=en-US&append_to_response=external_ids,images,tagged_images,movie_credits,tv_credits"
                to "person/person_details_287.json",
            "person/19292/movie_credits?language=en-US"
                to "person/person_movie_credits_19292.json",
            "person/19292/tv_credits?language=en-US"
                to "person/person_tv_credits_19292.json",
            "person/11701?language=en-DE&append_to_response=external_ids,images,tagged_images,combined_credits,movie_credits,tv_credits,translations"
                to "person/person_details_11701_all_responses.json",
            "person/2232?language=en-US&append_to_response=movie_credits"
                to "person/person_details_2232_sparse_movie_credits.json",
            "person/21688/movie_credits?language=en-US"
                to "person/person_movie_credits_21688_sparse_media.json",
            "person/21688/tv_credits?language=en-US"
                to "person/person_tv_credits_21688_sparse.json",
        ),
    )

    val classToTest = TmdbPeopleApi(client)

    @Test
    fun `it can fetch person details`() = runTest {
        val personDetail = classToTest.getDetails(
            personId = 287,
            language = "en-US",
            appendResponses = listOf(
                AppendResponse.EXTERNAL_IDS,
                AppendResponse.IMAGES,
                AppendResponse.TAGGED_IMAGES,
                AppendResponse.MOVIE_CREDITS,
                AppendResponse.TV_CREDITS,
            ),
        )

        assertThat(personDetail.id).isEqualTo(287)
        assertThat(personDetail.externalIds).isNotNull()
        assertThat(personDetail.movieCredits).isNotNull()
        assertThat(personDetail.tvCredits).isNotNull()
    }

    /**
     * external_ids,images,tagged_images,combined_credits,movie_credits,tv_credits,translations
     */
    @Test
    fun `it can fetch person details with all responses`() = runTest {
        val personDetail = classToTest.getDetails(
            personId = 11701,
            language = "en-DE",
            appendResponses = listOf(
                AppendResponse.EXTERNAL_IDS,
                AppendResponse.IMAGES,
                AppendResponse.TAGGED_IMAGES,
                AppendResponse.COMBINED_CREDITS,
                AppendResponse.MOVIE_CREDITS,
                AppendResponse.TV_CREDITS,
                AppendResponse.TRANSLATIONS,
            ),
        )

        // Add your assertions here
        assertEquals(false, personDetail.adult)
        assertTrue(personDetail.biography!!.startsWith("Angelina Jolie is an American actress"))
        assertEquals(LocalDate.parse("1975-06-04"), personDetail.birthday)
        assertEquals(null, personDetail.deathday)
        assertEquals(TmdbGender.FEMALE, personDetail.gender)
        assertEquals("https://www.unhcr.org/pages/49c3646c56.html", personDetail.homepage)
        assertEquals(11701, personDetail.id)
        assertEquals("nm0001401", personDetail.imdbId)
        assertEquals(TmdbDepartment.ACTING, personDetail.knownForDepartment)
        assertEquals("Angelina Jolie", personDetail.name)
        assertEquals("Los Angeles, California, USA ", personDetail.placeOfBirth)
        assertEquals(41.859f, personDetail.popularity)
        assertEquals("/9kkfIiufGORw4bELN79DuTaKqAy.jpg", personDetail.profilePath)
        assertEquals("nm0001401", personDetail.externalIds?.imdbId)
        assertEquals("Q13909", personDetail.externalIds?.wikidata)
        assertEquals("", personDetail.externalIds?.facebook)
        assertEquals("angelinajolie", personDetail.externalIds?.instagram)
        assertEquals(null, personDetail.externalIds?.tvdbId)
        assertEquals("", personDetail.externalIds?.twitter)
        assertEquals(null, personDetail.externalIds?.youtube)
    }

    @Test
    fun `it can fetch show credits`() = runTest {
        val showCredits = classToTest.getShowCredits(
            personId = 19292,
            language = "en-US",
        )

        assertThat(showCredits.cast).isNotNull()
        assertThat(showCredits.crew).isNotNull()
    }

    @Test
    fun `it can fetch movie credits`() = runTest {
        val showCredits = classToTest.getMovieCredits(
            personId = 19292,
            language = "en-US",
        )

        assertThat(showCredits.cast).isNotNull()
        assertThat(showCredits.crew).isNotNull()
    }

    @Test
    fun `it can fetch person details when one movie credit entry omits four fields`() = runTest {
        val personDetail = classToTest.getDetails(
            personId = 2232,
            language = "en-US",
            appendResponses = listOf(AppendResponse.MOVIE_CREDITS),
        )

        val movieCredits = personDetail.movieCredits ?: error("movie_credits must be present")
        assertThat(movieCredits.cast).hasSize(3)
        assertThat(movieCredits.crew).hasSize(1)

        // The sparse entry survives with the fields TMDB did send.
        val sparseCredit = movieCredits.cast[1]
        assertThat(sparseCredit.id).isEqualTo(102)
        assertThat(sparseCredit.title).isEqualTo("Sparse Movie")
        assertThat(sparseCredit.character).isEqualTo("Sami Sparse")
        assertThat(sparseCredit.releaseDate).isEqualTo(LocalDate.parse("2021-03-05"))

        // The four omitted keys fall back to neutral defaults.
        assertThat(sparseCredit.genreIds).isEmpty()
        assertThat(sparseCredit.popularity).isEqualTo(0f)
        assertThat(sparseCredit.voteAverage).isEqualTo(0f)
        assertThat(sparseCredit.voteCount).isEqualTo(0)

        // The other entries keep their real values.
        val firstCredit = movieCredits.cast[0]
        assertThat(firstCredit.genreIds).containsExactly(18, 10749)
        assertThat(firstCredit.popularity).isEqualTo(16.551f)
        assertThat(firstCredit.voteAverage).isEqualTo(7.2f)
        assertThat(firstCredit.voteCount).isEqualTo(3372)
        val lastCredit = movieCredits.cast[2]
        assertThat(lastCredit.genreIds).containsExactly(35)
        assertThat(lastCredit.popularity).isEqualTo(9.715f)
        assertThat(lastCredit.voteAverage).isEqualTo(7.1f)
        assertThat(lastCredit.voteCount).isEqualTo(683)

        // Consumers still read and sort these properties as non-nullable numbers.
        val sortedByPopularity = movieCredits.cast.sortedByDescending { it.popularity }
        assertThat(sortedByPopularity.first().id).isEqualTo(101)
    }

    @Test
    fun `it can fetch movie credits when poster, backdrop, overview and original language are missing`() = runTest {
        val movieCredits = classToTest.getMovieCredits(
            personId = 21688,
            language = "en-US",
        )

        val sparseCredit = movieCredits.cast[0]
        assertThat(sparseCredit.id).isEqualTo(201)
        assertThat(sparseCredit.title).isEqualTo("No Media Movie")
        assertThat(sparseCredit.posterPath).isNull()
        assertThat(sparseCredit.backdropPath).isNull()
        assertThat(sparseCredit.overview).isEmpty()
        assertThat(sparseCredit.originalLanguage).isEmpty()
        assertThat(sparseCredit.genreIds).containsExactly(12, 14)
        assertThat(sparseCredit.voteCount).isEqualTo(42)
    }

    @Test
    fun `it can fetch tv credits when vote average, vote count, overview and original language are missing`() = runTest {
        val showCredits = classToTest.getShowCredits(
            personId = 21688,
            language = "en-US",
        )

        assertThat(showCredits.cast).hasSize(2)

        val fullCredit = showCredits.cast[0]
        assertThat(fullCredit.voteAverage).isEqualTo(6.8f)
        assertThat(fullCredit.voteCount).isEqualTo(76)
        assertThat(fullCredit.overview).isEqualTo("Overview of the full show credit entry.")
        assertThat(fullCredit.originalLanguage).isEqualTo("en")

        val sparseCredit = showCredits.cast[1]
        assertThat(sparseCredit.id).isEqualTo(302)
        assertThat(sparseCredit.name).isEqualTo("Sparse Show One")
        assertThat(sparseCredit.firstAirDate).isEqualTo(LocalDate.parse("2012-02-02"))
        assertThat(sparseCredit.voteAverage).isEqualTo(0f)
        assertThat(sparseCredit.voteCount).isEqualTo(0)
        assertThat(sparseCredit.overview).isEmpty()
        assertThat(sparseCredit.originalLanguage).isEmpty()
        assertThat(sparseCredit.genreIds).containsExactly(16)
        assertThat(sparseCredit.popularity).isEqualTo(17.24f)
    }
}
