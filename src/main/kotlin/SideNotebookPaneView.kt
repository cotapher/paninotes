import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

class SideNotebookPaneView(val model: Model): GridPane(), IView {
    private enum class PaneView {
        NOTEBOOKS,
        NOTES
    }

    private var currentView: PaneView = PaneView.NOTEBOOKS
    private var currentNotebookId: Int = -1

    init {
        this.layoutView()
    }

    private fun layoutView() {
        this.children.clear()

        // Depending on the current view, we will either show a list of notebooks, or list of notes in a notebook
        when (currentView) {
            PaneView.NOTEBOOKS -> {
                // Get a list of all the notebooks from the Model
                val notebooks: ArrayList<Notebook> = model.getAllNotebooks()

                for (i in notebooks.indices) {
                    val notebookButton = Button(notebooks[i].title)
                    notebookButton.setPrefSize(110.0, 16.0)

                    notebookButton.setOnAction {
                        showNotesForNotebook(notebooks[i].notebookId)
                    }

                    this.add(notebookButton, 0, i)
                }
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
                        currentNotebookButton.setPrefSize(110.0, 16.0)
                        this.add(currentNotebookButton, 0, 0)

                        currentNotebookButton.setOnAction {
                            // Go back to the list of notebooks
                            showNotebooks()
                        }

                        for (i in currentNotebook.notes.indices) {
                            val noteButton = Button(currentNotebook.notes[i].title)
                            noteButton.setPrefSize(110.0, 16.0)
                            this.add(noteButton, 0, i + 1)
                        }
                    }
                } else {
                    println("ERROR - We're in the side pane Notes paneview, but the current notebook Id is < 0")
                }
            }
        }

        this.vgap = 4.0
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