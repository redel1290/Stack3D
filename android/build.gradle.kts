plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.stack3d"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.stack3d"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    // Виправлено: оновлений синтаксис замість застарілого packagingOptions
    packaging {
        resources {
            excludes += "META-INF/robovm/ios/robovm.xml"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            
            // Додаємо, щоб уникнути конфлікту "multiple files"
            pickFirsts += "lib/armeabi-v7a/libgdx.so"
            pickFirsts += "lib/arm64-v8a/libgdx.so"
            pickFirsts += "lib/x86/libgdx.so"
            pickFirsts += "lib/x86_64/libgdx.so"
        }
    }
}

dependencies {
    implementation(project(":core"))
    
    // --- ОСНОВНІ БІБЛІОТЕКИ LIBGDX ---
    // Це ядро, якого не вистачало для компіляції
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-backend-android:1.12.1")
    
    // --- НАТИВНІ БІБЛІОТЕКИ (ДЛЯ ПРОЦЕСОРІВ) ---
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86_64")
    
    // Рекомендую також додати стандартну бібліотеку Kotlin, якщо використовуєш її
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}