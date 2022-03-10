
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage

class Main : Application() {

    override fun start(stage: Stage) {
        // create the root of the scene graph
        // BorderPane supports placing children in regions around the screen
        val model = Model()
        model.initializeNotebooks()

        // Initialize all widgets--------------------------------------------------------------------------------------------
        val layout = BorderPane()

        val htmlEditor = HTMLEditor()
        val topMenuView = TopMenuView(model, htmlEditor, stage)
        val noteTabsView = NoteTabsView(model, stage)
        val sideNotebookPane = SideNotebookPaneView(model, stage)
        val sideIconPane = SideIconPaneView(model, sideNotebookPane)

        // Hacky thing so when the notebook pane is not visible, it doesn't take up any empty space in the side pane
        sideNotebookPane.managedProperty().bind(sideNotebookPane.visibleProperty())

        model.addView(topMenuView)
        model.addView(noteTabsView)
        model.addView(sideNotebookPane)
        model.addView(sideIconPane)
        model.notifyViews()

        // build the scene graph
        val sidePane = HBox()
        sidePane.children.addAll(sideIconPane, sideNotebookPane)

        val topPane = VBox()
        topPane.children.addAll(topMenuView, noteTabsView)

        layout.top = topPane
        layout.center = htmlEditor
        layout.left = sidePane
        layout.padding = Insets(5.0)

        // create and show the scene
        val scene = Scene(layout)

        stage.width = 800.0
        stage.height = 500.0
        stage.scene = scene
        stage.isResizable = true
        stage.title = "Paninotes"

        stage.show()
    }
}