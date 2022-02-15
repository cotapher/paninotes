
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import java.nio.file.*
import kotlin.system.exitProcess

class Main : Application() {
    private val homeDir: Path = Paths.get("${System.getProperty("user.dir")}/test/")

    private var currentRootPath: Path = homeDir

    private var hidden = false
    override fun start(stage: Stage) {
        // create the root of the scene graph
        // BorderPane supports placing children in regions around the screen

        // Initialize all widgets--------------------------------------------------------------------------------------------
        val layout = BorderPane()
        //status line
        val statusline = HBox()
        val currentPathLabel = Label()
        statusline.children.add(currentPathLabel)

        // left: tree
        val tree = ListView<String>()

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

        //top: toolbar-----------------------------------------------------------------------
        val toolBar = ToolBar()
        toolBar.isFocusTraversable = false
        val buttonHome = Button("Home")
        buttonHome.graphic = ImageView("baseline_home_black_24dp.png")
        val buttonPrev = Button("Prev")
        buttonPrev.graphic = ImageView("baseline_arrow_back_black_24dp.png")
        val buttonNext = Button("Next")
        buttonNext.graphic = ImageView("baseline_arrow_forward_black_24dp.png")
        val buttonRename = Button("Rename")
        buttonRename.graphic = ImageView("baseline_edit_black_24dp.png")
        val buttonMove = Button("Move")
        buttonMove.graphic = ImageView("baseline_control_camera_black_24dp.png")
        val buttonDelete = Button("Delete")
        buttonDelete.graphic = ImageView("baseline_delete_forever_black_24dp.png")

        //Disable button traversing
        buttonHome.isFocusTraversable = false
        buttonPrev.isFocusTraversable = false
        buttonNext.isFocusTraversable = false
        buttonRename.isFocusTraversable = false
        buttonMove.isFocusTraversable = false
        buttonDelete.isFocusTraversable = false

        // center display widgets--------------------------------------------------------------------------
        val textRenderingArea = TextArea()
        val imageRenderingArea = ImageView()
        //Start Actions ---------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------

        // Loop through and add the file system of the homedir for init
        updateTreeListDirectory(tree, homeDir, currentPathLabel,imageRenderingArea, textRenderingArea)
        val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
        renderTextOrImage(selectedPathString,listItemContents,imageRenderingArea,textRenderingArea)
        // handle mouse clicked actions on ListView
        tree.setOnMouseClicked { event ->
            //Get the selected contents in the listview
            val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)

            //Check number of clicks
            if (event.clickCount == 1) {

                //Set the statusline label
                currentPathLabel.text = selectedPathString

                renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
            } else {
                // double click
                descendFolderOrRenderFile(selectedPathString, tree, currentPathLabel,listItemContents, imageRenderingArea, textRenderingArea)
                //get updated path
                val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
                renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
            }
        }
        // handle keyboard input on listview
        tree.setOnKeyReleased { event ->
            //Get the selected contents in the listview
            val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
            if (event.code == KeyCode.UP || event.code == KeyCode.DOWN) {
                //update status bar
                currentPathLabel.text = selectedPathString
                renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
            } else if (event.code == KeyCode.ENTER) {
                descendFolderOrRenderFile(selectedPathString, tree, currentPathLabel,listItemContents, imageRenderingArea, textRenderingArea)

                //get updated path
                val (listItemContentsUPDATED: String, selectedPathStringUPDATED) = resolveSelectedPathString(tree)
                renderTextOrImage(selectedPathStringUPDATED, listItemContentsUPDATED, imageRenderingArea, textRenderingArea)
            } else if (event.code == KeyCode.DELETE || event.code == KeyCode.BACK_SPACE) {
                goToParentDir(tree, currentPathLabel,imageRenderingArea, textRenderingArea)
            }
        }

        // textarea actions--------------------------------------------------------------------------
        val renderContentPane = Group()
//        renderContentPane.padding = Insets(2.0)
        val calcHeight = 350.0 //500 - (29+46+16) stage.height -(menuSection.height + toolBar.height + statusline.height)
        val calcWidth =  527.0 //542 = 800-248 = stage.width - tree.width - 10 padding
        renderContentPane.isFocusTraversable = false

        textRenderingArea.isFocusTraversable = false
        textRenderingArea.isWrapText = true
        textRenderingArea.isVisible = false
        textRenderingArea.isEditable = false
        textRenderingArea.prefWidth = calcWidth
        textRenderingArea.prefHeight = calcHeight
        imageRenderingArea.isFocusTraversable = false
        imageRenderingArea.isPreserveRatio = true
        imageRenderingArea.isSmooth = true
        imageRenderingArea.fitWidth = calcWidth
        imageRenderingArea.fitHeight = calcHeight
        renderContentPane.children.add(textRenderingArea)
        renderContentPane.children.add(imageRenderingArea)

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
        viewHome.setOnAction {
                updateTreeListDirectory(tree, homeDir, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        viewMenu.items.add(viewPrev)
        viewPrev.setOnAction {
            goToParentDir(tree, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        viewMenu.items.add(viewNext)
        viewNext.setOnAction {
            val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
            descendFolderOrRenderFile(selectedPathString, tree, currentPathLabel,listItemContents, imageRenderingArea, textRenderingArea)

            //get updated path
            val (listItemContentsUPDATED: String, selectedPathStringUPDATED) = resolveSelectedPathString(tree)
            renderTextOrImage(selectedPathStringUPDATED, listItemContentsUPDATED, imageRenderingArea, textRenderingArea)
        }

        menuBar.menus.add(actionMenu)
        actionMenu.items.add(actionRename)
        actionRename.setOnAction {
            println("Rename Action from Menubar item")
            renameFeature(tree)
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        actionMenu.items.add(actionMove)
        actionMove.setOnAction {
            println("Move action from menubar")
            moveFeature(tree)
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        actionMenu.items.add(actionDelete)
        actionDelete.setOnAction {
            deleteFeature(tree)
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }

        menuBar.menus.add(optionMenu)
        optionMenu.items.add(optionHiddenToggle)
        optionHiddenToggle.setOnAction {
            hidden = !hidden
            println("Changed hiddenbool to $hidden")
            updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }



        menuSection.children.add(menuBar)




        menuSection.children.add(toolBar)
        toolBar.items.add(buttonHome)
        buttonHome.setOnAction {
                updateTreeListDirectory(tree, homeDir, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        toolBar.items.add(buttonPrev)
        buttonPrev.setOnAction {
            //Go to the parent directory
            goToParentDir(tree, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        toolBar.items.add(buttonNext)
        buttonNext.setOnAction {
            val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
            descendFolderOrRenderFile(selectedPathString, tree, currentPathLabel,listItemContents, imageRenderingArea, textRenderingArea)

            //get updated path
            val (listItemContentsUPDATED: String, selectedPathStringUPDATED) = resolveSelectedPathString(tree)
            renderTextOrImage(selectedPathStringUPDATED, listItemContentsUPDATED, imageRenderingArea, textRenderingArea)
        }
        toolBar.items.add(buttonRename)
        buttonRename.setOnAction {
            println("Rename action from button")
            renameFeature(tree)
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        toolBar.items.add(buttonMove)
        buttonMove.setOnAction {
            println("Move action from button")
            moveFeature(tree)
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }
        toolBar.items.add(buttonDelete)
        buttonDelete.setOnAction {
            deleteFeature(tree)
            updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)

        }

        // build the scene graph
        layout.top = menuSection
        layout.left = tree
        layout.center = renderContentPane
        layout.bottom = statusline
        layout.padding = Insets(5.0)

        // create and show the scene
        val scene = Scene(layout)

        stage.width = 800.0
        stage.height = 500.0
        stage.scene = scene
        stage.isResizable = false
        stage.title = "File Browser"
        stage.show()
    }

    private fun renderTextOrImage(
        selectedPathString: String,
        listItemContents: String,
        imageRenderingArea: ImageView,
        textRenderingArea: TextArea
    ) {
        val fileToRender = File(selectedPathString)
        if (fileToRender.isFile && fileToRender.canRead()) {
            if (selectedPathString.contains(".png") || selectedPathString.contains(".jpg") || selectedPathString.contains(".bmp")) {
                //hide image show text area
                imageRenderingArea.isVisible = true
                textRenderingArea.isVisible = false
                println(selectedPathString)
                val imageToShow = Image(File(selectedPathString).toURI().toString())
                imageRenderingArea.image = imageToShow
                println(imageRenderingArea.imageProperty().toString())
                println(imageRenderingArea.fitHeightProperty().toString())
                println(imageRenderingArea.fitWidthProperty().toString())
            } else if (selectedPathString.contains(".txt") || selectedPathString.contains(".md")) {
                //hide image show text area
                imageRenderingArea.isVisible = false
                textRenderingArea.isVisible = true
                println("TEXTAREA H: ${textRenderingArea.height}")
                println("TEXTAREA W: ${textRenderingArea.width}")
                textRenderingArea.text = ""
                fileToRender.readLines().forEach { line -> textRenderingArea.text = textRenderingArea.text + line }
            } else {
                imageRenderingArea.isVisible = false
                textRenderingArea.isVisible = false
            }
        } else {
            imageRenderingArea.isVisible = false
            textRenderingArea.isVisible = false
        }
    }

    private fun deleteFeature(
        tree: ListView<String>
    ) {
        val (_: String, selectedPathString) = resolveSelectedPathString(tree)
        val popup = Alert(Alert.AlertType.CONFIRMATION)
        popup.title = "Delete Utility"
        val currentFileOrDir = File(selectedPathString)
        if (currentFileOrDir.canWrite()) {
            val fileOrFolderString = if (currentFileOrDir.isDirectory) "Folder" else "File"
            popup.headerText = "Are you sure you want to permanently delete this $fileOrFolderString?"

            //show the popup
            val result = popup.showAndWait()

            if (result.isPresent) {
                println(result)
                println(result.get())
                if (result.get() == ButtonType.OK) {
                    try {
                        println("You have deleted the file $currentFileOrDir")
                        if (currentFileOrDir.isDirectory) {
                            println("Request delete on folder: recursing subdirectories")
                            recursiveDeleteSubDir(currentFileOrDir)
                        }
                        val deletionResult = currentFileOrDir.delete()
                        if (deletionResult) {
                            println("Deleted $fileOrFolderString")
                        } else {
                            println("$fileOrFolderString deletion was unsuccessful")
                        }
                    } catch (e: SecurityException) {
                        print("An error occurred while deleting")
                    }

                } else {
                    print("Delete Action Cancelled")
                }
            }
        }
    }

    private fun renameFeature(
        tree: ListView<String>
    ) {
        val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
        val popup = TextInputDialog(listItemContents.removeSuffix("/"))
        popup.title = "Rename Utility"
        val currentFileOrDir = File(selectedPathString)
        if (currentFileOrDir.canWrite()) {
            val fileOrFolderString = if (currentFileOrDir.isDirectory) "Folder" else "File"
            popup.headerText = "Rename $fileOrFolderString?"
            popup.contentText = "Enter new name for $fileOrFolderString $listItemContents"

            //show the popup
            val result = popup.showAndWait()
            if (result.isPresent) {
                println(result)
                println(result.get())
                val textResult = result.get()

                //rename file
                // get file references
                val renamedFileOrDir = File(currentRootPath.resolve(textResult).toString())
                if (renamedFileOrDir.exists()) {
                    println("Error: ${renamedFileOrDir.name} already exists")
                    generateAlertDialogPopup(
                        Alert.AlertType.ERROR, "Rename Error", "$listItemContents already exists, " +
                                "try clicking rename button again and choosing a different name"
                    )
                } else {
                    //Try renaming
                    val success = currentFileOrDir.renameTo(renamedFileOrDir)
                    if (success) {
                        println("Successfully renamed ${currentFileOrDir.name} to ${renamedFileOrDir.name}")
                        generateAlertDialogPopup(
                            Alert.AlertType.INFORMATION,
                            "Rename Successful",
                            "Successfully renamed ${currentFileOrDir.name} to ${renamedFileOrDir.name}"
                        )
                    } else {
                        println("Error renaming ${currentFileOrDir.name} to ${renamedFileOrDir.name}")
                        generateAlertDialogPopup(
                            Alert.AlertType.ERROR,
                            "Rename Unsuccessful",
                            "Error using File renameTo method to rename ${currentFileOrDir.name} to ${textResult}. \n" +
                                    "You probably used an illegal character"
                        )
                    }
                }
            }
        }
    }

    private fun moveFeature(
        tree: ListView<String>
    ) {
        val (listItemContents: String, selectedPathString) = resolveSelectedPathString(tree)
        val popup = TextInputDialog(currentRootPath.toString())
        popup.title = "Move Utility"
        val currentFileOrDir = File(selectedPathString)
        if (currentFileOrDir.canWrite()) {
            val fileOrFolderString = if (currentFileOrDir.isDirectory) "Folder" else "File"
            popup.headerText = "Enter new directory path to move this $fileOrFolderString to:"

            //show the popup
            val result = popup.showAndWait()
            if (result.isPresent) {
                println(result)
                println(result.get())
                val textResult = if(result.get().endsWith("/")) result.get() else result.get()+"/"
                try {
                    //rename file
                    // get file references
                    val renamedFileOrDir = File(currentRootPath.resolve(textResult).toString())
                    if (!renamedFileOrDir.isDirectory) {
                        println("Error: ${renamedFileOrDir.name} is not a directory")
                        throw FileSystemException("Invalid Destination")
                    } else {
                        //Try renaming

                        val source = Paths.get(currentFileOrDir.path)
                        println("source and target")
                        println(Files.isDirectory(source))
                        println(source)
                        val destination = Paths.get(renamedFileOrDir.path)

                        val target = destination.resolve(listItemContents)
                        println(Files.isDirectory(target))
                        println(target)
                        Files.move(source, target)
                        generateAlertDialogPopup(Alert.AlertType.INFORMATION,"Move Success","$fileOrFolderString has been moved to $destination/")
                    }
                } catch (e: FileAlreadyExistsException) {
                    print("ERROR: File already exists")
                    generateAlertDialogPopup(
                        Alert.AlertType.ERROR,
                        "File Already Exists",
                        "Destination has the file with a same name"
                    )
                } catch (e: FileSystemException) {
                    print("ERROR: Directory Invalid")
                    generateAlertDialogPopup(
                        Alert.AlertType.ERROR,
                        "Directory Invalid",
                        "Destination does not exist in filesystem"
                    )
                }
            }
        }
    }

    private fun generateAlertDialogPopup(type: Alert.AlertType, title: String, content: String) {
        val fileExistsAlert = Alert(type)
        fileExistsAlert.title = title
        val errorContent = Label(content)
        errorContent.isWrapText = true
        fileExistsAlert.dialogPane.content = errorContent
        fileExistsAlert.showAndWait()
    }

    private fun goToParentDir(
        tree: ListView<String>,
        currentPathLabel: Label,
        imageRenderingArea: ImageView,
        textRenderingArea: TextArea
    ) {
        if(currentRootPath.parent != null){
            //avoid null path
            currentRootPath = currentRootPath.parent
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)
        }
    }

    private fun descendFolderOrRenderFile(
        selectedPathString: String,
        tree: ListView<String>,
        currentPathLabel: Label,
        listItemContents: String,
        imageRenderingArea: ImageView,
        textRenderingArea: TextArea
    ) {
        //make sure folder is not empty
        if (!tree.items.isEmpty()) {
            if (File(selectedPathString).isDirectory) {
                println("Action on a Directory")
                //update currentRootPath
                currentRootPath = currentRootPath.resolve(selectedPathString)
                //Update the new tree
                updateTreeListDirectory(tree, currentRootPath, currentPathLabel,imageRenderingArea, textRenderingArea)
                renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
            } else {
                renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
                println("Action on a file $selectedPathString")
            }
        }
    }

    private fun resolveSelectedPathString(tree: ListView<String>): Pair<String, String> {
        // Check if folder is empty
        if (!tree.items.isEmpty()) {
            val listItemContents: String = tree.selectionModel.selectedItem.toString()
            println("You have Selected $listItemContents")

            val selectedPathString = currentRootPath.resolve(listItemContents).toString()
            return Pair(listItemContents, selectedPathString)
        } else {
            //folder is empty
            return Pair("", "")
        }
    }

    private fun updateTreeListDirectory(tree: ListView<String>, dir: Path, currentPathLabel: Label,imageRenderingArea: ImageView,textRenderingArea: TextArea) {
        tree.items.clear()
        currentRootPath = dir
        Files.list(dir).sorted().forEach {
            //Check the hidden toggle
            if(hidden){
                //show all
                if (Files.isDirectory(it)) {
                    tree.items.add(it.getName(it.nameCount - 1).toString() + File.separator)
                } else {
                    tree.items.add(it.getName(it.nameCount - 1).toString())
                }
            } else {
                //show only non hidden
                println("Does ${it.fileName} start with period")
                println(it.fileName.toString().startsWith("."))
                if(!it.fileName.toString().startsWith(".")){
                    if (Files.isDirectory(it)) {
                        tree.items.add(it.getName(it.nameCount - 1).toString() + File.separator)
                    } else {
                        tree.items.add(it.getName(it.nameCount - 1).toString())
                    }
                }
            }
        }
        //Check if the folder is empty
        if (!tree.items.isEmpty()) {
            //Select the first one item as default
            tree.selectionModel.select(0)

            //update status
            //Set the statusline label
            val listItemContents: String = tree.items.get(tree.selectionModel.selectedIndex).toString()
            println("You have Selected $listItemContents")
            //change the current root path to new directory
            val selectedPathString = currentRootPath.resolve(listItemContents).toString()
            println("Current Path updated to $selectedPathString")
            renderTextOrImage(selectedPathString, listItemContents, imageRenderingArea, textRenderingArea)
            //Add slash if it is a directory
            if (File(selectedPathString).isDirectory) {
                currentPathLabel.text = selectedPathString + File.separator
            } else {
                currentPathLabel.text = selectedPathString
            }
        }
    }

    private fun recursiveDeleteSubDir(dir: File){
        dir.listFiles()?.forEach { file ->
            if(file.isDirectory){
                recursiveDeleteSubDir(file)
            }
            val delResult = file.delete()
            if(delResult){
                println("Deleted ${file.path}")
            } else {
                print("Failed deleting ${file.path}")
            }
        }
    }
}