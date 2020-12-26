package jp.spotifyimporter.services

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import jp.spotifyimporter.domain.Playlist
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class SongStore(private val spotifyApi: SpotifyApi) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val apiLimit = 50

    fun saveLibrary(tracks: Set<Track>) {
        val trackIds = tracks.map { it.id }
        val chunkedTracks = trackIds.chunked(apiLimit)
        for (indexedTracksChunk in chunkedTracks.withIndex()) {
            log.info("Saving tracks chunk ${indexedTracksChunk.index + 1} / ${chunkedTracks.size}.")

            spotifyApi.saveTracksForUser(*indexedTracksChunk.value.toTypedArray())
                .build()
                .execute()
        }
    }

    fun savePlaylist(playlist: Playlist, tracks: Set<Track>) {
        val userId = spotifyApi.getCurrentUsersProfile()
            .build()
            .execute()
            .id

        val spotifyPlaylist = spotifyApi.createPlaylist(userId, playlist.title)
            .build()
            .execute()

        val trackUris = tracks.map { it.uri }
        val chunkedTracks = trackUris.chunked(apiLimit)
        for (indexedTracksChunk in chunkedTracks.withIndex()) {
            log.info("Saving tracks chunk ${indexedTracksChunk.index + 1} / ${chunkedTracks.size}.")

            spotifyApi.addItemsToPlaylist(spotifyPlaylist.id, indexedTracksChunk.value.toTypedArray())
                .build()
                .execute()
        }
    }

    fun removeLibrarySongs() {
        val total = spotifyApi.usersSavedTracks
            .build()
            .execute()
            .total

        var count = 0
        while (count < total) {
            log.info("Removing batch ${count / apiLimit + 1} of ${total / apiLimit + 1}")

            val trackIds = spotifyApi.usersSavedTracks
                .limit(apiLimit)
                .build()
                .execute()
                .items
                .map { it.track.id }

            if (trackIds.isNotEmpty()) {
                spotifyApi.removeUsersSavedTracks(*trackIds.toTypedArray())
                    .build()
                    .execute()
            }

            count += apiLimit
        }
    }

}