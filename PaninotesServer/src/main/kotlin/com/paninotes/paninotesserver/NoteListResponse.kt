package com.paninotes.paninotesserver

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonDeserialize
class NoteListResponse(val response: MutableList<Note>?) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteListResponse

        if (response != other.response) return false

        return true
    }

    override fun hashCode(): Int {
        return response.hashCode()
    }
}