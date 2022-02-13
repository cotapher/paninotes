// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import org.pushingpixels.aurora.component.model.*
import org.pushingpixels.aurora.component.projection.TextFieldValueProjection
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

@Composable
fun TextArea(note: Note) {
    // We need to build the annotated string, obtaining the styles for each character

    val textFieldValue by derivedStateOf {
        TextFieldValue(
            annotatedString = buildAnnotatedString {
                for (char in note.characters) {
                    withStyle(style = SpanStyle(
                        fontWeight = if (char.style.isBold!!) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (char.style.isItalic!!) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = when {
                            char.style.isUnderline!! -> TextDecoration.Underline
                            else -> null
                        },
                        color = Color(char.style.colour!!)
                    )) {
                        append(char.character)
                    }
                }
            }
        )
    }

    TextFieldValueProjection(
        contentModel = TextFieldValueContentModel(
            value = textFieldValue,
            readOnly = false,
            onValueChange = { }
        ),
        presentationModel = TextFieldPresentationModel(showBorder = false)
    ).project()
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
        var sampleNote: Note = Note()
        sampleNote.addCharacter(TextCharacter(character = 'h', CharacterStyle().setBold(true).setColour(0xFFFF0000)))
        sampleNote.addCharacter('e')
        sampleNote.addCharacter('l')
        sampleNote.addCharacter('l')
        sampleNote.addCharacter('o')
        TextArea(sampleNote)

    }
}