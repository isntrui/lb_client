import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    kotlin {
        jvm("desktop") {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "18"
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class) wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting
        val commonMain by getting
        val wasmJsMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha08")
            implementation("io.ktor:ktor-client-json:3.0.1")
            implementation("io.ktor:ktor-client-serialization:3.0.1")
            implementation("io.ktor:ktor-client-auth:3.0.1")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.2.0")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc02")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc02")
            implementation("io.github.vinceglb:filekit-core:0.8.7")
            implementation("io.ktor:ktor-client-core:3.0.1")
            implementation("io.ktor:ktor-client-logging:3.0.1")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
        }


        desktopMain.dependencies {
            implementation("io.ktor:ktor-client-cio:3.0.1")
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.bouncycastle:bcprov-jdk15on:1.70")
        }

        wasmJsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.2")
        }
    }
}



compose.desktop {
    application {
        mainClass = "ru.isntrui.lb.client.MainKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb
            )
            packageName = "LB Tool"
            packageVersion = "1.0.0"
            includeAllModules = true
            macOS {
                bundleID =
                    "ru.isntrui.lb.client"
                iconFile.set(project.file("src/commonMain/composeResources/drawable/lbtool.icns"))
                copyright = "isntrui / Artem Akopian © 2024"
            }
            windows {
                menuGroup =
                    "LB Tool"
                shortcut = true
                perUserInstall = true
                iconFile.set(project.file("src/commonMain/composeResources/drawable/lbtoolwin.ico"))
            }
        }
    }
}