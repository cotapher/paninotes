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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(ApplicationExtension::class)
internal class ModelTest {
    private val delay: CountDownLatch = CountDownLatch(1)


    private val testNotebookDir = File(Paths.get("src/main/resources/NotebooksModelTest").toUri())
    private val testNotebookName1 = "notebooka"
    private val testNotebookName2 = "notebookb"
    private val testNoteName = "notea"
    private val model = Model()

    private fun clearTestNotebookDirectory() {
        if (testNotebookDir.exists()) {
            for (file in testNotebookDir.listFiles()) {
                file.deleteRecursively()
            }
        }
    }

    @Start
    private fun start(stage: Stage) {
        // Set the model's notebook directory to our test directory
        model.NOTEBOOK_DIR = testNotebookDir
        model.initializeNotebooks()

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

    @Test
    fun getNotebookByTitle(robot: FxRobot) {
        val notebookTitle = "notebookModelTest"

        // Add a fake notebook into the model
        model.notebooks.add(Notebook(notebookTitle))

        // See if model.getNotebookByTitle gets the correct notebook
        val notebook: Notebook? = model.getNotebookByTitle(notebookTitle)
        assertTrue(notebook != null && notebook.title == notebookTitle)
    }

    @Test
    fun createNotebookWithPopup(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish

        // Click the Add Notebook Button
        robot.clickOn("#sideNotebookPane-add-notebook-button")

        // Type in the notebook's name, then hitting Enter to exit the popup
        robot.type(*TestUtils.getKeycodesFromString(testNotebookName1))
        robot.type(KeyCode.ENTER)

        // Check that the notebook was created in the model and that it's the current notebook
        assertTrue(model.currentOpenNotebook != null && model.currentOpenNotebook?.title == testNotebookName1)

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")

        // Now clear the test notebook directory
        clearTestNotebookDirectory()
    }

    @Test
    fun createNoteWithPopup(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish

        // Click the Add Notebook Button
        robot.clickOn("#sideNotebookPane-add-notebook-button")

        // Type in the notebook's name, then hitting Enter to exit the popup
        robot.type(*TestUtils.getKeycodesFromString(testNotebookName2))
        robot.type(KeyCode.ENTER)

        // Hit the notebook button
        robot.clickOn("#sideNotebookPane-notebook-button-0")

        // Now, hit the Add Note button
        robot.clickOn("#sideNotebookPane-add-note-button")

        // Type in the note's name, then hitting Enter to exit the popup
        robot.type(*TestUtils.getKeycodesFromString(testNoteName))
        robot.type(KeyCode.ENTER)

        // Check that the notebook was created in the model and that it's the current notebook
        assertTrue(model.currentOpenNotebook != null && model.currentOpenNotebook?.title == testNotebookName2)

        // Check that the note was created in the model and that it's the current note
        assertTrue(model.currentNote != null && model.currentNote?.title == testNoteName)

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")

        // Now clear the test notebook directory
        clearTestNotebookDirectory()
    }
}
