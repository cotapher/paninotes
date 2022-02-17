import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

class SideIconPaneView(val model: Model, val sideNotebookPaneView: SideNotebookPaneView): GridPane(), IView {

    init {
        this.layoutView()

        // Default don't show the notebook pane
        sideNotebookPaneView.isVisible = false
    }

    private fun layoutView() {
        // Set up the images and buttons for the sidebar
        val notebookImage = Image("notebook_icon.png")
        val notebookImageView = ImageView(notebookImage)
        notebookImageView.isPreserveRatio = true
        notebookImageView.fitHeight = 20.0

        val searchImage = Image("search_icon.png")
        val searchImageView = ImageView(searchImage)
        searchImageView.isPreserveRatio = true
        searchImageView.fitHeight = 20.0

        val infoImage = Image("info_icon.png")
        val infoImageView = ImageView(infoImage)
        infoImageView.isPreserveRatio = true
        infoImageView.fitHeight = 20.0

        val notebookButton = Button()
        val searchButton = Button()
        val infoButton = Button()

        notebookButton.setPrefSize(20.0, 20.0)
        searchButton.setPrefSize(20.0, 20.0)
        infoButton.setPrefSize(20.0, 20.0)

        notebookButton.graphic = notebookImageView
        searchButton.graphic = searchImageView
        infoButton.graphic = infoImageView

        this.add(notebookButton, 0, 0)
        this.add(searchButton, 0, 1)
        this.add(infoButton, 0, 2)

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
            popup.contentText = model.currentFileMetadata.toString()
            popup.show()
        }
    }

    override fun update() {
        // TODO("Not yet implemented")
        this.layoutView()
    }
}