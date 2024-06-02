## About
An app that will fetch video data using Youtube API and let the user download the fetched video based on the audio format or video format. Created by using the help of [yt-dlp](https://github.com/yausername/youtubedl-android") library.

## How to implement 
You just need to change the API KEY in **`YoutubeApiService.kt`** file

## Features
- **Search videos:** Allows user to see paginated result videos based on their search query.
- **Favorite videos:**  User can save their favorite videos that will persist as long the application installed.
- **Download videos:** Many choices for users to download videos in audio and video formats.
- **Customized settings:** User can change the settings based on their preferences.

# Tech-Stack
- **`Retrofit`**
- **`Room`**
- **`Coroutine`**
- **`Kotlin Flows`** for reactive programming
- **`Koin`** for dependency injection
- **`Shared Preferences`** for preferences

## Screenshots
<p float="left">
  <img src="/screenshot/home.png" width="100" />
  <img src="/screenshot/download_config.png" width="100" />
  <img src="/screenshot/downloading.png" width="100" />
  <img src="/screenshot/finished.png" width="100" />
  <img src="/screenshot/settings.png" width="100" />
</p>


## Things to improve
* UI/UX and animation
* Foreground services to notify download percentage
* Video player





