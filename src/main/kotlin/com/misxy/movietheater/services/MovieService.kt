package com.misxy.movietheater.services

import com.misxy.movietheater.models.MovieScene
import org.springframework.stereotype.Service


@Service
class MovieService {
    private final val scenes: MutableList<MovieScene> = mutableListOf(
        MovieScene(1, "First scene"),
        MovieScene(2, "Second scene"),
        MovieScene(3, "Third scene"),
        MovieScene(4, "Fourth scene"),
        MovieScene(5, "Fifth scene")
    )

    fun getScenes(): MutableList<MovieScene> {
        return  this.scenes
    }

    fun getScene(id: Int): MovieScene {
        return this.scenes[id]
    }
}
