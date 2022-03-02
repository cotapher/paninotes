import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
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
    }

    private fun layoutView() {
        notebookButton.id = "sideIconPane-notebook-button"
        searchButton.id = "sideIconPane-search-button"
        infoButton.id = "sideIconPane-info-button"

        // Set up the images and buttons for the sidebar
//        val notebookImage = Image("notebook_icon.png")
//        val notebookImageView = ImageView(notebookImage)
//        notebookImageView.isPreserveRatio = true
//        notebookImageView.fitHeight = 20.0
//
//        val searchImage = Image("search_icon.png")
//        val searchImageView = ImageView(searchImage)
//        searchImageView.isPreserveRatio = true
//        searchImageView.fitHeight = 20.0
//
//        val infoImage = Image("info_icon.png")
//        val infoImageView = ImageView(infoImage)
//        infoImageView.isPreserveRatio = true
//        infoImageView.fitHeight = 20.0

        notebookButton.setPrefSize(20.0, 20.0)
        searchButton.setPrefSize(20.0, 20.0)
        infoButton.setPrefSize(20.0, 20.0)

//        notebookButton.graphic = notebookImageView
//        searchButton.graphic = searchImageView
//        infoButton.graphic = infoImageView

        // JMetro MDL2 icons
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
            sideNotebookPaneView.isVisible = !sideNotebookPaneView.isVisible
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