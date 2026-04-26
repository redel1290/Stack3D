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

        // Вказуємо, які архітектури процесорів підтримуємо. 
        // Це змусить Gradle шукати відповідні .so файли.
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
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
    
    packaging {
        resources {
            excludes += "META-INF/robovm/ios/robovm.xml"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            
            // Якщо кілька бібліотек містять однаковий файл, беремо перший.
            // Це важливо для стабільної збірки libGDX.
            pickFirsts += "lib/armeabi-v7a/libgdx.so"
            pickFirsts += "lib/arm64-v8a/libgdx.so"
            pickFirsts += "lib/x86/libgdx.so"
            pickFirsts += "lib/x86_64/libgdx.so"
        }
        jniLibs {
            // Допомагає Android знайти бібліотеки всередині APK
            useLegacyPackaging = true
        }
    }

    sourceSets {
        getByName("main") {
            // Вказуємо, де шукати додаткові нативні ліби, якщо вони будуть
            jniLibs.setSrcDirs(listOf("libs"))
        }
    }
}

dependencies {
    // Зв'язок з основним кодом гри
    implementation(project(":core"))
    
    // Основні бібліотеки libGDX
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-backend-android:1.12.1")
    
    // НАТИВНІ БІБЛІОТЕКИ (.so файли)
    // Використовуємо runtimeOnly, щоб вони ГАРАНТОВАНО потрапили в папку lib/ всередині APK
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-armeabi-v7a")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-arm64-v8a")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86_64")
    
    // Підтримка Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}