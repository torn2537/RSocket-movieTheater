package com.misxy.movietheater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MovieTheaterApplication

fun main(args: Array<String>) {
    runApplication<MovieTheaterApplication>(*args)
}
