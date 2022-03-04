import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.awt.event.KeyEvent
import java.io.File
import java.nio.file.Paths

@ExtendWith(ApplicationExtension::class)
internal class ModelTest {
    val testNotebookDir = File(Paths.get("src/main/resources/NotebooksModelTest").toUri())

    val testNotebookName = "notebooka"

    val model = Model()

    val mockHtmlFileContents = "<html dir=\"ltr\"><head>\n" +
            "    <meta name=\"title\" content=\"THIS IS A HIDDEN TITLE\">\n" +
            "</head><body contenteditable=\"true\">Hello <a href=\"https://github.com/TestFX/TestFX\">world</a></body></html>"

    private fun clearTestNotebookDirectory() {
        for (file in testNotebookDir.listFiles()) {
            file.delete()
        }
    }


    @Start
    private fun start(stage: Stage) {
        // Set the model's notebook directory to our test directory
        model.NOTEBOOK_DIR = testNotebookDir

        // Clear the test notebook directory before each test
        clearTestNotebookDirectory()

        val sideNotebookPane = SideNotebookPaneView(model, stage)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane)
        val layout = BorderPane()
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        layout.left = sidePane

        model.addView(sideNotebookPane)
        model.addView(sideIconPane)

        stage.scene = Scene(layout)
        stage.show()
    }


    private fun getKeycodesFromString(str: String): Array<KeyCode> {
        val keyCodes: Array<KeyCode> = Array(str.length) {KeyCode.A}

        str.forEachIndexed { index, char ->
            var keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(char.code)
            keyCodes[index] = KeyCode.valueOf(KeyEvent.getKeyText(keyCode))
        }

        return keyCodes
    }

    @Test
    fun setCurrentOpenFolderTest() {
        //Changes the current open folder
        val dir = model.NOTEBOOK_DIR
        model.currentNotebook = dir
        assertEquals(this.model.currentNotebook, dir)
    }

    @Test
    fun createNotebookWithPopup(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")

        // Click the Add Notebook Button
        robot.clickOn("#sideNotebookPane-add-notebook-button")

        // Typing in the notebook's name, then hitting Enter to exit the popup
        robot.type(*getKeycodesFromString(testNotebookName))
        robot.type(KeyCode.ENTER)

        // Check that the notebook was created in the model and that it's the current notebook
        assertTrue(model.currentOpenNotebook != null && model.currentOpenNotebook?.title == testNotebookName)

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")

        // Now clear the test notebook directory
        clearTestNotebookDirectory()
    }



}