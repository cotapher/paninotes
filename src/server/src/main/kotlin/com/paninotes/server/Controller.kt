package com.paninotes.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller {
    @Autowired
    private val noteRepository: NoteRepository? = null
    @GetMapping("/")
    fun getAllNotes(): List<String>? {
        val noteList: MutableList<Note>? = noteRepository?.findAll()?.toMutableList()
        val stringlist = noteList?.map { note -> note.toString()  }
        return stringlist
    }


}