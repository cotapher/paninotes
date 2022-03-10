import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers

@ExtendWith(ApplicationExtension::class)
class TopMenuViewTest {

    @Start
    private fun start(stage: Stage) {
        val model = Model()
        model.initializeNotebooks()
        val htmlEditor = HTMLEditor()
        val layout = BorderPane()
        val jMetro = JMetro()
        val topMenuView = TopMenuView(model, htmlEditor, stage, jMetro)

        layout.top = topMenuView

        stage.scene = Scene(layout)
        stage.show()
    }

    @Test
    fun first_test(robot: FxRobot) {
        FxAssert.verifyThat("#menu-fileMenu", LabeledMatchers.hasText("File"))
    }
}