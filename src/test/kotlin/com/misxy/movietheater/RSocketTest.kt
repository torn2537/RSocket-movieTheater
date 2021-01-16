package com.misxy.movietheater

import com.misxy.movietheater.enums.TicketStatus
import com.misxy.movietheater.models.MovieScene
import com.misxy.movietheater.models.TicketRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.UUID


@SpringBootTest
class RSocketTest {

    @Autowired
    private lateinit var rSocketRequester: RSocketRequester

    @Test
    fun ticketCancel() {
        val mono: Mono<Void> = Mono.just(this.rSocketRequester)
            .map {
                it.route("ticket.cancel")
                    .data(TicketRequest(UUID.randomUUID()))
            }
            .flatMap { it.send() }

        StepVerifier.create(mono).verifyComplete()
    }

    @Test
    fun ticketPurchase() {
        val ticketRequestMono: Mono<TicketRequest> = Mono.just(rSocketRequester)
            .map { it.route("ticket.purchase").data(TicketRequest(UUID.randomUUID())) }
            .flatMap { it.retrieveMono(TicketRequest::class.java) }
            .doOnNext { println("Purchased: ${it.requestID} | ${it.status}") }

        StepVerifier.create(ticketRequestMono)
            .expectNextMatches { t: TicketRequest -> t.status == TicketStatus.TICKET_ISSUED }
            .verifyComplete()
    }

    @Test
    fun playMovie() {
        val ticketRequestMono: Mono<TicketRequest> = Mono.just(rSocketRequester)
            .map { r -> r.route("ticket.purchase").data(TicketRequest(UUID.randomUUID())) }
            .flatMap { r -> r.retrieveMono(TicketRequest::class.java) }

        val sceneFlux: Flux<MovieScene> = Mono.just(rSocketRequester)
            .zipWith(ticketRequestMono)
            .map { it.t1.route("movie.stream").data(it.t2) }
            .flatMapMany { it.retrieveFlux(MovieScene::class.java) }
            .doOnNext { println("Playing on scene: ${it.sceneDescription} | Scene ID: ${it.sceneId}") }

        // assert all the movie scenes
        StepVerifier.create(sceneFlux)
            .expectNextMatches { it.sceneDescription == "First scene" }
            .expectNextMatches { it.sceneDescription == "Second scene" }
            .expectNextMatches { it.sceneDescription == "Third scene" }
            .expectNextMatches { it.sceneDescription == "Fourth scene" }
            .expectNextMatches { it.sceneDescription == "Fifth scene" }
            .verifyComplete()
    }

    @Test
    fun tvPlayMovie(){
        val movieSceneFlux: Flux<Int> = Flux.just(1, 2, 2, 1, 2, 3, 3, 4, 5)
        val tvFlux: Flux<MovieScene> = Mono.just(this.rSocketRequester)
            .map { it.route("tv.movie").data(movieSceneFlux) }
            .flatMapMany { it.retrieveFlux(MovieScene::class.java) }
            .doOnNext { println("TV: ${it.sceneDescription}") }

        StepVerifier.create(tvFlux)
            .expectNextMatches { it.sceneDescription == "First scene" }
            .expectNextMatches { it.sceneDescription == "Second scene" }
            .expectNextMatches { it.sceneDescription == "Second scene" }
            .expectNextMatches { it.sceneDescription == "First scene" }
            .expectNextMatches { it.sceneDescription == "Second scene" }
            .expectNextMatches { it.sceneDescription == "Third scene" }
            .expectNextMatches { it.sceneDescription == "Third scene" }
            .expectNextMatches { it.sceneDescription == "Fourth scene" }
            .expectNextMatches { it.sceneDescription == "Fifth scene" }
            .verifyComplete()
    }
}
