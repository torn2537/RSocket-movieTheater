package com.misxy.movietheater.models

import com.fasterxml.jackson.annotation.JsonProperty


data class MovieScene(
    @JsonProperty("sceneId")
    var sceneId: Int = 0,
    @JsonProperty("sceneDescription")
    var sceneDescription: String
)
