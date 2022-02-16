import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane

class SideIconPane: GridPane() {
    init {
        this.layoutView()
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

        val notebookButton = Button()
        val searchButton = Button()

        notebookButton.setPrefSize(20.0, 20.0)
        searchButton.setPrefSize(20.0, 20.0)

        notebookButton.graphic = notebookImageView
        searchButton.graphic = searchImageView

        this.add(notebookButton, 0, 0)
        this.add(searchButton, 0, 1)

        this.vgap = 3.0
        this.padding = Insets(5.0)
    }
}