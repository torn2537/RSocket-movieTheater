package com.misxy.movietheater.controllers

import com.misxy.movietheater.enums.TicketStatus
import com.misxy.movietheater.models.MovieScene
import com.misxy.movietheater.models.TicketRequest
import com.misxy.movietheater.services.MovieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.function.Function


@Controller
class MovieTheaterController {
    @Autowired
    private lateinit var movieService: MovieService

    @MessageMapping("ticket.cancel")
    fun cancelTicket(request: Mono<TicketRequest>) {
        request
            .doOnNext {
                it.status = TicketStatus.TICKET_CANCELLED
                println("cancelled Ticket ID: ${it.requestID} | Status: ${it.status}")
            }.subscribe()
    }

    @MessageMapping("ticket.purchase")
    fun purchaseTicket(request: Mono<TicketRequest>): Mono<TicketRequest> {
        return request
            .doOnNext { t -> t.status = TicketStatus.TICKET_ISSUED }
            .doOnNext { t ->
                println(
                    "purchaseTicket :: " + t.requestID + " : " + t.status
                )
            }
    }

    @MessageMapping("movie.stream")
    fun playMovie(request: Mono<TicketRequest>): Flux<MovieScene> {
        return request
            .map { t ->
                if (t.status == TicketStatus.TICKET_ISSUED) this.movieService.getScenes() else emptyList()
            }
            .flatMapIterable(Function.identity())
            .cast(MovieScene::class.java)
            .delayElements(Duration.ofSeconds(1))
    }

}
