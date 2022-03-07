package com.paninotes.paninotesserver

import com.paninotes.server.Note
import com.paninotes.server.NoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller {
//    @Autowired
//    private val noteRepository: NoteRepository? = null
    @GetMapping("/")
    @ResponseBody
    fun getNote(): Note {
        return Note(1,"test")
    }

//    fun getAllNotes(): MutableList<NoteListResponse>? {
//        val noteList = noteRepository?.findAll()?.toMutableList()
////        If i send stringlist it works
////        val stringlist = noteList?.map { note -> note.toString()  }
//        val response = NoteListResponse(noteList)
//        return Collections.singletonList(response)
//    }



}