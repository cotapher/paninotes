package com.paninotes.paninotesserver

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
        return NotebookListResponse(notebooklist)
    }

    fun backupNotebook(newNotebook: Notebook): Notebook? {
        val matchingNotebooks: MutableList<Notebook>? = notebookRepository?.findByTitle(newNotebook.title)
        if(matchingNotebooks?.size == 0) {
            println("Notebook not found by title, inserting into db")
            //associate notes with notebooks
//            newNotebook.notes?.forEach { it.notebook = newNotebook }
            //return the notes with ids
            return notebookRepository?.save(newNotebook)

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

            return notebookRepository?.save(matchingNotebook)
        }
    }
    fun deleteAllData(): String {
        notebookRepository?.deleteAll()
        noteRepository?.deleteAll()
        return "ALL DATA DELETED"
    }
}