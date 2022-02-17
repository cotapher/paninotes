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
import org.testfx.matcher.base.NodeMatchers.*

@ExtendWith(ApplicationExtension::class)
class SideNotebookPaneViewTest {

    @Start
    private fun start(stage: Stage) {
        val model = Model()
        val sideNotebookPane = SideNotebookPaneView(model)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane)
        val layout = BorderPane()
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        layout.left = sidePane

        stage.scene = Scene(layout)
        stage.show()

        // Add some mock notebooks and notes
        val notebook1 = model.createNotebook("book1")
        notebook1.addNote(Note("note11"))
        notebook1.addNote(Note("note12"))
        val notebook2 = model.createNotebook("book1")
        notebook2.addNote(Note("note21"))
        notebook2.addNote(Note("note22"))
        notebook2.addNote(Note("note23"))
        model.addNotebook(notebook1)
        model.addNotebook(notebook2)
    }

    @Test
    fun notebookView(robot: FxRobot) {
        // We need to click the notebook button in the side icon pane for the notebook pane to pop up
        robot.clickOn("#sideIconPane-notebook-button")

        // Verify that in the default view, we show a list of all the notebooks
        FxAssert.verifyThat("#sideNotebookPane", isVisible())
       // FxAssert.verifyThat("#sideNotebookPane-notebook-button-0", isVisible())
        //FxAssert.verifyThat("#sideNotebookPane-notebook-button-1", isVisible())

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")
    }
}