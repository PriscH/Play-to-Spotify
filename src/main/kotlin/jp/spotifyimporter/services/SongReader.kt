package jp.spotifyimporter.services

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import jp.spotifyimporter.domain.Playlist
import jp.spotifyimporter.domain.Song
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

@Service
class SongReader {

    private val baseDir = "/src/main/resources/Google Play Music"
    private val libraryDir = "$baseDir/Tracks"
    private val playlistsDir = "$baseDir/Playlists"
    private val playlistTracksPostfix = "/Tracks"
    private val metadataFile = "Metadata.csv"

    private val bracketsRegex = "\\(.*\\)".toRegex()

    fun findPlaylists(): List<Playlist> {
        val projectAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val playlistsPath = Paths.get(projectAbsolutePath, playlistsDir)

        val playlistDirs = Files.list(playlistsPath)
            .filter { Files.isDirectory(it) }

        return playlistDirs.map { playlistDir ->
            val metadataFile = playlistDir.resolve(metadataFile)
            val playlistData = CsvReader().readAllWithHeader(metadataFile.toFile()).first()
            val title = bracketsRegex.replace(StringEscapeUtils.unescapeHtml4(playlistData["Title"]), "").trim();

            Playlist(title, playlistDir.fileName.toString())
        }.toList()
    }

    fun readLibrary(): List<Song> {
        val projectAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val libraryPath = Paths.get(projectAbsolutePath, libraryDir)

        return readSongs(libraryPath)
    }

    fun readPlaylist(playlist: Playlist): List<Song> {
        val projectAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val playlistPath = Paths.get(projectAbsolutePath, playlistsDir, playlist.dir, playlistTracksPostfix)

        return readSongs(playlistPath)
    }

    private fun readSongs(songsPath: Path): List<Song> {
        val songFiles = Files.list(songsPath)
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().endsWith(".csv") }

        return songFiles.map {
            val songData = CsvReader().readAllWithHeader(it.toFile()).first()
            val title = bracketsRegex.replace(StringEscapeUtils.unescapeHtml4(songData["Title"]), "").trim();
            val artist = StringEscapeUtils.unescapeHtml4(songData["Artist"]);
            val album = StringEscapeUtils.unescapeHtml4(songData["Album"]);

            Song(title, artist, album)
        }.toList()
    }

}