package app.moviebase.tmdb.core

import app.moviebase.tmdb.model.TmdbMediaListItem
import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbPerson
import app.moviebase.tmdb.model.TmdbPersonCredit
import app.moviebase.tmdb.model.TmdbSearchableListItem
import app.moviebase.tmdb.model.TmdbShow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object JsonFactory {

    @OptIn(ExperimentalSerializationApi::class)
    fun buildJson(): Json = Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        allowSpecialFloatingPointValues = true
        prettyPrint = false

        val module = SerializersModule {
            polymorphic(TmdbMediaListItem::class, TmdbShow::class, TmdbShow.serializer())
            polymorphic(TmdbMediaListItem::class, TmdbMovie::class, TmdbMovie.serializer())

            polymorphic(TmdbSearchableListItem::class, TmdbShow::class, TmdbShow.serializer())
            polymorphic(TmdbSearchableListItem::class, TmdbMovie::class, TmdbMovie.serializer())
            polymorphic(TmdbSearchableListItem::class, TmdbPerson::class, TmdbPerson.serializer())

            polymorphic(TmdbPersonCredit::class, TmdbPersonCredit.Show::class, TmdbPersonCredit.Show.serializer())
            polymorphic(TmdbPersonCredit::class, TmdbPersonCredit.Movie::class, TmdbPersonCredit.Movie.serializer())
        }
        serializersModule = module
        classDiscriminator = "media_type"
    }
}
