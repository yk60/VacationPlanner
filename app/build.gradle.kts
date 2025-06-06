plugins {
     alias(libs.plugins.android.application)
     alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)

}

android {
    namespace = "edu.sjsu.android.vacationplanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.sjsu.android.vacationplanner"
        minSdk = 24
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }


}
secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"

    ignoreList.add("keyToIgnore")
    ignoreList.add("sdk.*")
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.mpandroidchart)
    implementation(libs.circleimageview)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.android.volley:volley:1.1.1")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation(libs.gridlayout)
    


    // rounded ImageView
    implementation (libs.roundedimageview)

}