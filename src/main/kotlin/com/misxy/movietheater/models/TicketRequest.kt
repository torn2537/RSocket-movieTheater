package com.misxy.movietheater.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.misxy.movietheater.enums.TicketStatus
import java.util.*

data class TicketRequest(
    @JsonProperty("requestID")val requestID: UUID
)
{
   var status: TicketStatus = TicketStatus.TICKET_PENDING
}
