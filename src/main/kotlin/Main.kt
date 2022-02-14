// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.model.CommandGroup
import org.pushingpixels.aurora.component.model.CommandMenuContentModel
import org.pushingpixels.aurora.theming.marinerSkin
import org.pushingpixels.aurora.window.AuroraWindow
import org.pushingpixels.aurora.window.auroraApplication


@Composable
@Preview
fun App() {
//    TopAppBar(
//        title = { Text(text = "Menu Bar") }
//    )
}

fun main() = auroraApplication {
    AuroraWindow(
        skin = marinerSkin(),
        title = "Aurora Demo",
        onCloseRequest = ::exitApplication,
        menuCommands = CommandGroup(
            commands = listOf(
                Command(
                    text = "File",
                    secondaryContentModel = CommandMenuContentModel(
                        CommandGroup(
                            commands = listOf(
                                Command(
                                    text = "New",
                                    action = { println("New file!") }),
                                Command(
                                    text = "Open",
                                    action = { println("Open file!") }),
                                Command(
                                    text = "Save",
                                    action = { println("Save file!") })
                            )
                        )
                    )
                ),
                Command(
                    text = "Edit",
                    action = { println("Edit activated!") })
            )
        )
    ) {
        App()
    }
}