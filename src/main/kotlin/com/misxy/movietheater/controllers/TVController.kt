package com.misxy.movietheater.controllers

import com.misxy.movietheater.models.MovieScene
import com.misxy.movietheater.services.MovieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
class TVController {
    @Autowired
    private lateinit var movieService: MovieService

    @MessageMapping("tv.movie")
    fun playMovie(sceneIndex: Flux<Int>): Flux<MovieScene> {
        return sceneIndex
            .map { it - 1 }
            .map { this.movieService.getScene(it)}
            .delayElements(Duration.ofSeconds(1))
    }
}
