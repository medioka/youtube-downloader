import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.youtubedownloader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.youtubedownloader"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += setOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
        val property = Properties()
        property.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "YOUTUBE_API", "\"${property.getProperty("YOUTUBE_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = true
        }
    }

}

dependencies {
    testImplementation("junit:junit:4.13.2")
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.1"
    val pagingVersion = "3.2.1"
    val nav_version = "2.7.7"
    val fragment_version = "1.6.2"
    val coroutines_version = "1.8.0"
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.datastore:datastore-core:1.0.0")


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    //Youtube Downloader
    implementation("com.github.yausername.youtubedl-android:library:0.15.0")
    implementation("com.github.yausername.youtubedl-android:ffmpeg:0.15.0") // Optional
    implementation("com.github.yausername.youtubedl-android:aria2c:0.15.0") // Optional

    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")

    //KOIN
    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("io.insert-koin:koin-android:3.5.3")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.3")
    androidTestImplementation("io.insert-koin:koin-test-junit4:3.5.3")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    //GLIDE
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //SHARED PREF.
    implementation("androidx.preference:preference-ktx:1.2.1")
    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")


    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
    debugImplementation("androidx.fragment:fragment-testing:$fragment_version")
    androidTestImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}