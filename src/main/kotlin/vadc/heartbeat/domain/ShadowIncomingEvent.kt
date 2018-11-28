package vadc.heartbeat.domain

import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "ShadowIncomingEvent", timeToLive = 10)
data class ShadowIncomingEvent(var id: String) {
    init {
        this.id = "shadow:$id"
    }
}
