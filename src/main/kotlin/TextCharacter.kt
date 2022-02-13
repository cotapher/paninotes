class TextCharacter(character: Char = Character.MIN_VALUE, style: CharacterStyle = CharacterStyle()) {
    var character: Char = Character.MIN_VALUE
    var style: CharacterStyle = CharacterStyle()

   init {
       this.character = character
       this.style = style
   }
}