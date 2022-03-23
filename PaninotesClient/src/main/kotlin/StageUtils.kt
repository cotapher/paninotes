import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import jfxtras.styles.jmetro.FlatAlert

object StageUtils {
    fun saveOnClose(
        model: Model,
        stage: Stage,
        htmlEditor: CustomHTMLEditor
    ) {
        if (confirmClose(model, stage, htmlEditor)) stage.close()
    }

    fun confirmClose(
        model: Model,
        stage: Stage,
        htmlEditor: CustomHTMLEditor
    ): Boolean {
        if (model.currentNote != null) {
            val confirmationAlert = FlatAlert(Alert.AlertType.CONFIRMATION)
            confirmationAlert.initOwner(stage)
            confirmationAlert.contentText = "Save changes to ${model.currentNote?.title}?"
            confirmationAlert.buttonTypes.clear()
            val saveButton = ButtonType("Save", ButtonBar.ButtonData.YES)
            val discardButton = ButtonType("Discard", ButtonBar.ButtonData.NO)
            val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
            confirmationAlert.buttonTypes.addAll(saveButton, discardButton, cancelButton)

            //show the popup
            val result = confirmationAlert.showAndWait()

            if (result.isPresent) {
                println(result)
                println(result.get())
                if (result.get() == saveButton) {
                    print(htmlEditor.htmlText)
                    model.currentNote?.saveNote(htmlEditor.htmlText)
                    return true
                }
                if (result.get() == discardButton) {
                    return true
                }
                return false
            }
        }
        return true
    }
}