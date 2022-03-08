package com.paninotes.paninotesserver
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
@Repository("noteRepository")
interface NoteRepository : CrudRepository<Note, Int> {
}