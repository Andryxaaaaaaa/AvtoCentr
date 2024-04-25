plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")

}
android {
    namespace = "com.example.avtocentr"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.avtocentr"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        exclude ("META-INF/NOTICE.md")
        exclude ("META-INF/LICENSE.md")
        // Другие исключения, если необходимо
    }
    lintOptions {
        lintOptions {
            ("warningsAsErrors true")
            // Добавьте следующую строку, чтобы активировать предупреждения о устаревших методах
            warning ("deprecation")
        }
    }

}
dependencies {
    implementation (libs.google.maps.services)
    implementation(libs.volley)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation ("com.sun.mail","android-mail","1.6.7")
    implementation ("com.sun.mail","android-activation","1.6.7")
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}