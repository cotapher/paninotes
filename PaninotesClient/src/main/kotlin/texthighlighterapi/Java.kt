package texthighlighterapi

import java.util.Locale

/**
 * Created by akshay on 30/04/17.
 */
internal class Java : Language {
    //define all token in lowercase, even though text to be highlighted is in uppercase
    val operators = arrayOf(
        "+", "-", "/", "*", "%", "=", "+=", "-=", "*=", "/=",
        "%=", "&=", "^=", "<", ">", "<=", ">=", "==", "!=", "&&", "||", "!", "&", "|", "~", "^", "<<",
        ">>", "?", ":", "<<=", ">>=", "++", "--", "&lt;", "&lt;&lt;", "println", "print", ">>>", "<<<"
    )
    val tokens = arrayOf(
        "abstract",
        "assert boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum ",
        "extends",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        " super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "try",
        "void",
        "volatile",
        "while"
    )
    val symbols = arrayOf("import", "//", "{", "}", "(", ")")
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