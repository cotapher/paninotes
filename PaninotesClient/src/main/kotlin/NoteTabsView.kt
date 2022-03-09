import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.*
import javafx.stage.Stage

class NoteTabsView(val model: Model, val stage: Stage): TabPane(), IView {
    init {
        this.layoutView()
        this.id = "noteTabs"
    }

    private fun layoutView() {
        // Create a tab for each open note
        this.tabs.clear()

        model.openNotes.forEach { note ->
            val tab = Tab(note.fileName)
            tab.setOnSelectionChanged {
                if (tab.isSelected) {
                    model.openNote(note)
                }
            }
            this.tabs.add(tab)
        }

        // starts here
        this.selectionModel.selectedItemProperty().addListener { observable, oldTab, newTab ->

        }
    }

    override fun update() {
        this.layoutView()
    }
}