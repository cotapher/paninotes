import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Pane
import kotlin.system.exitProcess

class TopMenuView(val model: Model) : Pane(), IView {

    init {
        this.layoutView()
    }

    // TODO - add the actual menu items into here
    private fun layoutView() {
        val menuBar = MenuBar()

        // File: Quit
        val fileMenu = Menu("File")
        val fileQuit = createAddToMenu(fileMenu,"Quit")
        menuBar.menus.add(fileMenu)

        // View: Home, Prev Next
        val viewMenu = Menu("View")
        val viewHome = createAddToMenu(viewMenu,"Home")
        val viewPrev = createAddToMenu(viewMenu,"Prev")
        val viewNext = createAddToMenu(viewMenu,"Next")
        menuBar.menus.add(viewMenu)

        // Action: Rename, Move, Delete
        val actionMenu = Menu("Action")
        val actionRename = createAddToMenu(actionMenu,"Rename")
        val actionMove = createAddToMenu(actionMenu,"Move")
        val actionDelete = createAddToMenu(actionMenu,"Delete")
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

    private fun createAddToMenu(menu: Menu, menuItemName:String): MenuItem {
        val menuItem = MenuItem(menuItemName)
        menu.items.add(menuItem)
        return menuItem
    }

    override fun update() {
        // TODO("Not yet implemented")
    }
}