package texthighlighterapi

import java.util.HashMap

/**
 * Created by akshay on 30/04/17.
 */
internal interface Language {
    fun getColor(myToken: String): String

    companion object {
        val colorMap = HashMap<String, String>()
        const val defaultColor = "black"
    }
}