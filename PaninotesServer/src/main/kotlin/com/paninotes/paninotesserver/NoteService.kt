package com.paninotes.paninotesserver

import com.paninotes.paninotesserver.backupState.BackupState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NoteService(
    @Autowired val noteRepository: NoteRepository? = null,
    @Autowired val notebookRepository: NotebookRepository? = null
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun getAllNotes(): NoteListResponse {
        val noteList = noteRepository?.findAll()?.toMutableList()
        return NoteListResponse(noteList)
    }

    fun getAllNotebooks(): NotebookListResponse {

        val notebooklist = notebookRepository?.findAll()!!.toMutableList()
        notebooklist.forEach {
            it.notes?.forEach { note ->
                note.backupState = BackupState.BACKED_UP
            }
        }
        return NotebookListResponse(notebooklist)
    }

    fun backupNotebook(newNotebook: Notebook): Notebook? {
        val matchingNotebooks: MutableList<Notebook>? = notebookRepository?.findByTitle(newNotebook.title)
        if (matchingNotebooks?.size == 0) {
            logger.info("Notebook not found by title, inserting into db")
            newNotebook.notes!!.forEach {
                it.backupState = BackupState.BACKED_UP
            }
            return notebookRepository?.save(newNotebook)

        } else {
            //there should only be one make
            logger.info("Notebook exist")

            val matchingNotebook = matchingNotebooks!!.first()
            val matchingNotebookNotes = matchingNotebook.notes!!
            // Delete the old notes in that notebook in the database
            matchingNotebookNotes.forEach {
                noteRepository?.deleteById(it.id!!)
            }
            matchingNotebookNotes.clear()
            matchingNotebookNotes.addAll(newNotebook.notes!!)
            matchingNotebook.notes!!.forEach {
                it.backupState = BackupState.BACKED_UP
            }
            return notebookRepository?.save(matchingNotebook)
        }
    }

    fun deleteNotebook(notebookToDelete: Notebook): String {
        notebookRepository?.deleteById(notebookToDelete.id!!)
        return "NOTEBOOK DELETED: ${notebookToDelete.title}"
    }

    fun deleteAllData(): String {
        notebookRepository?.deleteAll()
        noteRepository?.deleteAll()
        return "ALL DATA DELETED"
    }
}