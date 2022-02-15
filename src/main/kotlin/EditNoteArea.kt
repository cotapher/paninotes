import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import org.pushingpixels.aurora.component.model.TextFieldPresentationModel
import org.pushingpixels.aurora.component.model.TextFieldValueContentModel
import org.pushingpixels.aurora.component.projection.TextFieldValueProjection
import kotlin.math.min

@Composable
fun EditNoteArea(startText: String) {
    var textState = remember { mutableStateOf(TextFieldValue(annotatedString = startText.parseHtml())) }


    // TODO: Add in styles for each character in the text
/*
    // We need to build the annotated string, obtaining the styles for each character
    val textFieldValue =
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
                    )
                    ) {
                        append(char.character)
                    }
                }
            }
        )
*/

    TextFieldValueProjection(
        contentModel = TextFieldValueContentModel(
            value = textState.value,
            readOnly = false,
            onValueChange = {
                // Update the NotepadManager's current note to actually save the text in there
                // Check if we added or deleted text
                NotepadManager.currentOpenNote?.setText(it.text)

                // Update this Composable's text state
                textState.value = it
            }
        ),
        presentationModel = TextFieldPresentationModel(showBorder = false)
    ).project()
}