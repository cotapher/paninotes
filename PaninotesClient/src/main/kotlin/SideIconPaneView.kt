import animatefx.animation.SlideInLeft
import animatefx.animation.SlideOutLeft
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetroStyleClass
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignM
import org.kordamp.ikonli.materialdesign2.MaterialDesignN

class SideIconPaneView(val model: Model, val sideNotebookPaneView: SideNotebookPaneView, val stage: Stage) : GridPane(),
    IView {

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
        notebookButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        searchButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
        infoButton.styleClass.addAll(JMetroStyleClass.LIGHT_BUTTONS, "icon-button")
    }

    private fun layoutView() {
        notebookButton.id = "sideIconPane-notebook-button"
        searchButton.id = "sideIconPane-search-button"
        infoButton.id = "sideIconPane-info-button"

        // Set up the images and buttons for the sidebar
        notebookButton.setPrefSize(30.0, 40.0)
        searchButton.setPrefSize(30.0, 40.0)
        infoButton.setPrefSize(30.0, 40.0)

        // Kordamp Material Design icons
        // https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
        val notebookIcon = FontIcon(MaterialDesignN.NOTEBOOK_OUTLINE)
        val searchIcon = FontIcon(MaterialDesignM.MAGNIFY)
        val infoIcon = FontIcon(MaterialDesignI.INFORMATION_OUTLINE)

        notebookIcon.iconSize = 24
        searchIcon.iconSize = 24
        infoIcon.iconSize = 24

        notebookButton.graphic = notebookIcon
        searchButton.graphic = searchIcon
        infoButton.graphic = infoIcon

        // Button Actions
        notebookButton.setOnAction {
            // Toggle the side notebook pane view
            if (sideNotebookPaneView.isVisible) {
                val anim = SlideOutLeft(sideNotebookPaneView)
                anim.setSpeed(2.5)
                anim.setOnFinished { sideNotebookPaneView.isVisible = false }
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
    }

    override fun update() {
        this.layoutView()
        //add a condition to only show editor if there is file assigned to model.currentFile
        infoButton.isVisible = model.currentNote != null
    }
}