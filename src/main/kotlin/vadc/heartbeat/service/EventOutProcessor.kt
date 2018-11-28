package vadc.heartbeat.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.repository.IncomingEventRepository

@Service
class EventOutProcessor: MessageListener {
    private val log = LoggerFactory.getLogger(EventOutProcessor::class.java)

    @Autowired
    lateinit var incomingEventRepository: IncomingEventRepository

    override fun onMessage(message: Message, pattern: ByteArray?) {
        log.info("Received incoming event for processing $message")
        val event = incomingEventRepository.findById(message.toString())
        if (event.isPresent) {
            event.get().state = IncomingEvent.State.PROCESSED
            incomingEventRepository.save(event.get())
            log.info("Processed ${event.get()}")
        }
    }

}