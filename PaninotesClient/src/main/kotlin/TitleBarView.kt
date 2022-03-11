import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.MDL2IconFont

class TitleBarView(val scene: BorderlessScene, val stage: Stage): BorderPane() {

    private val titleLabel = Label("Paninotes")

    private val minimizeButton = Button()
    private val maximizeButton = Button()
    private val closeButton = Button()

    init {
        scene.setMoveControl(this)

        val minimizeIcon = MDL2IconFont("\uE921")
        val maximizeIcon = MDL2IconFont("\uE922")
        val restoreIcon = MDL2IconFont("\uE923")
        val closeIcon = MDL2IconFont("\uE8BB")

        minimizeButton.graphic = minimizeIcon
        maximizeButton.graphic = maximizeIcon
        closeButton.graphic = closeIcon

        minimizeButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        maximizeButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        closeButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")

        minimizeButton.setOnAction { scene.minimizeStage() }
        maximizeButton.setOnAction {
            scene.maximizeStage()
            if (scene.isMaximized) maximizeButton.graphic = restoreIcon
            else maximizeButton.graphic = maximizeIcon
        }
        closeButton.setOnAction { stage.close() }

        val buttons = HBox(minimizeButton, maximizeButton, closeButton)

        this.center = titleLabel
        this.right = buttons

        this.styleClass.add("front-pane")
    }
}