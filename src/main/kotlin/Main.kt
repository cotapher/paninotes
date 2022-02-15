// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import org.pushingpixels.aurora.component.model.*
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
    // Set the current open note in the NotepadManager
    NotepadManager.currentOpenNote = Note()
    if (NotepadManager.currentOpenNote != null) {
        NotepadManager.currentOpenNote!!.name = "Test note"
    }

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

        var html: String = "Hello <b>World</b>. This <i><strike>text</strike>sentence</i> is form<b>att<u>ed</u></b> in simple html."
        EditNoteArea(html)
    }
}