plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kover)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.gultekinahmetabdullah.trainvoc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gultekinahmetabdullah.trainvoc"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    // Signing configuration for release builds
    // Store credentials in environment variables or local.properties (not committed to git)
    signingConfigs {
        create("release") {
            // Option 1: Use environment variables (recommended for CI/CD)
            val keystorePath = System.getenv("TRAINVOC_KEYSTORE_PATH")
                ?: project.findProperty("TRAINVOC_KEYSTORE_PATH") as? String
            val keystorePassword = System.getenv("TRAINVOC_KEYSTORE_PASSWORD")
                ?: project.findProperty("TRAINVOC_KEYSTORE_PASSWORD") as? String
            val keyAlias = System.getenv("TRAINVOC_KEY_ALIAS")
                ?: project.findProperty("TRAINVOC_KEY_ALIAS") as? String
                ?: "trainvoc-upload"
            val keyPassword = System.getenv("TRAINVOC_KEY_PASSWORD")
                ?: project.findProperty("TRAINVOC_KEY_PASSWORD") as? String

            // Only configure signing if keystore is available
            if (keystorePath != null && keystorePassword != null && keyPassword != null) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // Print warning if signing is not configured
                println("⚠️ WARNING: Release signing not configured. Set environment variables:")
                println("   TRAINVOC_KEYSTORE_PATH, TRAINVOC_KEYSTORE_PASSWORD,")
                println("   TRAINVOC_KEY_ALIAS, TRAINVOC_KEY_PASSWORD")
            }
        }
    }

    androidResources {
        // Optimize resources: keep only required languages
        // Density filtering is handled by bundle.density.enableSplit = true
        localeFilters += listOf("en", "tr")
    }

    buildTypes {
        release {
            // Apply signing configuration if available
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Disable PNG crunching for faster builds
            // WebP is more efficient anyway
            isCrunchPngs = false
        }
        debug {
            isMinifyEnabled = false
            isCrunchPngs = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Enable Android App Bundle optimizations
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }

    lint {
        // Enable strict lint checking for code quality
        abortOnError = true
        // Generate reports for CI/CD
        htmlReport = true
        xmlReport = true
        // Check all builds
        checkReleaseBuilds = true
        // Disable checks that cause OOM
        checkDependencies = false

        // Disable specific checks that are not critical
        disable += setOf(
            "ObsoleteLintCustomCheck",
            "GradleDependency",
            "NewerVersionAvailable"
        )

        // Treat warnings as errors for stricter quality control
        warningsAsErrors = false  // Will enable after fixing all warnings
    }
}

dependencies {

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")

    // Room Database
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.androidx.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Other
    implementation(libs.lottie.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.gson)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Retrofit & OkHttp for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Security
    implementation(libs.androidx.security.crypto)

    // Google Play Games Services
    implementation(libs.google.play.games)

    // Google Play Billing
    implementation(libs.google.billing)

    // Google Drive & Auth
    implementation(libs.google.auth)
    implementation(libs.google.drive)
    implementation(libs.google.api.client.android)
    implementation(libs.google.http.client.gson)
    implementation(libs.kotlinx.coroutines.play.services)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.hilt.android.testing)
    testImplementation("androidx.work:work-testing:2.10.1")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
    kspTest(libs.hilt.compiler)

    // MockK for mocking
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")

    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.2.1")

    // Truth for better assertions
    testImplementation("com.google.truth:truth:1.1.5")

    // Hilt testing
    testImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspTest("com.google.dagger:hilt-compiler:2.48.1")

    // Android Test dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Kover configuration for code coverage
kover {
    reports {
        // Configure HTML report
        total {
            html {
                onCheck = true
            }
            xml {
                onCheck = true
            }
        }
    }
}