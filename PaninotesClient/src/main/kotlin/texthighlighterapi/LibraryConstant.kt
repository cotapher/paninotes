package texthighlighterapi

/**
 * Created by akshay on 29/04/17.
 */
interface LibraryConstant {
    interface LanguageConstant {
        companion object {
            const val JAVA = "JAVA"
            const val C = "C"
            const val CPP = "C++"
        }
    }

    interface StyleConstant {
        companion object {
            //pre-defined Styles
            const val NORMAL = "NORMAL"
            const val BOLD = "BOLD"
            const val ITALIC = "ITALIC"
            const val UNDERLINE = "UNDERLINE"
            const val SUPERSCRIPT = "SUPERSCRIPT"
            const val SUBSCRIPT = "SUBSCRIPT"
        }
    }
}