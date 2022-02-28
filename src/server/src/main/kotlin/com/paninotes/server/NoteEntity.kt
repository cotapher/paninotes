package com.paninotes.server


import javax.persistence.*


@Entity
class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null

    @Column(nullable = false)
    private val name: String? = null //...
}