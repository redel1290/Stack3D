plugins {
    kotlin("jvm")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
