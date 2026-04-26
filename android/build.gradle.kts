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
    
    packaging {
        resources {
            excludes += "META-INF/robovm/ios/robovm.xml"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            
            pickFirsts += "lib/armeabi-v7a/libgdx.so"
            pickFirsts += "lib/arm64-v8a/libgdx.so"
            pickFirsts += "lib/x86/libgdx.so"
            pickFirsts += "lib/x86_64/libgdx.so"
        }
    }
}

dependencies {
    implementation(project(":core"))
    
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-backend-android:1.12.1")
    
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-x86_64")
    
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}
