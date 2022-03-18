import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Pane
import javafx.scene.web.HTMLEditor
import javafx.stage.Stage
import jfxtras.styles.jmetro.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Paths.get
import java.util.*
import kotlin.system.exitProcess


class TopMenuView(val model: Model, val htmlEditor: HTMLEditor,val stage: Stage, val jMetro: JMetro) : Pane(), IView{

    private val LIGHT_STYLESHEET_URL = TopMenuView::class.java.getResource("css/light.css")?.toExternalForm()
    private val DARK_STYLESHEET_URL = TopMenuView::class.java.getResource("css/dark.css")?.toExternalForm()


    init {
        this.layoutView()
    }

    // TODO - add the actual menu items into here
    private fun layoutView() {
        val menuBar = MenuBar()

        //responsive menubar
        menuBar.prefWidthProperty().bind(stage.widthProperty())

        // File: Quit
        val fileMenu = Menu("File")
        val fileNewNote = createAddToMenu(fileMenu,"New Note")
        val fileSave = createAddToMenu(fileMenu,"Save")
        val fileQuit = createAddToMenu(fileMenu,"Quit")
        menuBar.menus.add(fileMenu)

        // Option:
        val optionMenu = Menu("Option")
        val optionSearch = createAddToMenu(optionMenu, "Search")
        val optionTheme = createAddToMenu(optionMenu, "Use Dark Theme")
        val optionRestoreBackup = createAddToMenu(optionMenu,"Restore Backup")
        val optionTestSend = createAddToMenu(optionMenu,"Send a Test Note")
        val optionBackupCurrentNotebook = createAddToMenu(optionMenu,"Backup Current Notebook")
        val optionDeleteAllData = createAddToMenu(optionMenu,"Delete Backup Data")
        val optionUsage = createAddToMenu(optionMenu, "Usage Statistics")
        menuBar.menus.add(optionMenu)

        fileMenu.id = "menu-fileMenu"
        fileNewNote.id = "menuitem-fileNewNote"
        fileSave.id = "menuitem-fileSave"
        fileQuit.id = "menuitem-fileQuit"
        optionMenu.id = "menu-optionMenu"

        fileNewNote.setOnAction {
            // If there is currently a notebook open, then we will automatically create a new note in that notebook
            if (model.currentOpenNotebook != null) {
                model.createNotePopup(model.currentOpenNotebook!!)
            } else {
                // Get list of all notebook names
                val notebookNames: List<String> = model.notebooks.map({ it.title })

                // If there is no notebooks, show an error popup telling the user to create a notebook first
                if (notebookNames.isEmpty()) {
                    val warningPopup = FlatAlert(AlertType.WARNING)
                    warningPopup.initOwner(stage)
                    warningPopup.headerText = "No Notebooks!"
                    warningPopup.contentText = "You have no notebooks! You can only create a note in a notebook"

                    warningPopup.showAndWait()
                } else {
                    // Open a choice dialog to prompt the user what notebook they want to create the note in
                    val chooseNotebookDialog: FlatChoiceDialog<String> = FlatChoiceDialog(notebookNames[0], notebookNames)
                    chooseNotebookDialog.initOwner(stage)
                    chooseNotebookDialog.headerText = "Choose Notebook to create a note in:"

                    val result: Optional<String> = chooseNotebookDialog.showAndWait()

                    // If the result is present, that means the user pressed the OK button
                    // Otherwise, they pressed cancel, and we don't want to add the notebook
                    if (result.isPresent) {
                        // get the selected item
                        val selectedNotebookTitle: String = chooseNotebookDialog.selectedItem as String
                        if (selectedNotebookTitle.isNotEmpty()) {
                            val selectedNotebook: Notebook? = model.getNotebookByTitle(selectedNotebookTitle)
                            if (selectedNotebook != null) {
                                model.createNotePopup(selectedNotebook!!)
                            }
                        }
                    }
                }
            }
        }

        fileSave.setOnAction {
            print(htmlEditor.htmlText)
            model.currentNote?.saveNote(htmlEditor.htmlText)
        }


        fileQuit.setOnAction {
            exitProcess(0)
        }

        // Add a shortcut CTRL+Q for file->quit
        fileNewNote.accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN)
        //need new directory, open directory
        fileSave.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        fileQuit.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)
        optionSearch.accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)


        optionSearch.setOnAction{
            val dialog = FlatTextInputDialog("")
            dialog.initOwner(stage)
            dialog.title = "Search"
            dialog.headerText = "Find Word"

            (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "Search"

            val oldText = htmlEditor.htmlText
            val result = dialog.showAndWait()
            if (result.isPresent) {
                val entered = result.get()
                if (entered.compareTo("") == 0) {
                    (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "OK"
                    dialog.show()
                    dialog.headerText = "No Input"
                } else {
                    val noHtmlTags = Jsoup.parse(htmlEditor.htmlText).text()
                    println(htmlEditor.htmlText)
                    println(noHtmlTags)

                    val delim = " "
                    val list = noHtmlTags.split(delim)
                    var wordIndexes = ArrayList<Int>()

                    var outputString = ""
                    for ((i, item) in list.withIndex()) {
                        if (i != 0) {
                            outputString += " "
                        }
                        if((item.lowercase()).compareTo(entered.lowercase()) == 0 ) {
                            outputString = outputString + "<mark>" + item + "</mark>"
                            wordIndexes.add(i)
                        } else {
                            outputString += item
                        }
                    }

                    println(outputString)
                    val oldText = htmlEditor.htmlText
                    htmlEditor.htmlText = outputString

                    (dialog.dialogPane.lookupButton(ButtonType.OK) as Button).text = "OK"

                    dialog.headerText = "Found " + wordIndexes.size
                    dialog.showAndWait()
                    htmlEditor.htmlText = oldText
                }
            }
        }

        optionUsage.setOnAction {
            val usageInfo = FlatAlert(Alert.AlertType.CONFIRMATION)
            usageInfo.initOwner(stage)
            usageInfo.headerText = "Statistics:"
            usageInfo.title = "Usage Statistics"
            val noHtmlTags = Jsoup.parse(htmlEditor.htmlText).text()
            val delim = " "
            val list = noHtmlTags.split(delim)
            val textInParagraphs = Jsoup.parse(htmlEditor.htmlText).select("p")
            val emptyParagraphs = Jsoup.parse(htmlEditor.htmlText).select("p:empty")
            val paragraphs = textInParagraphs.size
            var characters = 0;
            println(textInParagraphs)

            for (i in 0..list.size-1) {
                for (j in 0..list[i].length-1) {
                    characters++
                }
            }

            println(emptyParagraphs.size)

            usageInfo.contentText = "Words: ${list.size}\n" +
                    "Characters (no spaces): ${characters}\n" +
                    "Charaters (with spaces) ${characters + (noHtmlTags.length- characters -paragraphs)}\n" +
                    "Paragraphs: ${paragraphs}\n"

            //show the popup
            usageInfo.showAndWait()

        }

        optionTheme.setOnAction {
            val ss = jMetro.scene.stylesheets
            if (jMetro.style == Style.LIGHT) {
                ss.clear()
                jMetro.style = Style.DARK
                ss.add(DARK_STYLESHEET_URL)
                optionTheme.text = "Use Light theme"
            }
            else {
                ss.clear()
                jMetro.style = Style.LIGHT
                ss.add(LIGHT_STYLESHEET_URL)
                optionTheme.text = "Use Dark theme"
            }
        }

        optionRestoreBackup.setOnAction {
            model.restoreBackup()
        }

        optionTestSend.setOnAction {
            model.testSendNote()
        }

        optionBackupCurrentNotebook.setOnAction {
            model.makeBackup()
        }

        optionDeleteAllData.setOnAction {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/deleteAll"))
                .GET()
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() == 200){
                println("Success ${response.statusCode()}")
                print(response.body().toString())
//                val noteList: List<Note> = mapper.readValue(response.body().toString())
//                print(noteList.size)
//                print(noteList.toString())
            } else {
                print("ERROR ${response.statusCode()}")
                print(response.body().toString())
            }
        }
        this.children.add(menuBar)



        stage.setOnCloseRequest {
            if(model.currentNote != null) {
                val confirmationAlert = FlatAlert(Alert.AlertType.CONFIRMATION)
                confirmationAlert.initOwner(stage)
                confirmationAlert.contentText = "Save changes to ${model.currentNote?.title}?"
                confirmationAlert.buttonTypes.clear()
                val discardButton = ButtonType("Discard")
                val saveButton = ButtonType("Save")
                val cancelButton = ButtonType("Cancel")
                confirmationAlert.buttonTypes.addAll(discardButton, saveButton, cancelButton)
                //show the popup
                val result = confirmationAlert.showAndWait()

                if (result.isPresent) {
                    println(result)
                    println(result.get())
                    if (result.get() == saveButton) {
                        print(htmlEditor.htmlText)
                        model.currentNote?.saveNote(htmlEditor.htmlText)
                        Platform.exit()
                        exitProcess(0)
                    } else if (result.get() == cancelButton) {
                        it.consume()
                    }
                }
            }
        }
    }



    private fun createAddToMenu(menu: Menu, menuItemName:String): MenuItem {
        val menuItem = MenuItem(menuItemName)
        menu.items.add(menuItem)
        return menuItem
    }

    override fun update() {
        //add a condition to only show editor if there is file assigned to model.currentFile
        if(model.currentNote != null){
            htmlEditor.htmlText = model.currentNote?.htmlText
            htmlEditor.isVisible = true
        } else {
            //hide the editor maybe welcome message
            htmlEditor.isVisible = false
        }
    }
}
