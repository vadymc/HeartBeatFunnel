package vadc.heartbeat.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.domain.ShadowIncomingEvent
import vadc.heartbeat.repository.IncomingEventRepository
import vadc.heartbeat.repository.ShadowIncomingEventRepository
import java.util.*

@Service
class ExpiredMessageListener: MessageListener {

    private val log = LoggerFactory.getLogger(ExpiredMessageListener::class.java)

    private var prefix = "ShadowIncomingEvent:shadow:"

    @Autowired
    private lateinit var incomingEventRepository: IncomingEventRepository

    @Autowired
    private lateinit var shadowIncomingEventRepository: ShadowIncomingEventRepository

    @Autowired
    private lateinit var eventInProcessor: EventInProcessor

    override fun onMessage(message: Message, pattern: ByteArray?) {
        log.info("Expired $message")
        if (isShadowIncomingMessage(message)) {
            val id = extractId(message)
            val optionalEvent = incomingEventRepository.findById(id)
            if (optionalEvent.isPresent) {
                if (isUnprocessed(optionalEvent)) {
                    eventInProcessor.submitForProcessing(id)
                    shadowIncomingEventRepository.save(ShadowIncomingEvent(id))
                    log.info("Submitted ${optionalEvent.get()} for reprocessing")
                } else {
                    incomingEventRepository.deleteById(id)
                    log.info("Deleted processed ${optionalEvent.get()}")
                }
            }
        }
    }

    private fun isShadowIncomingMessage(message: Message): Boolean =
            message.toString().startsWith(prefix)

    private fun extractId(message: Message) =
            message.toString().replace(prefix, "")

    private fun isUnprocessed(optionalEvent: Optional<IncomingEvent>) =
            optionalEvent.get().state == IncomingEvent.State.UNPROCESSED

}