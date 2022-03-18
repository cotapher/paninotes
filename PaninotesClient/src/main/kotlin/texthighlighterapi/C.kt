package texthighlighterapi

import java.util.Locale

/**
 * Created by akshay on 30/04/17.
 */
internal class C : Language {
    //define all token in lowercase, even though text to be highlighted is in uppercase
    val tokens = arrayOf(
        "auto",
        "double",
        "int",
        "struct",
        "const",
        "float",
        "short",
        "unsigned",
        "break",
        "else",
        "long",
        "switch",
        "continue",
        "for",
        "signed",
        "void",
        "case",
        "enum",
        "register",
        "typedef",
        "default",
        "goto",
        "sizeof",
        "volatile",
        "char",
        "extern",
        "return",
        "union",
        "do",
        "if",
        "static",
        "while"
    )
    val operators = arrayOf(
        "+", "-", "/", "*", "%", "=", "+=", "-=", "*=", "/=",
        "%=", "&=", "^=", "<", ">", "<=", ">=", "==", "!=", "&&", "||", "!", "&", "|", "~", "^", "<<",
        ">>", "?", ":", "Sizeof", "++", "--", "&lt;", "&lt;&lt;", "printf", "scanf"
    )
    val symbols = arrayOf("(", ")", "{", "}", "[", "]", "#include")
    val comments = arrayOf("/*", "*/", "//")

    init {
        for (token in tokens) {
            Language.colorMap[token] = "red"
        }
        for (operator in operators) {
            Language.colorMap[operator] = "#2C5A2E"
        }
        for (comment in comments) {
            Language.colorMap[comment] = "purple"
        }
        for (symbol in symbols) {
            Language.colorMap[symbol] = "blue"
        }
    }

    override fun getColor(myToken: String): String {
        var color = Language.colorMap[myToken.lowercase(Locale.getDefault())]
        if (color == null) {
            color = Language.defaultColor
        }
        return color
    }
}