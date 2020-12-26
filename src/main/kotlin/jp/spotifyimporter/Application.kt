package jp.spotifyimporter

import com.wrapper.spotify.SpotifyApi
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import java.awt.Desktop


@SpringBootApplication
class Application {

	private val log = LoggerFactory.getLogger(javaClass)

	@Bean
	fun runner(spotifyApi: SpotifyApi) = CommandLineRunner {
		val authorizationCodeUri  = spotifyApi.authorizationCodeUri()
			.scope("playlist-modify-public,playlist-modify-private,playlist-read-private,playlist-read-collaborative,user-library-modify,user-library-read")
			.build()
			.execute()

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(authorizationCodeUri)
		} else {
			log.error(
				"Unable to open the browser. Desktop is not supported.\n" +
						"You have to manually navigate to $authorizationCodeUri"
			)
		}
	}
}

fun main(args: Array<String>) {
	// runApplication<Application>(*args)

	val builder = SpringApplicationBuilder(Application::class.java)
	builder.headless(false)
	builder.run(*args)
}
