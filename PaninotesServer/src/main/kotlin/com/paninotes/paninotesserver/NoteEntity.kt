package com.paninotes.paninotesserver


import org.hibernate.Hibernate
import org.hibernate.annotations.Nationalized
import org.hibernate.annotations.Type
import org.hibernate.type.NClobType
import org.springframework.lang.NonNull
import javax.persistence.*

//import kotlinx.
@Entity
@Table(name = "note")
data class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(name = "title")
    @NonNull
    var title: String? = null,
    @Column(name = "htmltext")
    @NonNull
    @Lob
    @Basic
    @Nationalized
    var htmlText: String? = null //gives nclob
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Note

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}