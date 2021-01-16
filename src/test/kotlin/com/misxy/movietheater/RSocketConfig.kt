package com.misxy.movietheater

import io.rsocket.transport.netty.client.TcpClientTransport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Configuration
class RSocketConfig {

    @Bean
    fun rSocketStrategies(): RSocketStrategies {
        return RSocketStrategies
            .builder()
            .encoders { it.add(Jackson2CborEncoder()) }
            .decoders { it.add(Jackson2CborDecoder()) }
            .build()
    }

    @Bean
    fun getRSocketRequester(builder: RSocketRequester.Builder): RSocketRequester {
        return builder.rsocketConnector { it.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))) }
            .dataMimeType(MediaType.APPLICATION_CBOR)
            .transport(TcpClientTransport.create(6565))
    }
}
