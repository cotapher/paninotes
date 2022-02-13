class Note {
    // The text will be stored as a list of characters, where each character has a CharacterStyle
    var characters = mutableListOf<TextCharacter>()

    fun addCharacter(char: Char) {
        characters.add(TextCharacter(character = char))
    }

    fun addCharacter(textChar: TextCharacter) {
        characters.add(textChar)
    }
}