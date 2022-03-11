import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.base.NodeMatchers.isVisible
import org.testfx.matcher.control.LabeledMatchers
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(ApplicationExtension::class)
class NoteTabsViewTest {
    private val delay: CountDownLatch = CountDownLatch(1)

    private val testNotebookDir = File(Paths.get("src/main/resources/UnitTestNotebooks").toUri())
    private val model = Model()
    private lateinit var noteTabsView: NoteTabsView

    @Start
    private fun start(stage: Stage) {
        // Set the model's notebook directory to our test directory
        // The test directory will have some fake notebooks and notes in there
        model.NOTEBOOK_DIR = testNotebookDir
        model.initializeNotebooks()

        noteTabsView = NoteTabsView(model, stage)
        val sideNotebookPane = SideNotebookPaneView(model, stage)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane)
        val layout = BorderPane()
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        layout.top = noteTabsView
        layout.left = sidePane

        model.addView(noteTabsView)
        model.addView(sideNotebookPane)
        model.addView(sideIconPane)

        stage.scene = Scene(layout)
        stage.show()
    }

    @Test
    fun getNoteFromNotebookAndNoteName() {
        // When we pass in "notebook1/note13", we should get note "note13" from notebook "notebook1"
        val notebookAndNoteName = "notebook1/note13"
        val note: Note? = noteTabsView.getNoteFromNotebookAndNoteName(notebookAndNoteName)

        Assertions.assertTrue(note != null && note.title == "note13" && note.notebook?.title == "notebook1")
    }

    @Test
    fun openingTabs(robot: FxRobot) {
        // We're going to click on notebook1, and then click on 3 of the notes
        // 2 tabs should open up for the 2 notes

        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish

        // Click on the first notebook button
        robot.clickOn("#sideNotebookPane-notebook-button-0")

        // Click on the 2 note buttons
        robot.clickOn("#sideNotebookPane-note-button-0")
        robot.clickOn("#sideNotebookPane-note-button-1")

        // Verify that 2 tabs are visible
        FxAssert.verifyThat("#noteTabs-tab-0", isVisible())
        FxAssert.verifyThat("#noteTabs-tab-1", isVisible())

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
    }

    @Test
    fun selectingNotesChangesSelectedTab(robot: FxRobot) {
        // We're going to open up some tabs, and then click the note buttons corresponding to those tabs
        // The selected tab should switch to the corresponding note button

        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish

        // Click on the first notebook button
        robot.clickOn("#sideNotebookPane-notebook-button-0")

        // Click on the 2 note buttons to open up the 2 tabs
        robot.clickOn("#sideNotebookPane-note-button-0")
        robot.clickOn("#sideNotebookPane-note-button-1")

        // Click the first note button again, and the first tab should be selected
        robot.clickOn("#sideNotebookPane-note-button-0")
        delay.await(300, TimeUnit.MILLISECONDS)
        Assertions.assertTrue(noteTabsView.selectionModel.selectedIndex == 0)

        // Close the notebook pane again
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
    }
}