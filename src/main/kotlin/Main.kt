
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage
import kotlin.system.exitProcess

class Main : Application() {

    override fun start(stage: Stage) {
        // create the root of the scene graph
        // BorderPane supports placing children in regions around the screen

        // Initialize all widgets--------------------------------------------------------------------------------------------
        val layout = BorderPane()

        // top: menubar----------------------------------------------------------------------
        val menuSection = VBox()
        menuSection.isFocusTraversable = false
        val menuBar = MenuBar()
        menuBar.isFocusTraversable = false
        val fileMenu = Menu("File")
        val fileQuit = MenuItem("Quit")
        val viewMenu = Menu("View")
        val viewHome = MenuItem("Home")
        val viewPrev = MenuItem("Prev")
        val viewNext = MenuItem("Next")
        val actionMenu = Menu("Action")
        val actionRename = MenuItem("Rename")
        val actionMove = MenuItem("Move")
        val actionDelete = MenuItem("Delete")
        val optionMenu = Menu("Option")
        val optionHiddenToggle = RadioMenuItem("Show hidden")


        //------------------------------------
        menuBar.menus.add(fileMenu)
        fileMenu.items.add(fileQuit)
        // handle file->quit
        fileQuit.setOnAction {
            exitProcess(0)
        }

        // Add a shortcut CTRL+Q for file->quit
        fileQuit.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)

        menuBar.menus.add(viewMenu)
        viewMenu.items.add(viewHome)

        viewMenu.items.add(viewPrev)

        viewMenu.items.add(viewNext)

        menuBar.menus.add(actionMenu)
        actionMenu.items.add(actionRename)

        actionMenu.items.add(actionMove)

        actionMenu.items.add(actionDelete)

        menuBar.menus.add(optionMenu)
        optionMenu.items.add(optionHiddenToggle)

        menuSection.children.add(menuBar)

        val htmlEditor = HTMLEditor()
        htmlEditor.htmlText = "Hello <a href=\"https://github.com/TestFX/TestFX\">world</a>"

        // build the scene graph
        layout.top = menuSection
        layout.center = htmlEditor
        layout.padding = Insets(5.0)

        // create and show the scene
        val scene = Scene(layout)

        stage.width = 800.0
        stage.height = 500.0
        stage.scene = scene
        stage.isResizable = true
        stage.title = "File Browser"
        stage.show()
    }
}