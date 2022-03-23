package com.paninotes.paninotesserver

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class NoteServiceTest {
    private final val mockNoteRepository: NoteRepository = mock(NoteRepository::class.java)
    private final val mockNotebookRepository: NotebookRepository = mock(NotebookRepository::class.java)


    val mockNoteService: NoteService? = NoteService(mockNoteRepository, mockNotebookRepository)
    private final val mapper: ObjectMapper = jacksonObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

    val testNoteListResponse: NoteListResponse = mapper.readValue(TestDataObject.listOfNotesString)
    val testNotebookListResponse: NotebookListResponse = mapper.readValue(TestDataObject.notebookListString)

    @Test
    fun getAllNotes() {
        Mockito.`when`(mockNoteRepository.findAll()).thenReturn(testNoteListResponse.response)
        val result = mockNoteService?.getAllNotes()
        assertEquals(result?.response, testNoteListResponse.response)
    }

    @Test
    fun getAllNotebooks() {
        Mockito.`when`(mockNotebookRepository.findAll()).thenReturn(testNotebookListResponse.response)
        val result = mockNoteService?.getAllNotebooks()
        assertEquals(result, testNotebookListResponse)
    }

    @Test
    fun backupNotebook() {
        Mockito.`when`(mockNotebookRepository.findByTitle(anyString())).thenReturn(mutableListOf())
        Mockito.`when`(mockNotebookRepository.save(any())).thenReturn(testNotebookListResponse.response?.get(0))

        val result = mockNoteService?.backupNotebook(testNotebookListResponse.response!!.first())
        assertEquals(result, testNotebookListResponse.response?.get(0))
    }


}