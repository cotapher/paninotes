import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotlin.system.exitProcess

class TopMenuView(val stage: Stage) : Pane() {

    init {
        this.layoutView()
    }

    // TODO - add the actual menu items into here
    private fun layoutView() {
        val fileMenu: Menu

        val viewMenu: Menu
        val viewHome: MenuItem
        val viewPrev: MenuItem
        val viewNext: MenuItem
        val actionMenu: Menu
        val actionRename: MenuItem
        val actionMove: MenuItem
        val actionDelete: MenuItem
        val optionMenu: Menu


        val menuBar = MenuBar()

        // File: Quit
        val fileQuit: MenuItem
        fileMenu = Menu("File")
        fileQuit = MenuItem("Quit")
        fileMenu.items.add(fileQuit)
        menuBar.menus.add(fileMenu)

        // View: Home, Prev Next
        viewMenu = Menu("View")
        viewHome = MenuItem("Home")
        viewPrev = MenuItem("Prev")
        viewNext = MenuItem("Next")
        viewMenu.items.add(viewHome)
        viewMenu.items.add(viewPrev)
        viewMenu.items.add(viewNext)
        menuBar.menus.add(viewMenu)

        // Action: Rename, Move, Delete
        actionMenu = Menu("Action")
        actionRename = MenuItem("Rename")
        actionMove = MenuItem("Move")
        actionDelete = MenuItem("Delete")
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
        optionMenu = Menu("Option")
        menuBar.menus.add(optionMenu)

        this.children.add(menuBar)
    }
}