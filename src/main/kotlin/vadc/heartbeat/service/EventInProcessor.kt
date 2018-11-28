package vadc.heartbeat.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import vadc.heartbeat.config.ServiceConfig
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.domain.ShadowIncomingEvent
import vadc.heartbeat.repository.IncomingEventRepository
import vadc.heartbeat.repository.ShadowIncomingEventRepository
import java.util.*

@Service
class EventInProcessor {

    private val log = LoggerFactory.getLogger(EventInProcessor::class.java)

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    lateinit var incomingEventRepository: IncomingEventRepository

    @Autowired
    lateinit var shadowIncomingEventRepository: ShadowIncomingEventRepository

    fun submit(payload: String): IncomingEvent {
        val event = IncomingEvent(UUID.randomUUID().toString(), payload)
        incomingEventRepository.save(event)
        shadowIncomingEventRepository.save(ShadowIncomingEvent(event.id))
        submitForProcessing(event.id)
        log.info("Stored $event")
        return event
    }

    fun submitForProcessing(event: String) {
        redisTemplate.convertAndSend(ServiceConfig.incomingEventTopic, event)
    }

}