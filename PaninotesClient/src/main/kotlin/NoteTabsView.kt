import backupState.BackupState
import javafx.event.Event
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignC
import org.kordamp.ikonli.materialdesign2.MaterialDesignS

class NoteTabsView(val model: Model, val htmlEditor: CustomHTMLEditor, val stage: Stage) : TabPane(), IView {
    init {
        this.layoutView()
        this.id = "noteTabs"

        // Hacky thing so when the TabPane is not visible, it doesn't take up any empty space
        this.managedProperty().bind(this.visibleProperty())
    }

    fun getNotebookAndNoteName(note: Note): String {
        return note.notebook!!.title + "/" + note.title!!
    }

    fun getNoteFromNotebookAndNoteName(notebookAndNoteName: String): Note? {
        val notebookAndNoteSplit: List<String> = notebookAndNoteName.split("/")
        val notebookTitle: String = notebookAndNoteSplit[0]
        val noteTitle: String = notebookAndNoteSplit[1]

        val notebook: Notebook? = model.getNotebookByTitle(notebookTitle)
        if (notebook != null) {
            return notebook.getNoteByTitle(noteTitle)
        }

        return null
    }

    private fun layoutView() {
        // If there are no open notes, then hide the TabPane
        this.isVisible = model.openNotes.size > 0

        model.openNotes.forEachIndexed { index, note ->
            // Check if we already have a tab for this open note
            // This string will be unique for each tab, since every notebook must have a different name
            val notebookAndNoteName = getNotebookAndNoteName(note)

            if (this.tabs.none { it.text == notebookAndNoteName }) {
                val tab = Tab(notebookAndNoteName)
                tab.id = "noteTabs-tab-$index"

                when (model.currentNote!!.backupState) {
                    BackupState.NOT_BACKED_UP -> tab.graphic = FontIcon(MaterialDesignC.CLOUD_OFF_OUTLINE)
                    BackupState.OUT_OF_SYNC -> tab.graphic = FontIcon(MaterialDesignS.SYNC)
                    BackupState.BACKED_UP -> tab.graphic = FontIcon(MaterialDesignC.CLOUD_CHECK)
                }

                tab.setOnSelectionChanged {
                    if (tab.isSelected) {
                        // save the current note
                        model.currentNote!!.saveNote(htmlEditor.htmlText)

                        // find the note across notebooks
                        val selectedNote: Note? = getNoteFromNotebookAndNoteName(tab.text)

                        //set current notebook
                        if (selectedNote != null) {
                            model.currentNote = selectedNote
                            model.openNote(model.currentNote)
                        }

                        model.notifyViews()
                    }
                }

                tab.setOnCloseRequest { evt: Event ->
                    if (StageUtils.confirmClose(model, stage, htmlEditor)) {
                        // Remove the note from the open notes in the model
                        val closedNote: Note? = getNoteFromNotebookAndNoteName(tab.text)
                        model.closeNote(closedNote)

                        // For some reason, tab.setOnSelectionChanged is run before tab.setOnClosed, so when model.openNote is called
                        // in setOnSelectionChanged, that will call layoutView again and re-add the tab we just closed
                        // So, make sure that the tab actually gets closed/removed

                        // Get the index of the tab that we need to close
                        var tabIndex: Int = -1

                        for (i in 0 until this.tabs.size) {
                            if (this.tabs[i].text == tab.text) {
                                tabIndex = i
                                break
                            }
                        }

                        if (tabIndex >= 0) this.tabs.removeAt(tabIndex)
                    } else evt.consume()
                }

                this.tabs.add(tab)
                this.selectionModel.select(tab)
            }
        }

        // Iterate through all the tabs, and make sure all the tab's notes are in model.openNotes
        // When notes and notebooks get deleted, we can just automatically close those tabs
        for (i in this.tabs.indices.reversed()) {
            if (i < this.tabs.size && model.openNotes.filter {getNotebookAndNoteName(it) == this.tabs[i].text}.size == 0) {
                this.tabs.removeAt(i)
            }
        }

        if (model.currentNote != null) {
            // Make sure the active tab corresponds to the model's current note
            val currentNotebookAndNoteName: String =
                model.currentNote!!.notebook!!.title + "/" + model.currentNote!!.title!!

            for (i in 0 until this.tabs.size) {
                if (this.tabs[i].text == currentNotebookAndNoteName) {
                    this.selectionModel.select(this.tabs[i])
                    break
                }
            }

            // refresh sync status icons
            model.currentOpenNotebook?.notes?.forEach { note ->
                if (this.tabs.any { it.text == note.notebook!!.title + "/" + note.title!! }) {
                    this.tabs.find { it.text == note.notebook!!.title + "/" + note.title!! }!!.graphic =
                        when (note.backupState) {
                            BackupState.NOT_BACKED_UP -> FontIcon(MaterialDesignC.CLOUD_OFF_OUTLINE)
                            BackupState.OUT_OF_SYNC -> FontIcon(MaterialDesignS.SYNC)
                            BackupState.BACKED_UP -> FontIcon(MaterialDesignC.CLOUD_CHECK)
                        }
                }
            }
        }
    }

    override fun update() {
        this.layoutView()
    }
}