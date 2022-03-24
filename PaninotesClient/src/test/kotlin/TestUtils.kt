import javafx.scene.input.KeyCode
import java.awt.event.KeyEvent

object TestUtils {
    fun getKeycodesFromString(str: String): Array<KeyCode> {
        val keyCodes: Array<KeyCode> = Array(str.length) { KeyCode.A }

        str.forEachIndexed { index, char ->
            val keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(char.code)
            keyCodes[index] = KeyCode.valueOf(KeyEvent.getKeyText(keyCode))
        }

        return keyCodes
    }
}