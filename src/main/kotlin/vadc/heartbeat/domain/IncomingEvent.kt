package vadc.heartbeat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import vadc.heartbeat.domain.IncomingEvent.State.UNPROCESSED
import java.io.Serializable

@RedisHash(value = "IncomingEvent")
data class IncomingEvent(@Id val id: String,
                         val payload: String,
                         var state: State = UNPROCESSED): Serializable {
    enum class State {
        UNPROCESSED, PROCESSED
    }
}