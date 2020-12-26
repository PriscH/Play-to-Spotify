package jp.spotifyimporter.controllers

import com.wrapper.spotify.SpotifyApi
import jp.spotifyimporter.services.MigrationService
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import kotlin.concurrent.schedule

@RestController
class SpotifyController(val spotifyApi: SpotifyApi, val migrationService: MigrationService) {

    @RequestMapping("/spotify")
    fun authorize(@RequestParam code: String): ResponseEntity<String> {
        // Fetch the authorization tokens
        val credentials = spotifyApi.authorizationCode(code)
            .build()
            .execute()

        spotifyApi.accessToken = credentials.accessToken
        spotifyApi.refreshToken = credentials.refreshToken

        // Perform the migration
        migrationService.migrate()

        return ResponseEntity.ok(
            "Your songs are being migrated!<br/>" +
                "Please monitor the application logs for progress and status."
        )
    }

}