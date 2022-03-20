package com.paninotes.paninotesserver


import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.lang.NonNull
import java.io.File
import javax.persistence.*

@Entity
@Table(name = "notebook")
data class Notebook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    @Column(name = "title")
    @NonNull
    var title: String? = null,

    @Column(name= "filepath")
    var filePath: File? = null,
    @OneToMany(
        orphanRemoval = true,
        cascade = [CascadeType.ALL],
//        mappedBy = "notebook"
    )
    var notes: MutableList<Note>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notebook

        if (id != other.id) return false
        if (title != other.title) return false
        if (notes != other.notes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (notes?.hashCode() ?: 0)
        return result
    }
}