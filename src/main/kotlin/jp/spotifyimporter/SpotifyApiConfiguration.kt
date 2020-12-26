package jp.spotifyimporter

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpotifyApiConfiguration {

    @Value("\${client.id}")
    private lateinit var clientId: String

    @Value("\${client.secret}")
    private lateinit var clientSecret: String

    @Bean
    fun spotifyApi(): SpotifyApi {
        val spotifyApi = SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8080/spotify"))
                .build()

        return spotifyApi
    }

}