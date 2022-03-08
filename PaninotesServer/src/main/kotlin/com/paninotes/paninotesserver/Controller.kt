package com.paninotes.paninotesserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
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
}