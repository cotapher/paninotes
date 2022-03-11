import javafx.scene.Scene
import javafx.scene.input.KeyCode
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(ApplicationExtension::class)
class SideIconPaneViewTest {
    private val delay: CountDownLatch = CountDownLatch(1)

    @Start
    private fun start(stage: Stage) {
        val model = Model(stage)
        model.initializeNotebooks()

        val sideNotebookPane = SideNotebookPaneView(model, stage)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane, stage)
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
        FxAssert.verifyThat("#sideNotebookPane",  isInvisible())

        // After clicking on the notebook button, the notebook pane should now open
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
        FxAssert.verifyThat("#sideNotebookPane",  isVisible())

        // After clicking on the notebook button again, the notebook pane should close
        robot.clickOn("#sideIconPane-notebook-button")
        delay.await(1, TimeUnit.SECONDS) // wait for the animation to finish
        FxAssert.verifyThat("#sideNotebookPane",  isInvisible())
    }

    @Test
    fun openInfoDialog(robot: FxRobot) {
        // After clicking on the info button, an alert dialog should pop up
        robot.clickOn("#sideIconPane-info-button")
        FxAssert.verifyThat(".dialog-pane",  isVisible())

        // Press enter to close the info popup
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER)
    }
}