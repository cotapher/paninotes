import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
    id("org.openjfx.javafxplugin") version "0.0.11"
}

javafx {
    version = "11"
    modules("javafx.controls","javafx.swing","javafx.web")
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

    //Jetpack Compose
    implementation("androidx.compose:compose-compiler:0.1.0-dev09")
    implementation("androidx.compose:compose-runtime:0.1.0-dev09")
    implementation("androidx.ui:ui-layout:0.1.0-dev09")
    implementation("androidx.ui:ui-material:0.1.0-dev09")
    implementation("androidx.ui:ui-tooling:0.1.0-dev09")
    implementation("androidx.ui:ui-framework:0.1.0-dev09")

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