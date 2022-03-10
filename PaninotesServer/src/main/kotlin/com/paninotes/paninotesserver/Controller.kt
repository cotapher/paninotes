package com.paninotes.paninotesserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class Controller {
    @Autowired
    private val noteRepository: NoteRepository? = null
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
        return "THIS WORKS"
    }
}