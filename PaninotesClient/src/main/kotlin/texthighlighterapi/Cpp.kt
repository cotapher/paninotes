package texthighlighterapi

import java.util.Locale

/**
 * Created by akshay on 30/04/17.
 */
internal class Cpp : Language {
    //define all token in lowercase, even though text to be highlighted is in uppercase
    val operators = arrayOf(">", "&lt;", "&lt;&lt;", ">>", "+", "-", "++", "--", "==", "=")
    val symbols = arrayOf("#include", "//", "{", "}", "(", ")")
    val comments = arrayOf("/*", "*/")
    val tokens = arrayOf(
        "iostream.h", "asm", " auto", " break", " case", " catch", " char", " class", " const",
        " continue", " default", " delete", " do", " double", " else", " enum", " extern", " float",
        " for", " friend", " goto", " if", " inline", " int", " long", " new", " operator",
        " private", " protected", " public", " register", " return", " short",
        " signed", " sizeof", " static", " struct", " switch", " template", " this",
        " throw", " try", " typedef", " union", " unsigned", " virtual", " void", " volatile", " while", "cout", "cin"
    )

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