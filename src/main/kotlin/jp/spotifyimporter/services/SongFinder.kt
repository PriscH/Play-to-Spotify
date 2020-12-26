package jp.spotifyimporter.services

import com.neovisionaries.i18n.CountryCode
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import jp.spotifyimporter.domain.Song
import org.apache.commons.text.similarity.LevenshteinDistance
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SongFinder(private val spotifyApi: SpotifyApi) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun find(songs: List<Song>, context: String): Set<Track> {
        log.warn("Finding songs for '$context'")
        val matchingTracks = mutableSetOf<Track>()

        for (indexedSong in songs.withIndex()) {
            // For some reason the Spotify API does not like apostrophes
            val escapedTitle = indexedSong.value.title.replace("'", "")
            val escapedArtist = indexedSong.value.artist.replace("'", "")

            val query = "track:\"$escapedTitle\" artist:\"$escapedArtist\""
            val tracks = spotifyApi.searchTracks(query)
                .market(CountryCode.ZA)
                .build()
                .execute()

            if (tracks.items.isEmpty()) {
                log.warn("COULD NOT FIND: ${indexedSong.value.artist} - ${indexedSong.value.title}")
            } else {
                val bestMatch = tracks.items.first()

                val levenshteinDistance =
                    LevenshteinDistance.getDefaultInstance().apply(indexedSong.value.title.toLowerCase(), bestMatch.name.toLowerCase())	+
                            LevenshteinDistance.getDefaultInstance().apply(indexedSong.value.artist.toLowerCase(), bestMatch.artists.first().name.toLowerCase())
                if (levenshteinDistance > 2) {
                    log.warn("POTENTIAL MISMATCH: ${indexedSong.value.artist} - ${indexedSong.value.title} = ${bestMatch.artists.first().name} - ${bestMatch.name}")
                }

                matchingTracks += bestMatch
            }

            if ((indexedSong.index + 1) % 10 == 0) {
                log.info("Completed searching ${indexedSong.index + 1} / ${songs.size} songs.")
            }
        }

        return matchingTracks
    }

}