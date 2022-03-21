package com.paninotes.paninotesserver

import BackupState.BackupState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NoteService (    @Autowired val noteRepository: NoteRepository? = null,
    @Autowired val notebookRepository: NotebookRepository? = null){

    fun getAllNotes(): NoteListResponse {
        val noteList = noteRepository?.findAll()?.toMutableList()
        return NoteListResponse(noteList)
    }

    fun getAllNotebooks(): NotebookListResponse {

        val notebooklist =notebookRepository?.findAll()!!.toMutableList()
        notebooklist.forEach {
            it.notes?.forEach {
                    note -> note.backupState = BackupState.BACKED_UP
            }
        }
        return NotebookListResponse(notebooklist)
    }

    fun backupNotebook(newNotebook: Notebook): Notebook? {
        val matchingNotebooks: MutableList<Notebook>? = notebookRepository?.findByTitle(newNotebook.title)
        if(matchingNotebooks?.size == 0) {
            println("Notebook not found by title, inserting into db")
            //associate notes with notebooks
//            newNotebook.notes?.forEach { it.notebook = newNotebook }
            //return the notes with ids
            val backedupNotebook = notebookRepository?.save(newNotebook)
            backedupNotebook!!.notes!!.forEach {
                it.backupState = BackupState.BACKED_UP
            }
            return backedupNotebook

        } else {
            //there should only be one make
            println("Notebook exist")

            val matchingNotebook = matchingNotebooks!!.first()
            val matchingNotebookNotes = matchingNotebook.notes!!
            // Delete the old notes in that notebook in the database
            matchingNotebookNotes.forEach {
                noteRepository?.deleteById(it.id!!)
            }
            /*
            //compare which note objects have changed
            val newNotes = newNotebook.notes!!
            val merged = (matchingNotebookNotes union newNotes.toSet())
            val newNoteList = merged.toMutableList()
             */

            matchingNotebookNotes.clear()
            matchingNotebookNotes.addAll(newNotebook.notes!!)
            val backedupNotebook = notebookRepository?.save(matchingNotebook)
            backedupNotebook!!.notes!!.forEach {
                it.backupState = BackupState.BACKED_UP
            }
            return backedupNotebook
        }
    }
    fun deleteAllData(): String {
        notebookRepository?.deleteAll()
        noteRepository?.deleteAll()
        return "ALL DATA DELETED"
    }
}