package com.paninotes.paninotesserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
class Controller {
    @Autowired
    private val noteService: NoteService? = null
    @GetMapping("/")
    @ResponseBody
    fun getAllNotes(): NoteListResponse {
        return noteService!!.getAllNotes()
    }

    @GetMapping("/notebooks")
    @ResponseBody
    fun getAllNotebooks(): NotebookListResponse {
        return noteService!!.getAllNotebooks()
    }

    @PostMapping("/backupNotebook")
    @ResponseBody
    fun backupNotebook(@RequestBody newNotebook:Notebook): Notebook? {
        return noteService!!.backupNotebook(newNotebook)

    }

    @GetMapping("/deleteAll")
    @ResponseBody
    fun deleteAllData(): String {
        return noteService!!.deleteAllData()
    }

}