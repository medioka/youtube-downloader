## About
An app that will fetch video data using Youtube API and let the user download the fetched video based on the audio format or video format. Created by using the help of [yt-dlp](https://github.com/yausername/youtubedl-android") library.

## How to implement 
You just need to change the API KEY in **`YoutubeApiService.kt`** file

## Features
- **Search videos:** Allows user to see paginated result videos based on their search query.
- **Favorite videos:**  User can save their favorite videos that will persist as long the application installed.
- **Download videos:** Many choices for users to download videos in audio and video formats.

# Tech-Stack
- **`Retrofit`**
- **`Room`**
- **`Coroutine`**
- **`Kotlin Flows`** for reactive programming
- **`Koin`** for dependency injection


## Things to improve
* UI/UX and animation
* Foreground services to notify download percentage
* Video player





