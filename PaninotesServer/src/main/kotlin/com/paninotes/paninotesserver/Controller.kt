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
    fun backupNotebook(@RequestBody newNotebook:Notebook): String{
        notebookRepository?.save(newNotebook)
        return "Notebook Saved"
    }

    @GetMapping("/deleteAll")
    @ResponseBody
    fun deleteAllData(): String {
        notebookRepository?.deleteAll()
        noteRepository?.deleteAll()
        return "ALL DATA DELETED"
    }

}