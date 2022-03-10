import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.*
import javafx.stage.Stage

class NoteTabsView(val model: Model, val stage: Stage): TabPane(), IView {
    private var isCreatingTabs: Boolean = false
    private var tabNoteIds: ArrayList<Int?> = ArrayList()

    init {
        this.layoutView()
        this.id = "noteTabs"
    }

    private fun layoutView() {
        isCreatingTabs = true
        model.openNotes.forEach { note ->
            // Check if we already have a tab for this open note
            if (note.id != null && this.tabs.filter {(it as NoteTab).noteId == note.id}.size == 0) {
                val notebookAndNoteName: String = note.fileName!! // TODO: the tab should be like "notebook/note" where we have both the notebook and note
                val tab = NoteTab(notebookAndNoteName, note.id!!)

                tab.setOnSelectionChanged {
                    if (tab.isSelected && !isCreatingTabs) {
                        model.openNote(note)
                    }
                }

                tab.setOnClosed {
                   // tabNoteIds.remove(tab.noteId)
                    // remove from openNotes in model
                }

                this.tabs.add(tab)
                //tabNoteIds.add(note.id)
            }
        }

        isCreatingTabs = false
    }

    override fun update() {
        this.layoutView()
    }
}