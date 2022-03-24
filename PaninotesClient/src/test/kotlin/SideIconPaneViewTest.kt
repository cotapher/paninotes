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
import org.testfx.matcher.base.NodeMatchers.isInvisible
import org.testfx.matcher.base.NodeMatchers.isVisible
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(ApplicationExtension::class)
class SideIconPaneViewTest {
    private val delay: CountDownLatch = CountDownLatch(1)

    @Start
    private fun start(stage: Stage) {
        val model = Model(stage)
        val htmlEditor = CustomHTMLEditor()
        model.initializeNotebooks()

        val sideNotebookPane = SideNotebookPaneView(model, htmlEditor, stage)
        val sideIconPane = SideIconPaneView(model, htmlEditor, sideNotebookPane, stage)
        val layout = BorderPane()
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        layout.left = sidePane

        stage.scene = Scene(layout)
        stage.show()
    }

    @Test
    fun notebookButtonShows() {
        FxAssert.verifyThat("#sideIconPane-notebook-button", isVisible())
    }

    @Test
    fun searchButtonShows() {
        FxAssert.verifyThat("#sideIconPane-search-button", isVisible())
    }

    @Test
    fun infoButtonShows() {
        FxAssert.verifyThat("#sideIconPane-info-button", isVisible())
    }

    @Test
    fun openNotebookPane(robot: FxRobot) {
        // First, test that the notebook pane is closed on start
        FxAssert.verifyThat("#sideNotebookPane", isInvisible())

        // After clicking on the notebook button, the notebook pane should now open
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
        FxAssert.verifyThat("#sideNotebookPane", isVisible())

        // After clicking on the notebook button again, the notebook pane should close
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
        FxAssert.verifyThat("#sideNotebookPane", isInvisible())
    }
}