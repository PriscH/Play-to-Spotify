# Overview
Imports Google Play Music libraries and playlists into Spotify.

# How to use
1. Register a Spotify application at https://developer.spotify.com/dashboard/
    1. Retrieve the Client ID and Client Secret
    2. Configure src/main/resources/application.properties with those two values
2. Export your Google Play Music library and playlist at https://takeout.google.com/
3. Extract the Google Play Music folder to src/main/resources. Your folder structure should have:
    1. src/main/resources/Google Play Music/Playlists
    2. src/main/resources/Google Play Music/Tracks
4. Run the application (mvn install spring-boot:run)
5. Authorize your application through the browser (it should open automatically)
6. If your browser did not open automatically, have a look at the console output. There is a URL that you have to open manually.
7. Review the console output to determine when the migration completes. There is a file called songs.log that contains the names of any song that could not be found, or which isn't an exact match. You have to manually verify these.