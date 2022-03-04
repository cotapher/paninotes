
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.base.NodeMatchers.isVisible
import org.testfx.matcher.control.LabeledMatchers
import java.io.File

@ExtendWith(ApplicationExtension::class)
class SideNotebookPaneViewTest {

    @Start
    private fun start(stage: Stage) {
        val model = Model()
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

        // Add some mock notebooks and notes
        val notebook1 = model.createNotebook("book1")
        notebook1.addNote(Note(File("note11")))
        notebook1.addNote(Note(File("note12")))
        val notebook2 = model.createNotebook("book1")
        notebook2.addNote(Note(File("note21")))
        notebook2.addNote(Note(File("note22")))
        notebook2.addNote(Note(File("note23")))
        model.addNotebook(notebook1)
        model.addNotebook(notebook2)
    }

    @Test
    fun notebookView(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")

        // Verify that in the default view, we show a list of all the notebooks
        FxAssert.verifyThat("#sideNotebookPane", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-notebook-button-0", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-notebook-button-1", isVisible())

        // Verify that the "ADD NOTEBOOK" button is there
        FxAssert.verifyThat("#sideNotebookPane-add-notebook-button", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-add-notebook-button", LabeledMatchers.hasText("ADD NOTEBOOK"))

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")
    }

    @Test
    fun notesView(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")

        // Click on the first notebook button
        FxAssert.verifyThat("#sideNotebookPane-notebook-button-0", isVisible())
        robot.clickOn("#sideNotebookPane-notebook-button-0")

        // Now, verify that all the notes buttons are there
        FxAssert.verifyThat("#sideNotebookPane-note-button-0", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-note-button-1", isVisible())

        // Verify that the "ADD NOTE" button is there
        FxAssert.verifyThat("#sideNotebookPane-add-note-button", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-add-note-button", LabeledMatchers.hasText("ADD NOTE"))

        // Verify that there's a current notebook button to take you back to the notebook list
        FxAssert.verifyThat("#sideNotebookPane-current-notebook-button", isVisible())

        // Click on the current notebook button and verify that you see the notebooks again
        robot.clickOn("#sideNotebookPane-current-notebook-button")
        FxAssert.verifyThat("#sideNotebookPane-notebook-button-0", isVisible())
        FxAssert.verifyThat("#sideNotebookPane-notebook-button-1", isVisible())

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")
    }
}