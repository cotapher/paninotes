
import fr.brouillard.oss.cssfx.CSSFX
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.Style

class Main : Application() {

    private val LIGHT_STYLESHEET_URL = Main::class.java.getResource("css/light.css")?.toExternalForm()

    override fun start(stage: Stage) {
        // activate css live update
        CSSFX.start()

        val jMetro = JMetro(Style.LIGHT)

        // create the root of the scene graph
        // BorderPane supports placing children in regions around the screen
        val model = Model()

        // Initialize all widgets--------------------------------------------------------------------------------------------
        val layout = BorderPane()

        val htmlEditor = HTMLEditor()
        val topMenuView = TopMenuView(model, htmlEditor, stage, jMetro)
        val sideNotebookPane = SideNotebookPaneView(model, stage)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane)
        // Hacky thing so when the notebook pane is not visible, it doesn't take up any empty space in the side pane
        sideNotebookPane.managedProperty().bind(sideNotebookPane.visibleProperty())


        model.addView(topMenuView)
        model.addView(sideNotebookPane)
        model.addView(sideIconPane)
        model.notifyViews()
        // build the scene graph
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        layout.top = topMenuView
        layout.center = htmlEditor
        layout.left = sidePane

        // create and show the scene
        val scene = Scene(layout)

        // apply jmetro
        jMetro.scene = scene
        jMetro.scene.stylesheets.add(LIGHT_STYLESHEET_URL)
        layout.styleClass.add(JMetroStyleClass.BACKGROUND)

        stage.width = 800.0
        stage.height = 500.0
        stage.scene = scene
        stage.isResizable = true
        stage.title = "Paninotes"

        stage.show()
    }
}