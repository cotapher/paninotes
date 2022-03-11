import animatefx.animation.FadeInUp
import animatefx.animation.SlideOutLeft
import eu.iamgio.animated.AnimatedVBox
import eu.iamgio.animated.AnimationPair
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage


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
                val notebooks: ArrayList<Notebook> = model.getAllNotebooks()

                for (i in notebooks.indices) {
                    val notebookButton = Button(notebooks[i].title)
                    notebookButton.id = "sideNotebookPane-notebook-button-$i"
                    notebookButton.setPrefSize(135.0, 16.0)

                    notebookButton.setOnAction {
                        model.currentOpenNotebook = notebooks[i]
                        showNotes()
                    }

                    vBox.children.add(notebookButton)
                }

                // At the bottom, we want to add a button to add a notebook
                val plusImage = Image("plus_icon.png")
                val plusImageView = ImageView(plusImage)
                plusImageView.isPreserveRatio = true
                plusImageView.fitHeight = 17.0

                val addNotebookButton = Button("ADD NOTEBOOK", plusImageView)
                addNotebookButton.id = "sideNotebookPane-add-notebook-button"
                addNotebookButton.prefWidth = 135.0

                addNotebookButton.setOnAction {
                    // Open the dialog to create the notebook
                    model.createNotebookPopup()
                }

                this.bottom = addNotebookButton
            }

            PaneView.NOTES -> {
                if (model.currentOpenNotebook != null) {
                    // Put a button with the notebook name at the top, so the user can go back to the list of notebooks
                    val backArrowImage = Image("baseline_arrow_back_black_24dp.png")
                    val backArrowImageView = ImageView(backArrowImage)
                    backArrowImageView.isPreserveRatio = true
                    backArrowImageView.fitHeight = 17.0

                    val currentNotebookButton = Button(model.currentOpenNotebook!!.title, backArrowImageView)
                    currentNotebookButton.id = "sideNotebookPane-current-notebook-button"
                    currentNotebookButton.setPrefSize(135.0, 16.0)
                    vBox.children.add(0, currentNotebookButton)

                    currentNotebookButton.setOnAction {
                        // Go back to the list of notebooks
                        showNotebooks()

                        // In the Model, set the currentOpenNotebook back to null
                        model.currentOpenNotebook = null;
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
                    val plusImage = Image("plus_icon.png")
                    val plusImageView = ImageView(plusImage)
                    plusImageView.isPreserveRatio = true
                    plusImageView.fitHeight = 17.0

                    val addNoteButton = Button("ADD NOTE", plusImageView)
                    addNoteButton.id = "sideNotebookPane-add-note-button"
                    addNoteButton.prefWidth = 135.0

                    addNoteButton.setOnAction {
                        // create the note in the current open notebook
                        if (model.currentOpenNotebook != null) {
                            model.createNotePopup(model.currentOpenNotebook!!)
                        }
                    }

                    this.bottom = addNoteButton
                } else {
                    println("ERROR - We're in the side pane Notes paneview, but the current notebook Id is < 0")
                }
            }
        }

        vBox.spacing = 1.0

        this.top = vBox
    }

    fun showNotebooks() {
        currentView = PaneView.NOTEBOOKS

        this.layoutView()
    }

    fun showNotes() {
        currentView = PaneView.NOTES

        this.layoutView()
    }

    override fun update() {
        this.layoutView()
    }
}