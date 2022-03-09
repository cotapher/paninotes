
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage


class SideNotebookPaneView(val model: Model, val stage: Stage): BorderPane(), IView {
    private enum class PaneView {
        NOTEBOOKS,
        NOTES
    }

    private var currentView: PaneView = PaneView.NOTEBOOKS
    private var currentNotebookId: Int = -1

    init {
        this.layoutView()
        this.border = Border(
            BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT
            )
        )
        this.id = "sideNotebookPane"
    }

    private fun layoutView() {
        val gridPane = GridPane() // Holds the buttons for the list of notebooks and notes

        // Depending on the current view, we will either show a list of notebooks, or list of notes in a notebook
        when (currentView) {
            PaneView.NOTEBOOKS -> {
                // Get a list of all the notebooks from the Model
                val notebooks: ArrayList<Notebook> = model.getAllNotebooks()

                for (i in notebooks.indices) {
                    val notebookButton = Button(notebooks[i].title)
                    notebookButton.id = "sideNotebookPane-notebook-button-$i"
                    notebookButton.setPrefSize(110.0, 16.0)

                    notebookButton.setOnAction {
                        showNotesForNotebook(notebooks[i].notebookId)
                        model.currentOpenNotebook = notebooks[i]
                    }

                    gridPane.add(notebookButton, 0, i)
                }

                // At the bottom, we want to add a button to add a notebook
                val plusImage = Image("plus_icon.png")
                val plusImageView = ImageView(plusImage)
                plusImageView.isPreserveRatio = true
                plusImageView.fitHeight = 17.0

                val addNotebookButton = Button("ADD NOTEBOOK", plusImageView)
                addNotebookButton.id = "sideNotebookPane-add-notebook-button"

                addNotebookButton.setOnAction {
                    // Open the dialog to create the notebook
                    model.createNotebookPopup()
                }

                this.bottom = addNotebookButton
            }

            PaneView.NOTES -> {
                if (currentNotebookId >= 0) {
                    val currentNotebook = model.getNotebookById(currentNotebookId)

                    if (currentNotebook != null) {
                        // Put a button with the notebook name at the top, so the user can go back to the list of notebooks
                        val backArrowImage = Image("baseline_arrow_back_black_24dp.png")
                        val backArrowImageView = ImageView(backArrowImage)
                        backArrowImageView.isPreserveRatio = true
                        backArrowImageView.fitHeight = 17.0

                        val currentNotebookButton = Button(currentNotebook.title, backArrowImageView)
                        currentNotebookButton.id = "sideNotebookPane-current-notebook-button"
                        currentNotebookButton.setPrefSize(110.0, 16.0)
                        gridPane.add(currentNotebookButton, 0, 0)

                        currentNotebookButton.setOnAction {
                            // Go back to the list of notebooks
                            showNotebooks()

                            // In the Model, set the currentOpenNotebook back to null
                            model.currentOpenNotebook = null;
                        }

                        for (i in currentNotebook.notes.indices) {
                            val noteButton = Button(currentNotebook.notes[i].fileName)
                            noteButton.id = "sideNotebookPane-note-button-$i"
                            noteButton.setPrefSize(110.0, 16.0)
                            noteButton.setOnAction {
                                model.openNote(currentNotebook.notes[i])
                            }
                            gridPane.add(noteButton, 0, i + 1)
                        }
                    }

                    // At the bottom, we want to add a button to add a note into this notebook
                    val plusImage = Image("plus_icon.png")
                    val plusImageView = ImageView(plusImage)
                    plusImageView.isPreserveRatio = true
                    plusImageView.fitHeight = 17.0

                    val addNoteButton = Button("ADD NOTE", plusImageView)
                    addNoteButton.id = "sideNotebookPane-add-note-button"

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

        gridPane.vgap = 4.0

        this.top = gridPane;
    }

    fun showNotebooks() {
        currentView = PaneView.NOTEBOOKS

        this.layoutView()
    }

    fun showNotesForNotebook(notebookId: Int) {
        currentView = PaneView.NOTES
        currentNotebookId = notebookId

        this.layoutView()
    }

    override fun update() {
       // TODO("Not yet implemented")
        this.layoutView()
    }
}