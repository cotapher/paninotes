import animatefx.animation.FadeInUp
import animatefx.animation.SlideOutLeft
import eu.iamgio.animated.AnimatedVBox
import eu.iamgio.animated.AnimationPair
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert
import jfxtras.styles.jmetro.FlatTextInputDialog
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignA
import org.kordamp.ikonli.materialdesign2.MaterialDesignP


class SideNotebookPaneView(val model: Model, val stage: Stage): BorderPane(), IView {
    private enum class PaneView {
        NOTEBOOKS,
        NOTES
    }

    private var currentView: PaneView = PaneView.NOTEBOOKS

    init {
        this.layoutView()
        this.id = "sideNotebookPane"

        this.styleClass.add("middle-pane")
    }

    private fun layoutView() {
        // Animated VBox holds the buttons for the list of notebooks and notes
        // TODO - refactor to add elements to vBox without resetting vBox to make the animations nice
        val vBox = AnimatedVBox(AnimationPair(FadeInUp(), SlideOutLeft()).setSpeed(3.0, 3.0))

        // Depending on the current view, we will either show a list of notebooks, or list of notes in a notebook
        when (currentView) {
            PaneView.NOTEBOOKS -> {
                // Get a list of all the notebooks from the Model
                val notebooks: MutableList<Notebook> = model.notebooks

                for (i in notebooks.indices) {
                    val notebookButton = Button(notebooks[i].title)
                    notebookButton.id = "sideNotebookPane-notebook-button-$i"
                    notebookButton.setPrefSize(135.0, 16.0)

                    // create a context menu with a delete option
                    val contextMenu = ContextMenu()
                    val deleteOption = MenuItem("Delete")
                    deleteOption.setOnAction {
                        // Create a popup to confirm that the user wants to delete this notebook
                        val confirmationAlert = FlatAlert(Alert.AlertType.CONFIRMATION)
                        confirmationAlert.initOwner(stage)
                        confirmationAlert.contentText = "WARNING: Deleting a notebook will delete all notes within the notebook.\n" +
                                "Are you sure you want to delete Notebook: ${notebooks[i].title}"
                        val result = confirmationAlert.showAndWait()

                        if (result.isPresent) {
                            // Delete the notebook and all the notes in the notebook
                            model.deleteNotebook(notebooks[i])
                        }
                    }

                    contextMenu.items.add(deleteOption)

                    notebookButton.contextMenu = contextMenu

                    notebookButton.setOnAction {
                        model.currentOpenNotebook = notebooks[i]
                        showNotes()
                    }

                   /* notebookButton.setOnMouseClicked {
                        val button: MouseButton = it.button
                        if (button == MouseButton.PRIMARY) {
                            println("notebook left click")
                            model.currentOpenNotebook = notebooks[i]
                            showNotes()
                        } else if (button == MouseButton.SECONDARY) {
                            println("notebook right click")
                        }
                    }*/

                    vBox.children.add(notebookButton)
                }

                // At the bottom, we want to add a button to add a notebook
                val plusIcon = FontIcon(MaterialDesignP.PLUS)
                plusIcon.iconSize = 16

                val addNotebookButton = Button("ADD NOTEBOOK", plusIcon)
                addNotebookButton.id = "sideNotebookPane-add-notebook-button"
                addNotebookButton.prefWidth = 135.0
                addNotebookButton.minHeight = 40.0
                addNotebookButton.prefHeight = 40.0
                addNotebookButton.styleClass.add("add-note-notebook-button")

                addNotebookButton.setOnAction {
                    // Open the dialog to create the notebook
                    createNotebookPopup()
                }

                this.bottom = addNotebookButton
            }

            PaneView.NOTES -> {
                if (model.currentOpenNotebook != null) {

                    // Put a button with the notebook name at the top, so the user can go back to the list of notebooks
                    val backArrowIcon = FontIcon(MaterialDesignA.ARROW_LEFT)
                    backArrowIcon.iconSize = 16

                    val currentNotebookButton = Button(model.currentOpenNotebook!!.title, backArrowIcon)
                    currentNotebookButton.id = "sideNotebookPane-current-notebook-button"
                    currentNotebookButton.setPrefSize(135.0, 16.0)
                    vBox.children.add(0, currentNotebookButton)

                    currentNotebookButton.setOnAction {
                        // Go back to the list of notebooks
                        showNotebooks()

                        // In the Model, set the currentOpenNotebook back to null
                        model.currentOpenNotebook = null
                    }

                    for (i in model.currentOpenNotebook!!.notes.indices) {
                        val noteButton = Button(model.currentOpenNotebook!!.notes[i].title)
                        noteButton.id = "sideNotebookPane-note-button-$i"
                        noteButton.setPrefSize(135.0, 16.0)
                        noteButton.setOnAction {
                            // Open the clicked note
                            model.openNote(model.currentOpenNotebook!!.notes[i])
                        }
                        vBox.children.add(noteButton)
                    }

                    // At the bottom, we want to add a button to add a note into this notebook
                    val plusIcon = FontIcon(MaterialDesignP.PLUS)
                    plusIcon.iconSize = 16

                    val addNoteButton = Button("ADD NOTE", plusIcon)
                    addNoteButton.id = "sideNotebookPane-add-note-button"
                    addNoteButton.prefWidth = 135.0
                    addNoteButton.minHeight = 40.0
                    addNoteButton.prefHeight = 40.0
                    addNoteButton.styleClass.add("add-note-notebook-button")

                    addNoteButton.setOnAction {
                        // create the note in the current open notebook
                        if (model.currentOpenNotebook != null) {
                            model.createNotePopup(model.currentOpenNotebook!!)
                        }
                    }

                    this.bottom = addNoteButton
                } else {
                    // If the model's current open notebook is null, then maybe it got deleted, so just go back to the notebooks view
                    showNotebooks()
                }
            }
        }

        vBox.spacing = 1.0

        val scrollPane = ScrollPane(vBox)
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true
        scrollPane.prefHeightProperty().bind(stage.heightProperty().multiply(0.8))

        this.top = scrollPane
    }

    fun showNotebooks() {
        currentView = PaneView.NOTEBOOKS

        this.layoutView()
    }

    fun showNotes() {
        currentView = PaneView.NOTES

        this.layoutView()
    }

    fun createNotebookPopup() {
        val popup = FlatTextInputDialog()
        popup.initOwner(stage)

        popup.headerText = "Create Notebook"
        popup.contentText = "Enter name for new notebook:"

        //show the popup
        val result = popup.showAndWait()
        if (result.isPresent) {
            val notebookNameResult = result.get()
            model.createNotebookWithName(notebookNameResult)
        }
    }

    override fun update() {
        this.layoutView()
    }
}