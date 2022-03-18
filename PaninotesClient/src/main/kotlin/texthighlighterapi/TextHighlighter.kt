/*
 * Copyright 2016 AKSHAY NAIK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package texthighlighterapi

import texthighlighterapi.LibraryConstant.LanguageConstant
import texthighlighterapi.LibraryConstant.StyleConstant
import java.util.HashMap
import java.lang.StringBuilder
import java.util.StringTokenizer
import texthighlighterapi.C
import texthighlighterapi.Cpp
import texthighlighterapi.Java
import java.util.Locale

/**
 * Created by akshay on 14/08/16.
 */
class TextHighlighter : LanguageConstant, StyleConstant {
    private var defaultColor: String? = null
    private var language: String? = null
    private val colorMap = HashMap<String, String>()
    private val styleMap = HashMap<String, String>()
    fun setLanguage(language: String?) {
        this.language = language
    }

    fun getHighlightedText(stringToBeHighlighted: String?): String {
        var highlightedText = StringBuilder()
        var color: String? = ""
        var myToken = ""
        var selectedLanguage: Language? = null
        val tokenizer = StringTokenizer(stringToBeHighlighted)
        selectedLanguage = selectLanguage(selectedLanguage)
        if (selectedLanguage != null) {
            highlightedText = getHighlightedTextForLanguage(tokenizer, selectedLanguage)
        } else {
            while (tokenizer.hasMoreTokens()) {
                myToken = tokenizer.nextToken()
                color = getColor(myToken)
                highlightedText.append(colorTheToken(myToken, color) + " ")
            }
        }
        return String(highlightedText)
    }

    private fun selectLanguage(selectedLanguage: Language?): Language? {
        var selectedLanguage = selectedLanguage
        if (language !== "" && language != null) {
            when (language) {
                LanguageConstant.C -> selectedLanguage = C()
                LanguageConstant.CPP -> selectedLanguage = Cpp()
                LanguageConstant.JAVA -> selectedLanguage = Java()
            }
        }
        return selectedLanguage
    }

    private fun getHighlightedTextForLanguage(tokenizer: StringTokenizer, selectedLanguage: Language): StringBuilder {
        val highlightedText = StringBuilder()
        var color: String? = ""
        var myToken = ""
        while (tokenizer.hasMoreTokens()) {
            myToken = tokenizer.nextToken()
            color = selectedLanguage.getColor(myToken)
            highlightedText.append(colorTheToken(myToken, color) + " ")
        }
        return highlightedText
    }

    fun setDefaultColor(color: String?) {
        defaultColor = if (color === "" || color == null) {
            "black"
        } else {
            color
        }
    }

    fun getDefaultColor(): String? {
        return if (defaultColor === "" || defaultColor == null) {
            defaultColor
        } else {
            defaultColor
        }
    }

    private fun getColor(myToken: String): String? {
        var color = colorMap[myToken.lowercase(Locale.getDefault())]
        if (color == null) {
            color = defaultColor
        }
        return color
    }

    private fun colorTheToken(token: String, color: String?): String {
        return "<font color='$color'> $token </font>"
    }

    fun setColorForTheToken(token: String, color: String) {
        var token = token
        var color = color
        token = token.trim { it <= ' ' }
        color = color.trim { it <= ' ' }
        colorMap[token.lowercase(Locale.getDefault())] = color
    }

    fun setColorForTheToken(tokenArray: Array<String>, color: String) {
        for (token in tokenArray) {
            setColorForTheToken(token, color)
        }
    }

    fun getColorForTheToken(tokenArray: Array<String>): Array<String?> {
        val color = arrayOfNulls<String>(tokenArray.size)
        for (index in tokenArray.indices) {
            color[index] = getColorForTheToken(tokenArray[index])
        }
        return color
    }

    fun getColorForTheToken(token: String): String? {
        var token = token
        token = token.trim { it <= ' ' }
        var color: String? = ""
        color = colorMap[token.lowercase(Locale.getDefault())]
        return color ?: defaultColor
    }

    fun getStyledText(stringToBeStyled: String?): String {
        val styledText = StringBuilder()
        var style = ""
        var myToken = ""
        val tokenizer = StringTokenizer(stringToBeStyled)
        while (tokenizer.hasMoreTokens()) {
            myToken = tokenizer.nextToken()
            style = getStyle(myToken)
            styledText.append(styleTheToken(myToken, style) + " ")
        }
        return String(styledText)
    }

    fun getStyleForTheToken(tokenArray: Array<String>): Array<String?> {
        val style = arrayOfNulls<String>(tokenArray.size)
        for (index in tokenArray.indices) {
            style[index] = getStyleForTheToken(tokenArray[index])
        }
        return style
    }

    private fun getStyle(myToken: String): String {
        var style = styleMap[myToken.lowercase(Locale.getDefault())]
        if (style == null) {
            style = StyleConstant.NORMAL
        }
        return style
    }

    private fun styleTheToken(token: String, style: String): String {
        var taggedText = token
        taggedText = when (style) {
            StyleConstant.NORMAL -> token
            StyleConstant.BOLD -> "<b> $token </b>"
            StyleConstant.ITALIC -> "<i> $token </i>"
            StyleConstant.UNDERLINE -> "<u> $token </u>"
            StyleConstant.SUPERSCRIPT -> "<sup> $token </sup>"
            StyleConstant.SUBSCRIPT -> "<sub> $token </sub>"
            else -> token
        }
        return taggedText
    }

    fun setStyleForTheToken(token: String, style: String) {
        var token = token
        var style = style
        token = token.trim { it <= ' ' }
        style = style.trim { it <= ' ' }
        styleMap[token.lowercase(Locale.getDefault())] = style
    }

    fun setStyleForTheToken(tokenArray: Array<String>, style: String) {
        for (token in tokenArray) {
            setStyleForTheToken(token, style)
        }
    }

    fun getStyleForTheToken(token: String): String {
        var token = token
        token = token.trim { it <= ' ' }
        var style: String? = ""
        style = styleMap[token.lowercase(Locale.getDefault())]
        return style ?: StyleConstant.NORMAL
    }
}