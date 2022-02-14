import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "me.innotevators"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation("org.pushing-pixels:aurora-theming:1.0.1")
    implementation("org.pushing-pixels:aurora-component:1.0.1")
    implementation("org.pushing-pixels:aurora-window:1.0.1")
    implementation("org.pushing-pixels:aurora-tools-svg-transcoder:1.0.1")
    implementation("org.pushing-pixels:aurora-tools-svg-transcoder-gradle-plugin:1.0.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Team203_Paninotes"
            packageVersion = "1.0.0"
        }
    }
}