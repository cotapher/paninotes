import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Pane
import javafx.scene.web.HTMLEditor
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

class TopMenuView(val model: Model, val htmlEditor: HTMLEditor,val stage: Stage) : Pane(), IView{

    init {
        this.layoutView()
    }

    // TODO - add the actual menu items into here
    private fun layoutView() {


        val menuBar = MenuBar()

        // File: Quit
        val fileMenu = Menu("File")
        fileMenu.id = "menubar-file"
        val fileOpen = createAddToMenu(fileMenu,"Open")
        val fileSave = createAddToMenu(fileMenu,"Save")
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

        fileOpen.setOnAction {
            val fileDialog = FileChooser()
            fileDialog.title = "Select an HTML File"
            val extFilter = FileChooser.ExtensionFilter("HTML files (*.html)", "*.html")
            fileDialog.extensionFilters.add(extFilter)
            val file: File? = fileDialog.showOpenDialog(stage)
            model.openAndReadHTMLFile(file)
        }

        fileSave.setOnAction {
            print(htmlEditor.htmlText)
            model.currentFile.writeText(htmlEditor.htmlText)
        }


        fileQuit.setOnAction {
            exitProcess(0)
        }

        // Add a shortcut CTRL+Q for file->quit
        fileOpen.accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN)
        fileSave.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
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
        htmlEditor.htmlText = model.currentFileContents
    }
}