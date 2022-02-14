import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Note {
    var name: String = ""

    // The text will be stored as a list of characters, where each character has a CharacterStyle
    var characters = mutableListOf<TextCharacter>()

    var rawText: String = ""

    fun addCharacter(char: Char) {
        characters.add(TextCharacter(character = char))
    }

    fun addCharacter(textChar: TextCharacter) {
        characters.add(textChar)
    }

    fun setText(text: String) {
        println(name + " setText: " + text);
        this.rawText = text
    }
}