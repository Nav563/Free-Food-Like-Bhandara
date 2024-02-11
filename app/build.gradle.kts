import javax.xml.transform.OutputKeys.VERSION

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    //id("com.google.secrets_gradle_plugin:0.0.6")
}

android {
    namespace = "com.example.freefood_likebhandara"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.freefood_likebhandara"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-crashlytics:18.6.1")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.0")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    implementation ("com.firebaseui:firebase-ui-firestore:7.1.1")


    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    //implementation("com.google.android.gms:play-services:12.0.1")
    //implementation ("androidx.support:appcompat-v7:28.0.0")
    //implementation("com.androidx.support:support-core-ui:28.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Smooth Bottom Bar
    implementation ("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    // Room Database
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    //Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Circle ImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Glide Image
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    // Map View
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.1.0")
    implementation ("com.google.android.libraries.places:places:3.3.0")
    // Camera
    implementation ("androidx.camera:camera-core:1.3.1")
    implementation ("androidx.camera:camera-camera2:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-view:1.3.1")
    //pretytime
    implementation ("org.ocpsoft.prettytime:prettytime:5.0.1.Final")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")



//    implementation("com.google.dagger:dagger:2.40.5")
//    kapt("com.google.dagger:dagger-compiler:2.40.5")
//    annotationProcessor("com.google.dagger:dagger-compiler:2.40.5")
//    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")



}