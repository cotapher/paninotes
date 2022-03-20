package com.paninotes.paninotesserver
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
@Repository("notebookRepository")
interface NotebookRepository : CrudRepository<Notebook, Int> {
    fun findByTitle(title: String?): MutableList<Notebook>
}