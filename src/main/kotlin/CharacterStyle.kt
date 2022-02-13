data class CharacterStyle(
    var colour: Long? = 0xFF000000,
    var fontSize: Int? = 10,
    var isBold: Boolean? = false,
    var isItalic: Boolean? = false,
    var isUnderline: Boolean? = false,
    var isStrikethrough: Boolean? = false) {

        fun setColour(colour: Long) = apply { this.colour = colour }
        fun setFontSize(fontSize: Int) = apply { this.fontSize = fontSize }
        fun setBold(bold: Boolean) = apply { this.isBold = bold }
        fun setUnderline(underline: Boolean) = apply { this.isUnderline = underline }
        fun setStrikethrough(strikethrough: Boolean) = apply { this.isStrikethrough = strikethrough }
}