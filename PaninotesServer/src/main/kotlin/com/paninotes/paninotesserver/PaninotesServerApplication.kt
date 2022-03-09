package com.paninotes.paninotesserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaninotesServerApplication

fun main(args: Array<String>) {
    runApplication<PaninotesServerApplication>(*args)
}
