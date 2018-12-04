package vadc.heartbeat.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import vadc.heartbeat.config.MessagingConfig
import vadc.heartbeat.config.ServiceConfig
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.repository.IncomingEventRepository

@Service
class EventOutProcessor: MessageListener {
    private val log = LoggerFactory.getLogger(EventOutProcessor::class.java)

    @Autowired
    lateinit var incomingEventRepository: IncomingEventRepository

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    lateinit var jmsTemplate: JmsTemplate

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val incomingEvent = redisTemplate.opsForList().rightPop(ServiceConfig.incomingEventList) ?: return
        log.info("Received incoming event for processing $incomingEvent")
        val event = incomingEventRepository.findById(incomingEvent)
        if (event.isPresent) {
            jmsTemplate.convertAndSend(MessagingConfig.NOTIFICATION_QUEUE, event.get().payload)
            event.get().state = IncomingEvent.State.PROCESSED
            incomingEventRepository.save(event.get())
            log.info("Processed ${event.get()}")
        }
    }

}