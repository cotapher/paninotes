package com.paninotes.paninotesserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class Controller {
    @Autowired
    private val noteRepository: NoteRepository? = null
    @Autowired
    private val notebookRepository: NotebookRepository? = null
    @GetMapping("/")
    @ResponseBody
    fun getAllNotes(): MutableList<NoteListResponse>? {
        val noteList = noteRepository?.findAll()?.toMutableList()
        val response = NoteListResponse(noteList)
        return Collections.singletonList(response)
    }

    @PostMapping("/new")
    @ResponseBody
    fun backupNote(@RequestBody newNote:Note): String{
        noteRepository?.save(newNote)
        return "Note Saved"
    }

    @GetMapping("/notebooks")
    @ResponseBody
    fun getAllNotebooks(): MutableList<Notebook>? {
        return notebookRepository?.findAll()?.toMutableList()
    }

    @PostMapping("/backupNotebook")
    @ResponseBody
    fun backupNotebook(@RequestBody newNotebook:Notebook): Notebook? {
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
            //compare which note objects have changed
            val newNotes = newNotebook.notes!!
            val merged = (matchingNotebookNotes union newNotes.toSet())
            val newNoteList = merged.toMutableList()
            matchingNotebookNotes.clear()
            matchingNotebookNotes.addAll(newNoteList)
            return notebookRepository?.save(matchingNotebook)
        }

    }

    @GetMapping("/deleteAll")
    @ResponseBody
    fun deleteAllData(): String {
        notebookRepository?.deleteAll()
        noteRepository?.deleteAll()
        return "ALL DATA DELETED"
    }

}