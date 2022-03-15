import animatefx.animation.SlideInLeft
import animatefx.animation.SlideOutLeft
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.MDL2IconFont

class SideIconPaneView(val model: Model, val sideNotebookPaneView: SideNotebookPaneView): GridPane(), IView {

    private val notebookButton = Button()
    private val searchButton = Button()
    private val infoButton = Button()

    init {
        this.layoutView()
        this.add(notebookButton, 0, 0)
        this.add(searchButton, 0, 1)
        this.add(infoButton, 0, 2)
        
        // Default don't show the notebook pane
        sideNotebookPaneView.isVisible = false

        this.styleClass.add("front-pane")
        notebookButton.styleClass.add(JMetroStyleClass.LIGHT_BUTTONS)
        searchButton.styleClass.add(JMetroStyleClass.LIGHT_BUTTONS)
        infoButton.styleClass.add(JMetroStyleClass.LIGHT_BUTTONS)
    }

    private fun layoutView() {
        notebookButton.id = "sideIconPane-notebook-button"
        searchButton.id = "sideIconPane-search-button"
        infoButton.id = "sideIconPane-info-button"

        // Set up the images and buttons for the sidebar
        notebookButton.setPrefSize(20.0, 20.0)
        searchButton.setPrefSize(20.0, 20.0)
        infoButton.setPrefSize(20.0, 20.0)

        // JMetro MDL2 icons
        // https://docs.microsoft.com/en-us/windows/apps/design/style/segoe-ui-symbol-font
        val notebookIcon = MDL2IconFont("\uE700")
        val searchIcon = MDL2IconFont("\uE721")
        val infoIcon = MDL2IconFont("\uE946")

        notebookButton.graphic = notebookIcon
        searchButton.graphic = searchIcon
        infoButton.graphic = infoIcon

        this.vgap = 3.0
        this.padding = Insets(5.0)

        // Button Actions
        notebookButton.setOnAction {
            // Toggle the side notebook pane view
            if (sideNotebookPaneView.isVisible) {
                val anim = SlideOutLeft(sideNotebookPaneView)
                anim.setSpeed(2.5)
                anim.setOnFinished{sideNotebookPaneView.isVisible = false}
                anim.play()

            } else {
                sideNotebookPaneView.isVisible = true
                val anim = SlideInLeft(sideNotebookPaneView)
                anim.setSpeed(2.5)
                anim.play()
            }
        }

        searchButton.setOnAction {
        }

        infoButton.setOnAction {
            val popup = Alert(Alert.AlertType.INFORMATION)
            popup.title = "Note HTML Metadata Info"
            popup.dialogPane.content =  Label(model.currentNote?.fileMetadata.toString())
            popup.show()
        }
    }

    override fun update() {
        this.layoutView() //TODO don't want to refresh everything

        //add a condition to only show editor if there is file assigned to model.currentFile
        infoButton.isVisible = model.currentNote != null
    }
}