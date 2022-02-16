import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Pane
import kotlin.system.exitProcess

class TopMenuView : Pane() {

    init {
        this.layoutView()
    }

    // TODO - add the actual menu items into here
    private fun layoutView() {


        val menuBar = MenuBar()

        // File: Quit
        val fileMenu = Menu("File")
        val fileQuit = MenuItem("Quit")
        fileMenu.items.add(fileQuit)
        menuBar.menus.add(fileMenu)

        // View: Home, Prev Next
        val viewMenu = Menu("View")
        val viewHome = MenuItem("Home")
        val viewPrev = MenuItem("Prev")
        val viewNext = MenuItem("Next")
        viewMenu.items.add(viewHome)
        viewMenu.items.add(viewPrev)
        viewMenu.items.add(viewNext)
        menuBar.menus.add(viewMenu)

        // Action: Rename, Move, Delete
        val actionMenu = Menu("Action")
        val actionRename = MenuItem("Rename")
        val actionMove = MenuItem("Move")
        val actionDelete = MenuItem("Delete")
        actionMenu.items.add(actionRename)
        actionMenu.items.add(actionMove)
        actionMenu.items.add(actionDelete)
        menuBar.menus.add(actionMenu)


        fileQuit.setOnAction {
            exitProcess(0)
        }

        // Add a shortcut CTRL+Q for file->quit
        fileQuit.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)

        // Option:
        val optionMenu = Menu("Option")
        menuBar.menus.add(optionMenu)

        this.children.add(menuBar)
    }
}