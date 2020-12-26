package jp.spotifyimporter.services

import jp.spotifyimporter.domain.Playlist
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MigrationService(val context: ConfigurableApplicationContext, val songReader: SongReader, val songFinder: SongFinder, val songStore: SongStore) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    fun migrate() {
        // Automatically shut down when done or if an Exception occurs
        context.use { context ->
            migrateLibrary()

            log.info("Reading playlists.")
            val playlists = songReader.findPlaylists()
            log.info("Read ${playlists.size} playlists.")

            // Sort playlists in descending order so that they appear alphabetically on Spotify
            playlists.sortedByDescending { it.title }
                .forEach(::migratePlaylist)
        }
    }

    private fun migrateLibrary() {
        log.info("Reading library songs.")
        val songs = songReader.readLibrary()
        log.info("Read ${songs.size} library songs.")

        log.info("Finding library songs.")
        val tracks = songFinder.find(songs, "Library")
        log.info("Found ${tracks.size} songs.")

        log.info("Storing library songs.")
        songStore.saveLibrary(tracks)
        log.info("Stored library songs.")
    }

    private fun migratePlaylist(playlist: Playlist) {
        log.info("Reading playlist '${playlist.title} songs.")
        val songs = songReader.readPlaylist(playlist)
        log.info("Read ${songs.size} playlist songs.")

        log.info("Finding playlist '${playlist.title} songs.")
        val tracks = songFinder.find(songs, playlist.title)
        log.info("Found ${tracks.size} songs.")

        log.info("Storing playlist '${playlist.title} songs.")
        songStore.savePlaylist(playlist, tracks)
        log.info("Stored playlist songs.")
    }
}